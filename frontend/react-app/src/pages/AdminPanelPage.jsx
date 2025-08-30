import React, { useState } from 'react';
import Navbar from '../components/Navbar';
import BookingEditor from '../components/BookingEditor';
import { getBookingById, updateBooking, deleteBooking } from '../api/bookings';
import { getUserInfoById, updateUser, deleteUser } from '../api/auth';
import { getAllCoworkings, createCoworking, updateCoworking, deleteCoworking } from '../api/coworkings';
import CoworkingEditor from '../components/CoworkingEditor';
import UserEditor from '../components/UserEditor';
import "./AdminPanelPage.css"

export default function AdminPanel() {
  const [bookingId, setBookingId] = useState('');
  const [booking, setBooking] = useState(null);
  const [bookingSuccess, setBookingSuccess] = useState('');
  const [bookingError, setBookingError] = useState('');

  const [userId, setUserId] = useState('');
  const [user, setUser] = useState(null);
  const [userSuccess, setUserSuccess] = useState('');
  const [userError, setUserError] = useState('');

  const [coworkings, setCoworkings] = useState([]);
  const [coworkingSuccess, setCoworkingSuccess] = useState('');
  const [coworkingError, setCoworkingError] = useState('');

  const [newCoworking, setNewCoworking] = useState({
    label: '',
    floor: '',
    occupancy: '',
    roleRequired: 'GUEST'
  });

  const findBooking = async () => {
    setBookingSuccess('');
    setBookingError('');
    try {
      const b = await getBookingById(bookingId);
      setBooking(b);
      setBookingSuccess('Бронь загружена');
    } catch {
      setBooking(null);
      setBookingError('Бронь не найдена');
    }
  };

  const findUser = async () => {
    setUserSuccess('');
    setUserError('');
    try {
      const u = await getUserInfoById(userId);
      setUser(u);
      setUserSuccess('Пользователь загружен');
    } catch {
      setUser(null);
      setUserError('Пользователь не найден');
    }
  };

  const loadCoworkings = async () => {
    setCoworkingSuccess('');
    setCoworkingError('');
    try {
      const list = await getAllCoworkings();
      setCoworkings(list);
      setCoworkingSuccess('Коворкинги загружены');
    } catch {
      setCoworkingError('Ошибка загрузки коворкингов');
    }
  };

  const handleCoworkingCreate = async () => {
    setCoworkingSuccess('');
    setCoworkingError('');
    try {
      await createCoworking(newCoworking);
      setNewCoworking({
        label: '',
        floor: '',
        occupancy: '',
        roleRequired: 'GUEST'
      });
      await loadCoworkings();
      setCoworkingSuccess('Коворкинг добавлен');
    } catch {
      setCoworkingError('Ошибка при добавлении коворкинга');
    }
  };

  return (
    <>
      <Navbar />
      <main className="admin-panel">
        <h1>Админ-панель</h1>

        <section className="panel-section">
          <h2>Найти бронь по ID</h2>
          <input
            value={bookingId}
            onChange={e => setBookingId(e.target.value)}
            placeholder="ID брони"
            className="input-field"
          />
          <button onClick={findBooking} className="btn-primary">Найти</button>

          {bookingSuccess && <p className="success-message">{bookingSuccess}</p>}
          {bookingError && <p className="error-message">{bookingError}</p>}

          {booking && (
            <BookingEditor
              booking={booking}
              onSave={async (data) => {
                await updateBooking(bookingId, data);
                setBookingSuccess('Бронь обновлена');
                setBookingError('');
              }}
              onDelete={async () => {
                await deleteBooking(bookingId);
                setBooking(null);
                setBookingSuccess('Бронь удалена');
                setBookingError('');
              }}
              onCancel={() => {
                setBooking(null);
                setBookingSuccess('');
                setBookingError('');
              }}
            />
          )}
        </section>

        <section className="panel-section">
          <h2>Найти пользователя по ID</h2>
          <input
            value={userId}
            onChange={e => setUserId(e.target.value)}
            placeholder="ID пользователя"
            className="input-field"
          />
          <button onClick={findUser} className="btn-primary">Найти</button>

          {userSuccess && <p className="success-message">{userSuccess}</p>}
          {userError && <p className="error-message">{userError}</p>}

          {user && (
            <UserEditor
              user={user}
              fields={{
                role: user.role,
                surname: user.surname,
                personName: user.personName,
                patronymic: user.patronymic
              }}
              onSave={async (data) => {
                await updateUser(userId, data);
                setUserSuccess('Пользователь обновлён');
                setUserError('');
              }}
              onDelete={async () => {
                await deleteUser(userId);
                setUser(null);
                setUserSuccess('Пользователь удалён');
                setUserError('');
              }}
              onCancel={() => {
                setUser(null);
                setUserSuccess('');
                setUserError('');
              }}
            />
          )}
        </section>

        <section className="panel-section">
          <h2>Коворкинги</h2>
          <button onClick={loadCoworkings} className="btn-primary">Загрузить</button>

          {coworkingSuccess && <p className="success-message">{coworkingSuccess}</p>}
          {coworkingError && <p className="error-message">{coworkingError}</p>}

          {coworkings.map(cw => (
            <CoworkingEditor
              key={cw.id}
              coworking={cw}
              onSave={async (data) => {
                await updateCoworking(cw.id, data);
                await loadCoworkings();
                setCoworkingSuccess('Коворкинг обновлён');
                setCoworkingError('');
              }}
              onDelete={async () => {
                await deleteCoworking(cw.id);
                await loadCoworkings();
                setCoworkingSuccess('Коворкинг удалён');
                setCoworkingError('');
              }}
            />
          ))}

          <div className="new-coworking">
            <h3>Добавить коворкинг</h3>
            <input
              placeholder="Название"
              value={newCoworking.label}
              onChange={e => setNewCoworking(prev => ({ ...prev, label: e.target.value }))}
              className="input-field"
            />
            <input
              type="number"
              placeholder="Этаж"
              value={newCoworking.floor}
              onChange={e => setNewCoworking(prev => ({ ...prev, floor: Number(e.target.value) }))}
              className="input-field"
            />
            <input
              type="number"
              placeholder="Вместимость"
              value={newCoworking.occupancy}
              onChange={e => setNewCoworking(prev => ({ ...prev, occupancy: Number(e.target.value) }))}
              className="input-field"
            />
            <select
              value={newCoworking.roleRequired}
              onChange={e => setNewCoworking(prev => ({ ...prev, roleRequired: e.target.value }))}
              className="input-field"
            >
              <option value="GUEST">Гость</option>
              <option value="STUDENT">Студент</option>
              <option value="STAFF">Сотрудник</option>
              <option value="ADMIN">Администратор</option>
            </select>
            <button onClick={handleCoworkingCreate} className="btn-primary">Добавить</button>
          </div>
        </section>
      </main>
    </>
  );
}
