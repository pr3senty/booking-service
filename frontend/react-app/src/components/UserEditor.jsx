import React, { useState } from 'react';
import "./UserEditor.css"

export default function UserEditor({ user, onSave, onDelete, onCancel }) {
  const [form, setForm] = useState({
    role: user.role || 'GUEST',
    surname: user.surname || '',
    personName: user.personName || '',
    patronymic: user.patronymic || ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(form);
  };

  return (
    <form onSubmit={handleSubmit} className="editor-form">
      <label>
        Роль:
        <select name="role" value={form.role} onChange={handleChange}>
          <option value="GUEST">Гость</option>
          <option value="STUDENT">Студент</option>
          <option value="STAFF">Сотрудник</option>
          <option value="ADMIN">Администратор</option>
        </select>
      </label>

      <label>
        Фамилия:
        <input name="surname" value={form.surname} onChange={handleChange} required />
      </label>

      <label>
        Имя:
        <input name="personName" value={form.personName} onChange={handleChange} required />
      </label>

      <label>
        Отчество:
        <input name="patronymic" value={form.patronymic} onChange={handleChange} required />
      </label>

      <button type="submit">Сохранить</button>
      <button type="button" onClick={onCancel}>Отмена</button>
      <button type="button" onClick={onDelete} className='delete-btn'>Удалить</button>
    </form>
  );
}
