// Клик по строке таблицы с data-href → переход на адрес редактирования.
(() => {
  document.querySelectorAll('tr.clickable-row[data-href]').forEach((row) => {
    row.addEventListener('click', () => {
      window.location.href = row.getAttribute('data-href');
    });
  });
})();
