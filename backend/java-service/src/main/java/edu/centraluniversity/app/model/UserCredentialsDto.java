package edu.centraluniversity.app.model;

import edu.centraluniversity.Model;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsDto {

    @NotEmpty
    String username;

    @NotEmpty
    String password;

    public UserCredentialsDto(Model.UserCredentials userCredentials) {
        this.username = userCredentials.getUsername();
        this.password = userCredentials.getPassword();
    }
}
