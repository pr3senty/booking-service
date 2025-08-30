package edu.centraluniversity.app.model;

import edu.centraluniversity.Model;
import edu.centraluniversity.app.model.database.Booking;
import edu.centraluniversity.app.model.database.Coworking;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;

    @NotEmpty
    private String description;

    @NotNull
    private OffsetDateTime startTime;

    @NotNull
    private OffsetDateTime endTime;

    @NotNull
    private Long userId;

    private Long coworkingId;

    private CoworkingDto coworking;

    private UserInfoDto userInfo;

    public BookingDto(Booking booking, UserInfoDto userInfo) {
        this.id = booking.getId();
        this.userId = booking.getUserId();
        this.coworkingId = booking.getCoworkingId();
        this.description = booking.getDescription();
        this.startTime = OffsetDateTime.of(booking.getStartTime(), ZoneOffset.UTC);
        this.endTime = OffsetDateTime.of(booking.getEndTime(), ZoneOffset.UTC);
        this.userInfo = userInfo;
    }

    public BookingDto(Booking booking, UserInfoDto userInfo, CoworkingDto coworking) {
        this(booking, userInfo);
        this.coworking = coworking;
    }
}
