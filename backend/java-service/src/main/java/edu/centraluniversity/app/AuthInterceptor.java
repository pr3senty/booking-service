package edu.centraluniversity.app;

import edu.centraluniversity.app.model.ResultDto;
import edu.centraluniversity.app.model.UserDto;
import edu.centraluniversity.app.model.exception.HttpStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import edu.centraluniversity.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            // разрешаем preflight запросы без аутентификации
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return false;
        }

        ResultDto result = authService.signInByJwt(authHeader);

        request.setAttribute("user", result.getUserData());

        if (hasAccess(request, result.getUserData())) {
            return true;
        }

        throw new HttpStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    private boolean hasAccess(HttpServletRequest request, UserDto user) {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals(HttpMethod.GET.name())) {
            return true;
        }

        Pattern usersPattern = Pattern.compile("/users/(\\d+)");
        Matcher usersMatcher = usersPattern.matcher(uri);

        if (usersMatcher.find()) {
            long userId = Long.parseLong(usersMatcher.group(1));

            return user.isAdmin() || user.getId() == userId;
        }

        Pattern bookingPattern = Pattern.compile("/bookings/(\\d+)");
        Matcher bookingMatcher = bookingPattern.matcher(uri);

        if (bookingMatcher.find()) {
            Long bookingId = Long.parseLong(bookingMatcher.group(1));

            return request.getMethod().equals(HttpMethod.POST.name()) || user.isAdmin() || user.hasBooking(bookingId);
        }


        Pattern coworkingPattern = Pattern.compile("/coworkings/(\\d+)");
        Matcher coworkingMatcher = coworkingPattern.matcher(uri);

        if (coworkingMatcher.find()) {
            return uri.endsWith("bookings") || user.isAdmin();
        }

        return true;
    }
}

