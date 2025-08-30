package edu.centraluniversity.app.model;


import edu.centraluniversity.app.model.database.Coworking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoworkingDto {

    private Long id;

    @NotNull
    private Integer floor;

    @NotNull
    private Role roleRequired;

    @NotBlank
    private String label;

    @NotNull
    private Integer occupancy;

    private BookingDto[] bookings = new BookingDto[0];

    public CoworkingDto(Coworking coworking) {
        this.id = coworking.getId();
        this.floor = coworking.getFloor();
        this.roleRequired = coworking.getRoleRequired();
        this.label = coworking.getLabel();
        this.occupancy = coworking.getOccupancy();
    }

    public CoworkingDto(Coworking coworking, BookingDto[] bookings) {
        this(coworking);
        this.bookings = bookings;
    }
}
