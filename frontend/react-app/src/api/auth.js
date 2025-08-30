import api from './api';

export async function signUp(userData) {
  const response = await api.post('/users', userData);
  return response.data; 
}

export async function signIn(username, password) {
  const response = await api.post('/auth', {
        username: username,
        password: password,
    },
  );
  return response.data;
}

export async function signInWithJwt(token) {
  const response = await api.get('/auth/jwt', {
    headers: {
      Authorization: `${token}`,
    },
  });
  return response.data;
}

export async function getUserInfoById(id) {
  const response = await api.get(`/users/${id}`)
  return response.data
}

export async function updateUser(id, userData) {
  const response = await api.patch(`/users/${id}`, userData);
  return response.data;
}

export async function deleteUser(id) {
  const response = await api.delete(`/users/${id}`);
  return response.data;
}
