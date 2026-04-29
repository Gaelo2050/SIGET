package com.siget.siget20.aop;

import com.siget.siget20.Controllers.LoginController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;

@Aspect
@Component
public class LoginAsp {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Around("execution(* com.siget.siget20.Controllers.LoginController.loginPage(..))")
    public Object validarHorarioLogin(ProceedingJoinPoint pjp) throws Throwable {

        String methodName = pjp.getSignature().getName();
        String args = Arrays.toString(pjp.getArgs());
        logger.info("Se va a ejecutar el método: " + methodName + " con los argumentos: " + args);

        boolean validarHorario = false;
        if (!validarHorario) {
            return pjp.proceed();
        }

        LocalTime ahora = LocalTime.now();
        LocalTime inicio = LocalTime.of(7, 0);
        LocalTime fin    = LocalTime.of(19, 0);

        if (ahora.isBefore(inicio) || ahora.isAfter(fin)) {
            logger.warn("Intento de acceso fuera de horario: " + ahora);


            return "redirect:/login/fueradehorario";
        }


        return pjp.proceed();
    }
}
