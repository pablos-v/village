(() => {
  const input = document.getElementById('address-search');
  const suggestions = document.getElementById('address-suggestions');
  const hidden = document.getElementById('household-id');
  if (!input || !suggestions || !hidden) return;

  let timer;

  input.addEventListener('input', () => {
    clearTimeout(timer);
    timer = setTimeout(async () => {
      const q = input.value.trim();
      const resp = await fetch(`/api/addresses?q=${encodeURIComponent(q)}`);
      const data = await resp.json();
      suggestions.innerHTML = '';
      data.forEach(a => {
        const li = document.createElement('li');
        li.textContent = a.label;
        li.style.cursor = 'pointer';
        li.addEventListener('click', () => {
          input.value = a.label;
          hidden.value = a.householdId;
          suggestions.innerHTML = '';
        });
        suggestions.appendChild(li);
      });
    }, 300);
  });
})();
