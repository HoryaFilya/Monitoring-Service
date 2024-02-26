package com.shaikhraziev.in.servlets.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaikhraziev.aop.annotations.Loggable;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.shaikhraziev.error.Error.INVALID_REQUEST;
import static com.shaikhraziev.error.Error.NOT_ENOUGH_RIGHTS;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Loggable
@WebServlet("/admin/audits")
public class AuditUserServlet extends HttpServlet {

    private AuditService auditService;
    private JwtService jwtService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        auditService = (AuditService) getServletContext().getAttribute("auditService");
        jwtService = (JwtService) getServletContext().getAttribute("jwtService");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (jwtService.authorizationAdminRights(req.getCookies())) {
                resp.setStatus(SC_OK);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(auditService.getUserAudit()));
            } else {
                resp.setStatus(SC_BAD_REQUEST);
                resp.getOutputStream().write(objectMapper.writeValueAsBytes(NOT_ENOUGH_RIGHTS));
            }
        }
        catch (Exception e) {
            resp.setStatus(SC_BAD_REQUEST);
            resp.getOutputStream().write(objectMapper.writeValueAsBytes(INVALID_REQUEST));
        }
    }
}