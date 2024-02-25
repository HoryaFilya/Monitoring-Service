package com.shaikhraziev.in.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.service.JwtService;
import com.shaikhraziev.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.SneakyThrows;

import java.io.IOException;

import static com.shaikhraziev.entity.Role.ADMIN;
import static com.shaikhraziev.error.Error.INVALID_REQUEST;
import static com.shaikhraziev.error.Error.USER_WAS_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Loggable
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        jwtService = (JwtService) getServletContext().getAttribute("jwtService");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserCreateEditDto maybeUser = objectMapper.readValue(req.getInputStream(), UserCreateEditDto.class);

            UserReadDto authorizationUser = userService.authorization(maybeUser).orElse(null);

            if (authorizationUser == null) {
                resp.setStatus(SC_BAD_REQUEST);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(USER_WAS_NOT_FOUND));
            } else if (authorizationUser.getRole().equals(ADMIN)) {
                resp.setStatus(SC_OK);
                resp.addCookie(new Cookie("JWT", jwtService.generateToken(authorizationUser)));
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(authorizationUser));
            } else {
                resp.setStatus(SC_OK);
                resp.addCookie(new Cookie("JWT", jwtService.generateToken(authorizationUser)));
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(authorizationUser));
            }
        } catch (IOException exception) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_REQUEST));
        }
    }
}