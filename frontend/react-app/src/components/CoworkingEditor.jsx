import React, { useState } from 'react';
import "./CoworkingEditor.css"

export default function CoworkingEditor({ coworking, onSave, onDelete }) {
  const [form, setForm] = useState({
    label: coworking.label || '',
    floor: coworking.floor || '',
    occupancy: coworking.occupancy || '',
    roleRequired: coworking.roleRequired || 'GUEST',
  });

  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: name === 'floor' || name === 'occupancy' ? Number(value) : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!form.label.trim()) {
      setError('Название коворкинга обязательно');
      return;
    }
    if (form.floor < 0) {
      setError('Этаж не может быть отрицательным');
      return;
    }
    if (form.occupancy <= 0) {
      setError('Вместимость должна быть положительным числом');
      return;
    }
    try {
      await onSave(form);
    } catch (err) {
      setError(err.response?.data?.errorMessage || 'Ошибка сохранения');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Вы уверены, что хотите удалить этот коворкинг?')) {
      try {
        await onDelete();
      } catch (err) {
        setError(err.response?.data?.errorMessage || 'Ошибка удаления');
      }
    }
  };

  return (
    <div className="coworking-editor">
      <form onSubmit={handleSubmit} className="coworking-form">
        <label>
          Название:
          <input name="label" value={form.label} onChange={handleChange} required />
        </label>
        <label>
          Этаж:
          <input name="floor" type="number" value={form.floor} onChange={handleChange} required min="0" />
        </label>
        <label>
          Вместимость:
          <input name="occupancy" type="number" value={form.occupancy} onChange={handleChange} required min="1" />
        </label>
        <label>
          Роль:
          <select name="roleRequired" value={form.roleRequired} onChange={handleChange}>
            <option value="GUEST">Гость</option>
            <option value="STUDENT">Студент</option>
            <option value="STAFF">Сотрудник</option>
            <option value="ADMIN">Администратор</option>
          </select>
        </label>
        {error && <p className="error">{error}</p>}
        <div className="buttons">
          <button type="submit">Сохранить</button>
          {onDelete && (
            <button
              type="button"
              className="delete-btn"
              onClick={handleDelete}
            >
              Удалить
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
