(() => {
  const input = document.getElementById('ai-input');
  const sendBtn = document.getElementById('ai-send');
  const history = document.getElementById('ai-history');
  if (!input || !sendBtn || !history) return;

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  sendBtn.addEventListener('click', async () => {
    const message = input.value.trim();
    if (!message) return;

    appendMessage('Вы', message);
    sendBtn.disabled = true;
    input.value = '';

    const thinking = appendThinking();

    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

      const resp = await fetch('/api/ai/chat', {
        method: 'POST',
        headers,
        body: JSON.stringify({ message })
      });
      thinking.remove();
      if (!resp.ok) {
        appendMessage('ИИ', 'Ошибка: HTTP ' + resp.status);
        return;
      }
      const data = await resp.json();
      appendMessage('ИИ', data.response || 'Не удалось получить ответ');
    } catch (e) {
      thinking.remove();
      appendMessage('ИИ', 'Ошибка: ' + e.message);
    } finally {
      sendBtn.disabled = false;
    }
  });

  function appendMessage(author, text) {
    const div = document.createElement('div');
    div.className = 'mb-2';
    const strong = document.createElement('strong');
    strong.textContent = author + ': ';
    div.appendChild(strong);
    div.appendChild(document.createTextNode(text));
    history.appendChild(div);
    div.scrollIntoView({ block: 'nearest' });
    return div;
  }

  /** Индикатор «AI думает…» со спиннером, возвращает узел для последующего удаления. */
  function appendThinking() {
    const div = document.createElement('div');
    div.className = 'mb-2 text-muted d-flex align-items-center';
    div.innerHTML =
      '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>' +
      '<span>ИИ думает…</span>';
    history.appendChild(div);
    div.scrollIntoView({ block: 'nearest' });
    return div;
  }
})();
