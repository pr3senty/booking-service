package edu.centraluniversity.app.repository;

import edu.centraluniversity.app.model.database.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByCoworkingId(Long coworkingId);

    List<Booking> findAllByUserId(Long userId);
}
