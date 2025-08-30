import React, { useState } from 'react';
import "./BookingEditor.css"

export default function BookingEditor({ booking, onSave, onCancel, onDelete }) {
  const [form, setForm] = useState({
    startTime: booking.startTime.slice(0, 16),
    endTime: booking.endTime.slice(0, 16),
    description: booking.description || '',
  });

  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (new Date(form.startTime) >= new Date(form.endTime)) {
      setError('Время начала должно быть раньше времени окончания');
      return;
    }
    try {
      await onSave({
        startTime: toIsoWithTimezone(form.startTime),
        endTime: toIsoWithTimezone(form.endTime),
        description: form.description,
      });
    } catch (err) {
      setError(err.response?.data?.errorMessage || 'Ошибка сохранения');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Вы уверены, что хотите удалить эту бронь?')) {
      try {
        await onDelete();
      } catch (err) {
        setError(err.response?.data?.errorMessage || 'Ошибка удаления');
      }
    }
  };

  return (
    <div className='booking-editor'>
      <form onSubmit={handleSubmit} className='editor-form'>
        <label>
          Начало:
          <input type="datetime-local" name="startTime" value={form.startTime} onChange={handleChange} required />
        </label>
        <label>
          Конец:
          <input type="datetime-local" name="endTime" value={form.endTime} onChange={handleChange} required />
        </label>
        <label>
          Описание:
          <textarea name="description" value={form.description} onChange={handleChange} />
        </label>
        {error && <p className="error">{error}</p>}
        <div>
          <button type="submit">Сохранить</button>{' '}
          <button type="button" onClick={onCancel}>Отмена</button>{' '}
          {onDelete && (
            <button type="button" onClick={handleDelete} style={{ marginLeft: '1rem', backgroundColor: '#d9534f', color: '#fff' }}>
              Удалить
            </button>
          )}
        </div>
      </form>
    </div>
  );
}

function toIsoWithTimezone(datetimeLocal) {
  if (!datetimeLocal) return '';

  const date = new Date(datetimeLocal);

  return date.toISOString(); 
}
