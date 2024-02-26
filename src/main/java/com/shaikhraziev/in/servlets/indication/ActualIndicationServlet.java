package com.shaikhraziev.in.servlets.indication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.dto.IndicationReadDto;
import com.shaikhraziev.service.IndicationService;
import com.shaikhraziev.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.shaikhraziev.error.Error.*;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/indications/actual/*")
public class ActualIndicationServlet extends HttpServlet {

    private IndicationService indicationService;
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        indicationService = (IndicationService) getServletContext().getAttribute("indicationService");
        jwtService = (JwtService) getServletContext().getAttribute("jwtService");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getPathInfo().substring(1);

            if (jwtService.authorizationUserRights(req.getCookies(), id)) {
                IndicationReadDto actualIndications = indicationService.getActualIndications(Long.valueOf(id)).orElse(null);
                resp.setStatus(SC_OK);

                if (actualIndications == null) resp.getOutputStream().write(objectMapper.writeValueAsBytes(INDICATIONS_WAS_NOT_TRANSMITTED));
                else resp.getOutputStream().write(objectMapper.writeValueAsBytes(actualIndications));
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