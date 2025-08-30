package edu.centraluniversity.app.model.database;

import edu.centraluniversity.app.model.BookingDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.*;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime startTime;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long coworkingId;

    public Booking(BookingDto bookingDto) {
        this.description = bookingDto.getDescription();
        this.startTime = bookingDto.getStartTime().atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        this.endTime = bookingDto.getEndTime().atZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        this.userId = bookingDto.getUserId();
        this.coworkingId = bookingDto.getCoworkingId();
    }
}
