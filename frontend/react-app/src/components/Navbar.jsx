import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../utils/useAuth';
import "./Navbar.css"

export default function Navbar() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-links">
        <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''}>
          Главная
        </NavLink>
        <NavLink to="/my-bookings" className={({ isActive }) => isActive ? 'active' : ''}>
          Мои брони
        </NavLink>

        {user?.role === 'ADMIN' && (
          <NavLink to="/admin" className={({ isActive }) => isActive ? 'active' : ''}>
            Админ
          </NavLink>
        )}
      </div>
      <button onClick={handleLogout}>Выйти</button>
    </nav>
  );
}