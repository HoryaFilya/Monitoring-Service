package com.shaikhraziev.in.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.service.JwtService;
import com.shaikhraziev.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

import static com.shaikhraziev.error.Error.INVALID_REQUEST;
import static com.shaikhraziev.error.Error.NOT_ENOUGH_RIGHTS;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Loggable
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private UserService userService;
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        jwtService = (JwtService) getServletContext().getAttribute("jwtService");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (jwtService.authorizationUserRights(req.getCookies()) || jwtService.authorizationAdminRights(req.getCookies())) {
                userService.logout(req, resp);
                resp.setStatus(SC_OK);
            } else {
                resp.setStatus(SC_BAD_REQUEST);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(NOT_ENOUGH_RIGHTS));
            }
        } catch (Exception e) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_REQUEST));
        }
    }
}