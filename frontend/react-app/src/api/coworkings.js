import api from './api'; 


export const getAllCoworkings = async (floor, role) => {
  const params = {};
  if (floor !== undefined) params.floor = floor;
  if (role !== undefined) params.role = role;

  const res = await api.get('/coworkings', { params });
  return res.data;
};

export const getCoworkingById = async (id) => {
  const res = await api.get(`/coworkings/${id}`);
  return res.data;
};

export const createCoworking = async (coworkingDto) => {
  const res = await api.post('/coworkings', coworkingDto);
  return res.data;
};

export const updateCoworking = async (id, coworkingDto) => {
  const res = await api.patch(`/coworkings/${id}`, coworkingDto);
  return res.data;
};

export const deleteCoworking = async (id) => {
  await api.delete(`/coworkings/${id}`);
};
