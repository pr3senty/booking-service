package edu.centraluniversity.app.model;

import edu.centraluniversity.Model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    UserDto userData;
    String jwtToken;

    public ResultDto(Model.Result result) {
        userData = new UserDto(result.getUserData());
        jwtToken = result.getJwtToken().getValue();
    }
}
