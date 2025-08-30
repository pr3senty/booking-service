import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getUserBookings, updateBooking, deleteBooking } from '../api/bookings';
import { useAuth } from '../utils/useAuth';
import { useAsyncState } from '../utils/useAsyncState';
import BookingEditor from '../components/BookingEditor';


export default function MyBookings() {
  const navigate = useNavigate();
  const { user, loading: authLoading } = useAuth();

  const { data: bookings, setData: setBookings, error, setError, loading, load } = useAsyncState([]);
  const [filterLabel, setFilterLabel] = useState('');
  const [filterFloor, setFilterFloor] = useState('');
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    if (!authLoading && user) {
      load(() => getUserBookings(user.id)).catch(() => {
        localStorage.setItem('authMessage', 'Ошибка загрузки броней');
      });
    }
  }, [authLoading, user]);

  const filtered = bookings.filter(b => {
    return (
      (filterLabel === '' || b.coworking?.label.toLowerCase().includes(filterLabel.toLowerCase())) &&
      (filterFloor === '' || String(b.coworking?.floor) === filterFloor)
    );
  });

  const startEditing = (id) => setEditingId(id);
  const cancelEditing = () => setEditingId(null);

  const saveBooking = async (id, updatedData) => {
    setError('');
    await updateBooking(id, updatedData);
    const updated = await getUserBookings(user.id);
    const now = new Date();
    const toSet = updated.filter(booking => new Date(booking.endTime) > now);
    setBookings(toSet);
    cancelEditing();
  };

  const removeBooking = async (id) => {
    setError('');
    try {
      await deleteBooking(id);
      setBookings(bookings.filter(b => b.id !== id));
    } catch {
      setError('Ошибка при удалении брони');
    }
  };

  if (authLoading || loading) return <p>Загрузка...</p>;

  return (
    <>
      <Navbar />
      <main>
        <h1>Мои брони</h1>

        {error && <p className="error">{error}</p>}

        <div className='filter-controls'>
          <input
            type="text"
            placeholder="Название коворкинга"
            value={filterLabel}
            onChange={e => setFilterLabel(e.target.value)}
          />
          <input
            type="number"
            placeholder="Этаж"
            value={filterFloor}
            onChange={e => setFilterFloor(e.target.value)}
          />
        </div>

        {filtered.length === 0 ? (
          <p>Нет броней</p>
        ) : (
          <ul className="bookings-list">
            {[...filtered]
              .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
              .map(b => (
              <li key={b.id}>
                {editingId === b.id ? (
                  <BookingEditor
                    booking={b}
                    onSave={(data) => saveBooking(b.id, data)}
                    onCancel={cancelEditing}
                  />
                ) : (
                  <>
                    <p><strong>Коворкинг:</strong> {b.coworking?.label || '—'} (этаж {b.coworking?.floor ?? '?'})</p>
                    <p><strong>Начало:</strong> {new Date(b.startTime).toLocaleString()}</p>
                    <p><strong>Конец:</strong> {new Date(b.endTime).toLocaleString()}</p>
                    <p><strong>Описание:</strong> {b.description || '—'}</p>
                    <div>
                      <button onClick={() => startEditing(b.id)}>Изменить</button>{' '}
                      <button onClick={() => removeBooking(b.id)}>Удалить</button>
                    </div>
                  </>
                )}
              </li>
            ))}
          </ul>
        )}
      </main>
    </>
  );
}
