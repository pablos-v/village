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

    try {
      const headers = { 'Content-Type': 'application/json' };
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

      const resp = await fetch('/api/ai/chat', {
        method: 'POST',
        headers,
        body: JSON.stringify({ message })
      });
      if (!resp.ok) {
        appendMessage('AI', 'Ошибка: HTTP ' + resp.status);
        return;
      }
      const data = await resp.json();
      appendMessage('AI', data.response || 'Не удалось получить ответ');
    } catch (e) {
      appendMessage('AI', 'Ошибка: ' + e.message);
    } finally {
      sendBtn.disabled = false;
    }
  });

  function appendMessage(author, text) {
    const div = document.createElement('div');
    const strong = document.createElement('strong');
    strong.textContent = author + ': ';
    div.appendChild(strong);
    div.appendChild(document.createTextNode(text));
    history.appendChild(div);
  }
})();
