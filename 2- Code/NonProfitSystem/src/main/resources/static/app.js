'use strict';

// Disable inactive fields so only the selected form variant is submitted.
function bindVariants(formSelector, selectSelector, fieldsAttribute) {
  document.querySelectorAll(formSelector).forEach(form => {
    const select = form.querySelector(selectSelector);
    const update = () => {
      form.querySelectorAll(`[${fieldsAttribute}]`).forEach(fields => {
        const active = fields.getAttribute(fieldsAttribute).split(' ').includes(select.value);
        fields.hidden = !active;
        fields.disabled = !active;
      });
    };
    select.addEventListener('change', update);
    update();
  });
}
bindVariants('[data-donation-form]', '[data-donation-type]', 'data-donation-fields');
bindVariants('[data-event-form]', '[data-event-type]', 'data-event-fields');
bindVariants('[data-operation-form]', '[data-operation]', 'data-operation-fields');

document.querySelectorAll('form[data-confirm]').forEach(form => {
  form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});
document.querySelectorAll('[data-print]').forEach(button => {
  button.addEventListener('click', () => window.print());
});
