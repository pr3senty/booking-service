import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { useAuth } from '../utils/useAuth';
import { useAsyncState } from '../utils/useAsyncState';
import { getCoworkingBookings, createBooking } from '../api/bookings';
import BookingEditor from '../components/BookingEditor';
import "./CoworkingPage.css"

export default function CoworkingDetails() {
  const { id } = useParams();
  const { user, loading: authLoading } = useAuth();

  const { data: bookings, setData: setBookings, error, setError, loading, load } = useAsyncState([]);

  const [adding, setAdding] = useState(false);

  useEffect(() => {
    if (!authLoading) {
      load(() => getCoworkingBookings(id));
    }
  }, [authLoading, id]);

  const addBooking = async (newData) => {
    setError('');
    await createBooking(id, {
      ...newData,
      userId: user.id,
      coworkingId: Number(id),
    });
    const updated = await getCoworkingBookings(id);
    setBookings(updated);
    setAdding(false);
  };

  if (authLoading || loading) return (
    <>
      <Navbar />
      <main>Загрузка...</main>
    </>
  );

  return (
    <>
      <Navbar />
      <main>
        <h1>Бронирования коворкинга #{id}</h1>
        {error && <p className="error">{error}</p>}

        <button onClick={() => setAdding(true)} disabled={adding}>Добавить бронь</button>

        {adding && (
          <BookingEditor
            booking={{ startTime: '', endTime: '', description: '' }}
            onSave={addBooking}
            onCancel={() => setAdding(false)}
          />
        )}

        <ul className="bookings-list">
          {[...bookings]
              .filter(b => new Date(b.endTime) > new Date())
              .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
              .map(b => (
            <li key={b.id}>
              <p><strong>Владелец:</strong> {b.userInfo.userId == user.id && 'Вы,'} {b.userInfo.surname} {b.userInfo.personName}</p>
              <p><strong>Начало:</strong> {new Date(b.startTime).toLocaleString()}</p>
              <p><strong>Конец:</strong> {new Date(b.endTime).toLocaleString()}</p>
              <p><strong>Описание:</strong> {b.description || '—'}</p>
            </li>
          ))}
        </ul>
      </main>
    </>
  );
}
