package com.shaikhraziev.in.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.dto.UserReadDto;
import com.shaikhraziev.dto.UserCreateEditDto;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.validation.UserValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.shaikhraziev.error.Error.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@Loggable
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private UserService userService;
    private UserValidation userValidation;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        userValidation = (UserValidation) getServletContext().getAttribute("userValidation");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserCreateEditDto user = objectMapper.readValue(req.getInputStream(), UserCreateEditDto.class);

            if (!userValidation.isValidLoginAndPassword(user)) {
                resp.setStatus(SC_BAD_REQUEST);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_LOGIN_OR_PASSWORD));
            } else {
                UserReadDto userReadDto = userService.registration(user);
                resp.setStatus(SC_CREATED);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(userReadDto));
            }
        } catch (IOException exception) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_REQUEST));
        } catch (Exception e) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(USER_ALREADY_EXISTS));
        }
    }
}