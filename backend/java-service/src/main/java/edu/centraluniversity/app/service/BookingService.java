package edu.centraluniversity.app.service;


import edu.centraluniversity.Model;
import edu.centraluniversity.app.model.BookingDto;
import edu.centraluniversity.app.model.CoworkingDto;
import edu.centraluniversity.app.model.UserDto;
import edu.centraluniversity.app.model.UserInfoDto;
import edu.centraluniversity.app.model.database.Booking;
import edu.centraluniversity.app.model.database.Coworking;
import edu.centraluniversity.app.model.exception.HttpStatusException;
import edu.centraluniversity.app.repository.BookingRepository;
import edu.centraluniversity.app.repository.CoworkingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CoworkingRepository coworkingRepository;
    private final AuthService authService;

    public BookingDto[] getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        BookingDto[] bookingDtos = new BookingDto[bookings.size()];
        for (int i = 0; i < bookingDtos.length; i++) {
            bookingDtos[i] = getBookingById(bookings.get(i).getId());
        }

        return bookingDtos;
    }

    public BookingDto[] getAllCoworkingBookings(Long coworkingId) {
        List<Booking> bookings = bookingRepository.findAllByCoworkingId(coworkingId);

        BookingDto[] bookingDtos = new BookingDto[bookings.size()];
        for (int i = 0; i < bookingDtos.length; i++) {
            bookingDtos[i] = getBookingById(bookings.get(i).getId());
        }

        return bookingDtos;
    }

    public BookingDto[] getAllUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findAllByUserId(userId);

        BookingDto[] bookingDtos = new BookingDto[bookings.size()];
        for (int i = 0; i < bookingDtos.length; i++) {
            bookingDtos[i] = getBookingById(bookings.get(i).getId());
        }

        return bookingDtos;
    }

    public BookingDto getBookingById(Long id) {

        if (!bookingRepository.existsById(id)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, String.format("Booking with id %d not found", id));
        }

        Booking booking = bookingRepository.findById(id).get();
        Coworking coworking = coworkingRepository.findById(booking.getCoworkingId()).get();
        UserInfoDto ownerInfo = authService.getUserInfo(booking.getUserId());

        return new BookingDto(booking, ownerInfo, new CoworkingDto(coworking));
    }

    public BookingDto createBooking(long coworkingId, BookingDto bookingDto) {

        checkBooking(coworkingId, bookingDto.getUserId(), bookingDto.getStartTime(), bookingDto.getEndTime());

        bookingDto.setCoworkingId(coworkingId);
        Booking newBooking = new Booking(bookingDto);

        Booking saved = bookingRepository.save(newBooking);

        return getBookingById(saved.getId());
    }

    public BookingDto patchBooking(long bookingId, BookingDto bookingDto) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Booking with id " + bookingId + " not found"));

        if (bookingDto.getCoworkingId() != null) {
            booking.setCoworkingId(bookingDto.getCoworkingId());
        }

        if (!bookingDto.getDescription().isBlank()) {
            booking.setDescription(bookingDto.getDescription());
        }

        if (bookingDto.getStartTime() != null) {
            booking.setStartTime(bookingDto.getStartTime().atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        }

        if (bookingDto.getEndTime() != null) {
            booking.setEndTime(bookingDto.getEndTime().atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        }

        checkBooking(booking.getCoworkingId(), booking.getUserId(), bookingDto.getStartTime(), bookingDto.getEndTime());

        bookingRepository.save(booking);

        return getBookingById(bookingId);
    }

    public void checkBooking(long coworkingId, long userId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {

        Coworking coworking = coworkingRepository.findById(coworkingId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, String.format("Coworking with id %d not found", coworkingId)));

        UserInfoDto userInfo = authService.getUserInfo(userId);
        if (coworking.getRoleRequired().ordinal() > userInfo.getRole().ordinal()) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "You are not allowed to booking this coworking");
        }

        LocalDateTime localStartTime = startDateTime.atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime localEndTime = endDateTime.atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        if (localEndTime.isBefore(localStartTime)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "End time must be before start time");
        }

        if (localStartTime.isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Start time must be in future time");
        }

        List<Booking> bookings = bookingRepository.findAllByCoworkingId(coworkingId);
        for (Booking booking : bookings) {
            if (booking.getStartTime().isBefore(localStartTime) && booking.getEndTime().isAfter(localStartTime)) {
                throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Already exists a booking for this coworking at the same time");
            }
        }
    }

    public void deleteBooking(Long bookingId) {

        if (!bookingRepository.existsById(bookingId)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, String.format("Booking with id %s not found", bookingId));
        }

        bookingRepository.deleteById(bookingId);
    }

}
