package com.siget.siget20.aop;

import com.siget.siget20.Model.DTO.TablaRhDto;
import com.siget.siget20.Services.Login.RhService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class ConfirmacionAsp {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmacionAsp.class);

    @Autowired
    @Qualifier("RhService")
    private RhService rhService;


    @Around("execution(* com.siget.siget20.Controllers..*constancia*(Long,..)) && args(docenteId, ..)")
    public Object validarAntesDeConstancia(ProceedingJoinPoint pjp,
                                           Long docenteId) throws Throwable {

        logger.info("Interceptando generación de constancia para docenteId={}", docenteId);

        // Obtener info del docente desde el servicio
        List<TablaRhDto> rhList = rhService.InformacionConfirmacion();
        TablaRhDto tablaRhDto = null;

        for (TablaRhDto rh : rhList) {
            if (rh.getDocente_id() != null && rh.getDocente_id().equals(docenteId)) {
                tablaRhDto = rh;
                break;
            }
        }

        if (tablaRhDto == null) {
            logger.warn("No se encontró información de RH para docente {}", docenteId);
            // Redirige a donde tenga sentido si no hay info
            return "redirect:/Rh/Evaluar?error=sinInfo";
        }

        String validacion = tablaRhDto.getValidacion();
        logger.info("Validación para docente {}: {}", docenteId, validacion);

        // Regla genérica: si la validación contiene "no cumple", bloquear
        if (validacion != null &&
                validacion.toLowerCase().contains("no cumple")) {

            logger.warn("Docente {} NO cumple con los requisitos. Constancia BLOQUEADA.", docenteId);

            // Volver a la vista de confirmado del mismo docente con mensaje de error
            return "redirect:/Rh/Confirmado/" + docenteId + "?error=noCumple";
        }

        // Si sí cumple con los requisitos, continuar normalmente
        return pjp.proceed();
    }
}

