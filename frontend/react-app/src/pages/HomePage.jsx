import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getAllCoworkings } from '../api/coworkings';
import "./HomePage.css"

const roles = ['GUEST', 'STUDENT', 'STAFF', 'ADMIN'];
const roleLabels = {
  GUEST: 'Гость',
  STUDENT: 'Студент',
  STAFF: 'Сотрудник',
  ADMIN: 'Администратор',
};

export default function HomePage() {
  const [coworkings, setCoworkings] = useState([]);
  const [floor, setFloor] = useState('');
  const [role, setRole] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const loadCoworkings = async () => {
    setError('');
    try {
      const data = await getAllCoworkings(floor || undefined, role || undefined);
      setCoworkings(data);
    } catch (e) {
      setError('Ошибка загрузки коворкингов');
    }
  };

  useEffect(() => {
    loadCoworkings();
  }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    loadCoworkings();
  };

  return (
    <>
      <Navbar />
      <main className="home-container">
        <h1>Коворкинги</h1>

        <form onSubmit={handleFilter} className="filter-form">
          <label>
            Этаж:
            <input
              type="number"
              value={floor}
              onChange={(e) => setFloor(e.target.value)}
              placeholder="Например, 1"
              min="1"
            />
          </label>

          <label>
            Роль:
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="">Все</option>
              {roles.map((r) => (
                <option key={r} value={r}>
                  {roleLabels[r]}
                </option>
              ))}
            </select>
          </label>

          <button type="submit">Фильтровать</button>
        </form>

        {error && <p className="error-message">{error}</p>}

        {coworkings.length === 0 && !error && (
          <p className="empty-message">Коворкинги не найдены</p>
        )}

        <ul className="coworking-list">
        {coworkings.map((c) => (
            <li
            key={c.id}
            className="coworking-card"
            onClick={() => navigate(`/coworkings/${c.id}`)}
            style={{ cursor: 'pointer' }}
            role="button"
            tabIndex={0}
            onKeyPress={(e) => { if (e.key === 'Enter') navigate(`/coworkings/${c.id}`); }}
            >
            <h3>{c.label}</h3>
            <p><strong>Этаж:</strong> {c.floor}</p>
            <p><strong>Требуемая роль:</strong> {roleLabels[c.roleRequired]}</p>
            <p><strong>Вместимость:</strong> {c.occupancy}</p>
            <p><strong>ID:</strong> {c.id}</p>
            </li>
        ))}
        </ul>
      </main>
    </>
  );
}
