package com.shaikhraziev.aop;

import com.shaikhraziev.entity.User;
import com.shaikhraziev.map.UserCreateEditMapper;
import com.shaikhraziev.map.UserReadMapper;
import com.shaikhraziev.repository.AuditRepository;
import com.shaikhraziev.repository.UserRepository;
import com.shaikhraziev.service.AuditService;
import com.shaikhraziev.service.JwtService;
import com.shaikhraziev.service.UserService;
import com.shaikhraziev.util.ConnectionManager;
import com.shaikhraziev.util.PropertiesUtil;
import jakarta.servlet.http.Cookie;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
public class Audit {

    private final UserCreateEditMapper userCreateEditMapper = new UserCreateEditMapper();
    private final UserReadMapper userReadMapper = new UserReadMapper();
    private final ConnectionManager connectionManager = new ConnectionManager(
            PropertiesUtil.get("db.url"),
            PropertiesUtil.get("db.username"),
            PropertiesUtil.get("db.password")
    );
    private final UserRepository userRepository = new UserRepository(connectionManager);
    private final AuditRepository auditRepository = new AuditRepository(connectionManager);
    private final UserService userService = new UserService(userRepository, userCreateEditMapper, userReadMapper);
    private final AuditService auditService = new AuditService(auditRepository);
    private final JwtService jwtService = new JwtService();


    @AfterReturning(value = "@within(com.shaikhraziev.aop.annotations.Audit) && " +
                            "execution(public com.shaikhraziev.entity.User com.shaikhraziev.repository.UserRepository.save(com.shaikhraziev.entity.User))",
            returning = "result")
    public void registrationAuditing(Object result) throws Throwable {
        User user = (User) result;

        if (user != null)
            auditService.registration(user.getUsername());
    }

    @AfterReturning(value = "@within(com.shaikhraziev.aop.annotations.Audit) && " +
                            "execution(public java.util.Optional com.shaikhraziev.repository.UserRepository.findByUsernameAndPassword(com.shaikhraziev.dto.UserCreateEditDto))",
            returning = "result")
    public void authorizationAuditing(Object result) throws Throwable {
        Optional maybeUser = (Optional) result;

        if (maybeUser.isPresent()) {
            User user = (User) maybeUser.get();
            auditService.authorization(user.getUsername());
        }
    }

    @AfterReturning(value = "@within(com.shaikhraziev.aop.annotations.Audit) && " +
                            "execution(public jakarta.servlet.http.Cookie com.shaikhraziev.service.UserService.logout(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse))",
            returning = "result")
    public void logoutAuditing(Object result) throws Throwable {
        Cookie jwt = (Cookie) result;

        String id = jwtService.extractPayload(jwt.getValue()).getOrDefault("userId", "error").toString();

        Optional<User> maybeUser = userService.findById(Long.valueOf(id));

        if (maybeUser.isPresent())
            auditService.logout(maybeUser.get().getUsername());
    }

    @Around("@within(com.shaikhraziev.aop.annotations.Audit) && " +
            "execution(public java.util.Optional com.shaikhraziev.repository.IndicationRepository.getActualIndications(Long))"
    )
    public Object getActualIndicationsAuditing(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = proceedingJoinPoint.proceed();

        Object maybeId = Arrays.stream(proceedingJoinPoint.getArgs()).findFirst().orElse(null);

        if (maybeId != null) {
            Long id = (Long) maybeId;
            User user = userService.findById(id).orElse(null);

            if (user != null) {
                auditService.getActualIndications(user.getUsername());
            }
        }

        return result;
    }

    @Around("@within(com.shaikhraziev.aop.annotations.Audit) && " +
            "execution(public java.util.List com.shaikhraziev.repository.IndicationRepository.getHistory(Long))"
    )
    public Object getHistoryAuditing(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = proceedingJoinPoint.proceed();

        Object maybeId = Arrays.stream(proceedingJoinPoint.getArgs()).findFirst().orElse(null);

        if (maybeId != null) {
            Long id = (Long) maybeId;
            User user = userService.findById(id).orElse(null);

            if (user != null) {
                auditService.getHistory(user.getUsername());
            }
        }

        return result;
    }

    @Around("@within(com.shaikhraziev.aop.annotations.Audit) && " +
            "execution(public java.util.List com.shaikhraziev.repository.IndicationRepository.getMonthlyIndications(Long, java.time.Month))"
    )
    public Object getMonthlyIndicationsAuditing(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = proceedingJoinPoint.proceed();

        Object requestParameters = Arrays.stream(proceedingJoinPoint.getArgs()).toList();

        List parameterList = (List) requestParameters;

        Long id = Long.valueOf(parameterList.get(0).toString());
        Month month = Month.valueOf(parameterList.get(1).toString());

        User user = userService.findById(id).orElse(null);

        if (user != null) {
            auditService.getMonthlyIndications(user.getUsername(), month);
        }

        return result;
    }

    @Around("@within(com.shaikhraziev.aop.annotations.Audit) && " +
            "execution(public void com.shaikhraziev.repository.IndicationRepository.uploadIndications(Long, com.shaikhraziev.dto.IndicationCreateEditDto))"
    )
    public Object uploadIndicationsAuditing(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object result = proceedingJoinPoint.proceed();

        Object maybeId = Arrays.stream(proceedingJoinPoint.getArgs()).findFirst().orElse(null);

        if (maybeId != null) {
            Long id = (Long) maybeId;
            Month month = LocalDate.now().getMonth();
            User user = userService.findById(id).orElse(null);

            if (user != null) {
                auditService.uploadIndications(user.getUsername(), month);
            }
        }

        return result;
    }
}