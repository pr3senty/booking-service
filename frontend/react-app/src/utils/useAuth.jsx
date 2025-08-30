import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { signInWithJwt } from '../api/auth';

export function useAuth() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadUser() {
      const token = localStorage.getItem('jwtToken');
      if (!token) {
        localStorage.setItem('authMessage', 'Пожалуйста, авторизуйтесь для доступа к приложению.');
        navigate('/login');
        setLoading(false);
        return;
      }
      try {
        const result = await signInWithJwt(token);
        setUser(result.userData);
      } catch {
        localStorage.setItem('authMessage', 'Ваша сессия истекла, необходимо авторизироваться снова!');
        localStorage.removeItem('jwtToken');
        navigate('/login');
      } finally {
        setLoading(false);
      }
    }
    loadUser();
  }, [navigate]);

  return { user, loading };
}
