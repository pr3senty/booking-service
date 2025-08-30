package edu.centraluniversity.app.controller;


import edu.centraluniversity.app.model.BookingDto;
import edu.centraluniversity.app.model.CoworkingDto;
import edu.centraluniversity.app.model.Role;
import edu.centraluniversity.app.service.CoworkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@Tag(name="Coworking", description="Coworking API")
@RequestMapping("/coworkings")
@RequiredArgsConstructor
public class CoworkingController {

    private final CoworkingService coworkingService;

    @Operation(summary="Получение всех коворкингов. А также отдельно по этажу и необходимой роли пользователя.")
    @GetMapping()
    public CoworkingDto[] getAllCoworkings(
            @RequestParam(value = "floor", required = false) Integer floor,
            @RequestParam(value = "role", required = false) Role role
    ) {
        if (floor != null && role != null) {
            CoworkingDto[] result = coworkingService.getAllCoworking();
            return Arrays.stream(result).filter(c -> c.getFloor().equals(floor) && c.getRoleRequired() == role).toArray(CoworkingDto[]::new);
        }
        if (floor != null) {
            return coworkingService.getAllCoworkingsByFloor(floor);
        } else if (role != null) {
            return coworkingService.getAllCoworkingsByRequiredRole(role);
        } else {
            return coworkingService.getAllCoworking();
        }
    }

    @PostMapping()
    public CoworkingDto createNewCoworking(@Valid @RequestBody CoworkingDto coworkingDto) {
        return coworkingService.createCoworking(coworkingDto);
    }

    @GetMapping("/{id}")
    public CoworkingDto getCoworkingById(@PathVariable long id) {
        return coworkingService.getCoworkingById(id);
    }


    @PatchMapping("/{id}")
    public CoworkingDto patchCoworking(@PathVariable long id, @RequestBody CoworkingDto coworkingDto) {
        return coworkingService.updateCoworking(id, coworkingDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCoworkingById(@PathVariable long id) {
        coworkingService.deleteCoworking(id);
    }
}
