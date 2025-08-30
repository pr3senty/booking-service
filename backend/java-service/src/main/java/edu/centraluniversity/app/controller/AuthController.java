package edu.centraluniversity.app.controller;

import edu.centraluniversity.app.model.UserInfoDto;
import edu.centraluniversity.app.service.AuthService;
import edu.centraluniversity.app.model.ResultDto;
import edu.centraluniversity.app.model.UserCredentialsDto;
import edu.centraluniversity.app.model.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name="Auth", description="Auth API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary="Регистрация нового пользователя")
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultDto signUp(@Valid @RequestBody UserDto user) {

        return authService.signUp(user);
    }

    @Operation(summary="Вход пользователя по логину и паролю")
    @PostMapping("/auth")
    public ResultDto signIn(@Valid @RequestBody UserCredentialsDto userCredentials) {

        return authService.signIn(userCredentials);
    }

    @Operation(summary="Вход пользователя по JWT токену")
    @GetMapping("/auth/jwt")
    public ResultDto signInByJwt(@RequestHeader("Authorization") String jwtToken) {

        return authService.signInByJwt(jwtToken);
    }

    @Operation(summary="Обновление данных пользователя")
    @PatchMapping("/users/{id}")
    public ResultDto updateUser(@PathVariable("id") long id, @RequestBody UserDto user) {

        return authService.updateUser(id, user);
    }

    @Operation(summary="Удаление пользователя")
    @DeleteMapping("/users/{id}")
    public boolean deleteUser(@PathVariable("id") long id) {

        return authService.deleteUser(id);
    }

    @Operation(summary = "Получение данных пользователя по ID")
    @GetMapping("/users/{id}")
    public UserInfoDto getUser(@PathVariable("id") long id) {
        return authService.getUserInfo(id);
    }

}
