package edu.centraluniversity.app.model;


import edu.centraluniversity.Model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;

    @NotNull
    private Role role;

    @NotBlank
    private String surname = "";

    @NotNull
    private String personName = "";

    @NotNull
    private String patronymic = "";

    @NotBlank
    private String username = "";

    @NotBlank
    private String password = "";

    private BookingDto[] bookingDto = new BookingDto[0];

    public UserDto(Model.User user) {
        id = user.getId();
        role = Role.valueOf(user.getRole().name());
        surname = user.getSurname();
        personName = user.getPersonName();
        patronymic = user.getPatronymic();
        username = user.getUsername();
        password = user.getPassword();
    }

    public boolean hasBooking(Long bookingId) {

        for (BookingDto bookingDto : bookingDto) {
            if (bookingDto.getId().equals(bookingId)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
