package edu.centraluniversity.app.controller;

import edu.centraluniversity.app.model.BookingDto;
import edu.centraluniversity.app.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name="Booking", description="Booking API")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary="Получение всех броней. А также отдельно по коворкингу или пользователю")
    @GetMapping("/bookings")
    public BookingDto[] getAllBookings(
            @RequestParam(value = "coworkingId", required = false) Long coworkingId,
            @RequestParam(value = "userId", required = false) Long userId
    ) {

        if (coworkingId != null) {
            return bookingService.getAllCoworkingBookings(coworkingId);
        } else if (userId != null) {
            return bookingService.getAllUserBookings(userId);
        } else {
            return bookingService.getAllBookings();
        }
    }

    @Operation(summary="Получение брони по её ID")
    @GetMapping("/bookings/{id}")
    public BookingDto getBookingById(@PathVariable("id") long id) {
        return bookingService.getBookingById(id);
    }

    @Operation(summary="Создание новой брони на коворкинг")
    @PostMapping("coworkings/{coworkingId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createNewCoworkingBooking(@PathVariable long coworkingId, @Valid @RequestBody BookingDto booking) {
        return bookingService.createBooking(coworkingId, booking);
    }

    @Operation(summary="Обновление брони")
    @PatchMapping("/bookings/{bookingId}")
    public BookingDto patchCoworkingBooking(@PathVariable long bookingId, @RequestBody BookingDto booking) {

        return bookingService.patchBooking(bookingId, booking);
    }

    @Operation(summary="Удаление брони")
    @DeleteMapping("/bookings/{bookingId}")
    public void deleteCoworkingBooking(@PathVariable long bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}
