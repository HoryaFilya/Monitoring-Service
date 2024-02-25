package com.shaikhraziev.in.servlets.indication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.dto.IndicationCreateEditDto;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.service.IndicationService;
import com.shaikhraziev.service.JwtService;
import com.shaikhraziev.validation.UserValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static com.shaikhraziev.error.Error.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@Loggable
@WebServlet("/indications")
public class UploadIndicationServlet extends HttpServlet {

    private IndicationService indicationService;
    private UserValidation userValidation;
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        indicationService = (IndicationService) getServletContext().getAttribute("indicationService");
        jwtService = (JwtService) getServletContext().getAttribute("jwtService");
        userValidation = (UserValidation) getServletContext().getAttribute("userValidation");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            IndicationCreateEditDto transmittedIndications = objectMapper.readValue(req.getInputStream(), IndicationCreateEditDto.class);
            System.out.println("text " + transmittedIndications);
            System.out.println("request: " + req.getInputStream());

            if (jwtService.authorizationUserRights(req.getCookies())) {
                Cookie cookie = Arrays.stream(req.getCookies())
                        .filter(c -> c.getName().equals("JWT"))
                        .findFirst().get();
                Object o = jwtService.extractPayload(cookie.getValue()).getOrDefault("userId", null);
                Long id = Long.valueOf(o.toString());

                IndicationReadDto actualIndications = indicationService.getActualIndications(id).orElse(null);

                if (userValidation.isValidUploadIndications(transmittedIndications, actualIndications)) {

                    if (!indicationService.indicationsAlreadyUploaded(id, LocalDate.now().getMonth(), LocalDate.now().getYear())) {
                        indicationService.uploadIndications(id, transmittedIndications, actualIndications);
                        resp.setStatus(SC_OK);
                    } else {
                        resp.setStatus(SC_BAD_REQUEST);
                        resp.getOutputStream().write(objectMapper.writeValueAsBytes(INDICATIONS_HAS_ALREADY_BEEN_TRANSMITTED));
                    }

                } else {
                    resp.setStatus(SC_BAD_REQUEST);
                    resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_VALUES_TRANSMITTED_INDICATIONS));
                }
            } else {
                resp.setStatus(SC_BAD_REQUEST);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(NOT_ENOUGH_RIGHTS));
            }
        } catch (Exception exception) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_REQUEST));
            System.out.println(exception.getMessage());
        }
    }
}