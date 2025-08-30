package edu.centraluniversity.app.service;

import edu.centraluniversity.app.model.BookingDto;
import edu.centraluniversity.app.model.CoworkingDto;
import edu.centraluniversity.app.model.Role;
import edu.centraluniversity.app.model.database.Coworking;
import edu.centraluniversity.app.model.exception.HttpStatusException;
import edu.centraluniversity.app.repository.CoworkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoworkingService {

    private final CoworkingRepository coworkingRepository;
    private final BookingService bookingService;

    public CoworkingDto[] getAllCoworking() {
        List<Coworking> coworkingList = coworkingRepository.findAll();

        CoworkingDto[] coworkingDtos = new CoworkingDto[coworkingList.size()];
        for (int i = 0; i < coworkingList.size(); i++) {
            coworkingDtos[i] = getCoworkingById(coworkingList.get(i).getId());
        }

        return coworkingDtos;
    }

    public CoworkingDto[] getAllCoworkingsByFloor(int floor) {
        List<Coworking> coworkingList = coworkingRepository.findAllByFloor(floor);

        CoworkingDto[] coworkingDtos = new CoworkingDto[coworkingList.size()];
        for (int i = 0; i < coworkingList.size(); i++) {
            coworkingDtos[i] = getCoworkingById(coworkingList.get(i).getId());
        }

        return coworkingDtos;
    }

    public CoworkingDto[] getAllCoworkingsByRequiredRole(Role requiredRole) {
        List<Coworking> coworkingList = coworkingRepository.findAllByRoleRequired(requiredRole);

        CoworkingDto[] coworkingDtos = new CoworkingDto[coworkingList.size()];
        for (int i = 0; i < coworkingList.size(); i++) {
            coworkingDtos[i] = getCoworkingById(coworkingList.get(i).getId());
        }

        return coworkingDtos;
    }

    public CoworkingDto getCoworkingById(long id) {

        Coworking coworking = coworkingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Coworking with id " + id + " not found"));

        BookingDto[] bookingDtos = bookingService.getAllCoworkingBookings(coworking.getId());

        return new CoworkingDto(coworking, bookingDtos);
    }

    public CoworkingDto getCoworkingByLabel(String label) {

        Coworking coworking = coworkingRepository.findByLabelContaining(label)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Coworking with label " + label + " not found"));

        return getCoworkingById(coworking.getId());
    }

    public CoworkingDto createCoworking(CoworkingDto coworkingDto) {

        Coworking newCoworking = new Coworking(coworkingDto);

        Coworking saved = coworkingRepository.save(newCoworking);

        return getCoworkingById(saved.getId());
    }

    public CoworkingDto updateCoworking(long id, CoworkingDto coworkingDto) {

        Coworking coworking = coworkingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Coworking with id " + id + " not found"));

        BookingDto[] coworkingBookings = bookingService.getAllCoworkingBookings(coworking.getId());
        if (coworkingBookings.length > 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Coworking has bookings");
        }

        if (!coworkingDto.getLabel().isBlank()) {
            coworking.setLabel(coworkingDto.getLabel());
        }

        if (!(coworkingDto.getFloor() == null)) {
            coworking.setFloor(coworkingDto.getFloor());
        }

        if (!(coworkingDto.getOccupancy() == null)) {
            coworking.setOccupancy(coworkingDto.getOccupancy());
        }

        if (coworkingDto.getRoleRequired() != null) {
            coworking.setRoleRequired(coworkingDto.getRoleRequired());
        }

        coworkingRepository.save(coworking);

        return getCoworkingById(id);
    }

    public void deleteCoworking(long id) {

        Coworking coworking = coworkingRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Coworking with id " + id + " not found"));

        BookingDto[] coworkingBookings = bookingService.getAllCoworkingBookings(coworking.getId());
        if (coworkingBookings.length > 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Coworking has bookings");
        }

        coworkingRepository.deleteById(id);
    }


}
