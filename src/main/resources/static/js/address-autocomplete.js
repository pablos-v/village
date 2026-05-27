(() => {
  const input = document.getElementById('address-search');
  const suggestions = document.getElementById('address-suggestions');
  const hidden = document.getElementById('household-id');
  if (!input || !suggestions || !hidden) return;

  let timer;

  function hide() { suggestions.classList.remove('show'); }

  input.addEventListener('input', () => {
    clearTimeout(timer);
    timer = setTimeout(async () => {
      const q = input.value.trim();
      const resp = await fetch(`/api/addresses?q=${encodeURIComponent(q)}`);
      const data = await resp.json();
      suggestions.innerHTML = '';
      if (data.length === 0) { hide(); return; }
      data.forEach(a => {
        const li = document.createElement('li');
        const item = document.createElement('button');
        item.type = 'button';
        item.className = 'dropdown-item';
        item.textContent = a.label;
        item.addEventListener('click', () => {
          input.value = a.label;
          hidden.value = a.householdId;
          hide();
        });
        li.appendChild(item);
        suggestions.appendChild(li);
      });
      suggestions.classList.add('show');
    }, 300);
  });

  document.addEventListener('click', (e) => {
    if (e.target !== input) hide();
  });
})();
