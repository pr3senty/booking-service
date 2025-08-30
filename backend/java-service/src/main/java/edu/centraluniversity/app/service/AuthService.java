package edu.centraluniversity.app.service;

import edu.centraluniversity.AuthGrpc;
import edu.centraluniversity.Model;
import edu.centraluniversity.app.model.ResultDto;
import edu.centraluniversity.app.model.UserCredentialsDto;
import edu.centraluniversity.app.model.UserDto;
import edu.centraluniversity.app.model.UserInfoDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthGrpc.AuthBlockingStub stub;

    public AuthService(AuthGrpc.AuthBlockingStub stub) {
        this.stub = stub;
    }

    public ResultDto signUp(UserDto user) {

        Model.User request = Model.User.newBuilder()
                .setRole(Model.UserRole.valueOf(user.getRole().name()))
                .setSurname(user.getSurname())
                .setPersonName(user.getPersonName())
                .setPatronymic(user.getPatronymic())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        return new ResultDto(stub.signUp(request));
    }

    public ResultDto signIn(UserCredentialsDto userCredentials) {

        Model.UserCredentials request = Model.UserCredentials.newBuilder()
                .setUsername(userCredentials.getUsername())
                .setPassword(userCredentials.getPassword())
                .build();

        return new ResultDto(stub.signIn(request));
    }

    public ResultDto signInByJwt(String jwtToken) {

        Model.JwtToken request = Model.JwtToken.newBuilder()
                .setValue(jwtToken)
                .build();

        return new ResultDto(stub.signInByJwt(request));
    }

    public ResultDto updateUser(long userId, UserDto user) {
        Model.User.Builder builder = Model.User.newBuilder()
                .setId(userId)
                .setSurname(user.getSurname())
                .setPersonName(user.getPersonName())
                .setPatronymic(user.getPatronymic())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword());

        if (user.getRole() != null) {
            builder.setRole(Model.UserRole.valueOf(user.getRole().name()));
        }

        Model.User request = builder.build();

        return new ResultDto(stub.updateUser(request));
    }

    public boolean deleteUser(long userId) {

        Model.UserId request = Model.UserId.newBuilder()
                .setValue(userId)
                .build();

        return stub.deleteUser(request).getValue();
    }

    public UserInfoDto getUserInfo(long userId) {

        Model.UserId request = Model.UserId.newBuilder()
                .setValue(userId)
                .build();

        return new UserInfoDto(stub.getUserInfo(request));
    }
}
