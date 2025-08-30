import api from './api';

export async function getCoworkingBookings(coworkingId) {
    
    const response = await api.get(`/bookings?coworkingId=${coworkingId}`);
    return response.data;
}

export async function getUserBookings(userId) {
    
    const response = await api.get(`/bookings?userId=${userId}`);
    return response.data;
}

export async function getBookingById(id) {

    const response = await api.get(`/bookings/${id}`);
    return response.data;
}

export async function createBooking(coworkingId, bookingData) {
    
    const response = await api.post(`/coworkings/${coworkingId}/bookings`, bookingData);
    return response.data;
}

export async function updateBooking(bookingId, bookingData) {

    const response = await api.patch(`/bookings/${bookingId}`, bookingData);
    return response.data;
}

export async function deleteBooking(bookingId) {

    const response = await api.delete(`/bookings/${bookingId}`);
}