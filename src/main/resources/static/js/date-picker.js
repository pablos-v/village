// Открывать нативный календарь по клику на всё поле даты, а не только на иконку.
(() => {
  document.querySelectorAll('input[type="date"]').forEach((input) => {
    input.addEventListener('click', () => {
      if (typeof input.showPicker === 'function') {
        input.showPicker();
      }
    });
  });
})();
