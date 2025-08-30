package edu.centraluniversity.app.model;

import edu.centraluniversity.Model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    @NotNull
    private Long userId;

    @NotNull
    private Role role;

    @NotBlank
    private String surname;

    @NotBlank
    private String personName;

    @NotBlank
    private String patronymic;

    public UserInfoDto(Model.UserInfo userInfo) {
        this.userId = userInfo.getId();
        this.role = Role.valueOf(userInfo.getRole().name());
        this.surname = userInfo.getSurname();
        this.personName = userInfo.getPersonName();
        this.patronymic = userInfo.getPatronymic();
    }
}
