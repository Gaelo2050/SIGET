package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.TablaRhDto;
import com.siget.siget20.Repository.AMBAR.*;
import com.siget.siget20.Repository.AMBAR.Entity.*;
import com.siget.siget20.Services.Login.RhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service("RhService")
public class RhServiceImp implements RhService {

    @Autowired
    private AsistenciaPeriodoRepository AsistenciaPeriodoRepository;

    @Autowired
    private DocenteRepository DocenteRepository;

    @Autowired
    private ExclusividadLaboralRepository ExclusividadLaboralRepository;

    @Autowired
    private NombramientoRepository NombramientoRepository;

    @Autowired
    private PeriodoEscolarRepository PeriodoEscolarRepository;

    @Autowired
    private SancionRepository SancionRepository;

    @Autowired
    private HorasDocenteRepository HorasDocenteRepository;

    /**
     * Suma TODAS las horas registradas del docente
     * en la tabla HORAS_DOCENTE (sin filtrar por mes).
     */
    private BigDecimal obtenerHorasMesActual(Long docenteId) {
        if (docenteId == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = HorasDocenteRepository.totalHorasPorDocente(docenteId);
        return (total != null) ? total : BigDecimal.ZERO;
    }

    /**
     * Valida todos los requisitos de RR.HH. para emitir la constancia.
     * Devuelve un texto explicando si cumple o en qué falla.
     */
    @Override
    public String ValidacionDeRRh(Long docenteId,
                                  String correoInstitucional,
                                  String rfc,
                                  String curp,
                                  String estatus,
                                  String vigenciaDocente,
                                  BigDecimal porcentajeAsistencia,
                                  boolean tieneSancionActiva,
                                  boolean periodoEscolarActivo) {

        // Vamos a ir guardando el primer motivo de rechazo
        String motivo = null;

        // 1) ESTATUS ACTIVO
        if (!"ACTIVO".equalsIgnoreCase(estatus)) {
            motivo = "el estatus del docente no es ACTIVO";
        }
        // 2) DocenteId no nulo
        else if (docenteId == null) {
            motivo = "el docente no está registrado correctamente en AMBAR";
        }
        // 3) Correo institucional
        else if (correoInstitucional == null || correoInstitucional.isBlank()) {
            motivo = "no tiene correo institucional registrado";
        }
        // 4) RFC
        else if (rfc == null || rfc.isBlank()) {
            motivo = "no tiene RFC registrado";
        }
        // 5) CURP
        else if (curp == null || curp.isBlank()) {
            motivo = "no tiene CURP registrada";
        }
        // 6) Antigüedad mínima (al menos 1 año)
        else if (vigenciaDocente == null || vigenciaDocente.startsWith("Sin")) {
            motivo = "no tiene fecha de ingreso registrada";
        } else {
            try {
                String[] partes = vigenciaDocente.split(" ");
                int anios = Integer.parseInt(partes[0]);
                if (anios < 1) {
                    motivo = "su antigüedad es menor a 1 año";
                }
            } catch (NumberFormatException e) {
                motivo = "la vigencia del docente no tiene un formato válido";
            }
        }

        // 7) NO TENER SANCIONES ACTIVAS
        if (motivo == null && tieneSancionActiva) {
            motivo = "tiene sanciones activas registradas";
        }

        // 8) PORCENTAJE DE ASISTENCIA MÍNIMO (>= 90%)
        if (motivo == null) {
            final BigDecimal UMBRAL_ASISTENCIA = new BigDecimal("90.00");
            if (porcentajeAsistencia == null ||
                    porcentajeAsistencia.compareTo(UMBRAL_ASISTENCIA) < 0) {
                motivo = "su porcentaje de asistencia es menor al 90%";
            }
        }

        // 9) PERIODO ESCOLAR ACTIVO
        if (motivo == null && !periodoEscolarActivo) {
            motivo = "no tiene un periodo escolar activo asociado a su asistencia";
        }

        // 10) HORAS MENSUALES (>= 40 horas registradas en HORAS_DOCENTE)
        //     En esta versión se valida que tenga al menos 40 horas totales registradas.
        if (motivo == null) {
            BigDecimal horasMes = obtenerHorasMesActual(docenteId);
            final BigDecimal UMBRAL_HORAS = new BigDecimal("40.00");

            if (horasMes.compareTo(UMBRAL_HORAS) < 0) {
                motivo = "no cumple con las 40 horas mensuales registradas "
                        + "(actualmente tiene " + horasMes + " horas)";
            }
        }

        if (motivo != null) {
            return "No cumple con los requisitos: " + motivo + ".";
        }

        return "Cumple con todos los requisitos para la emisión del documento.";
    }

    @Override
    public List<TablaRhDto> InformacionConfirmacion() {
        List<TablaRhDto> tablaRhDtos = new ArrayList<>();

        List<AsistenciaPeriodo> periodos         = AsistenciaPeriodoRepository.findAll();
        List<Docente> docentes                   = DocenteRepository.findAll();
        List<ExclusividadLaboral> exclusividades = ExclusividadLaboralRepository.findAll();
        List<Nombramiento> nombramientos         = NombramientoRepository.findAll();
        List<PeriodoEscolar> periodosEscolares   = PeriodoEscolarRepository.findAll();
        List<Sancion> sanciones                  = SancionRepository.findAll();

        for (Docente d : docentes) {

            // ---- Calcular vigencia del docente (años / meses) ----
            String vigenciaDocente = "Sin fecha de ingreso";

            if (d.getFechaIngreso() != null) {
                LocalDate fechaIngreso = d.getFechaIngreso();
                LocalDate hoy = LocalDate.now();
                Period diff = Period.between(fechaIngreso, hoy);

                int years = diff.getYears();
                int months = diff.getMonths();

                vigenciaDocente = years + " años, " + months + " meses";
            }

            // ---- ¿Tiene alguna sanción ACTIVA? (activa = 1 / true) ----
            boolean tieneSancionActivaParaDocente = sanciones.stream()
                    .filter(s -> s.getDocente() != null)
                    .anyMatch(s ->
                            s.getDocente().getDocenteId().equals(d.getDocenteId())
                                    && Boolean.TRUE.equals(s.getActiva())
                    );

            for (AsistenciaPeriodo a : periodos) {
                if (a.getDocente() == null || !a.getDocente().getDocenteId().equals(d.getDocenteId())) {
                    continue;
                }

                // Periodo escolar de esa asistencia
                PeriodoEscolar periodo = a.getPeriodo();
                boolean periodoActivo = periodo != null && Boolean.TRUE.equals(periodo.getActivo());
                if (!periodoActivo) {
                    // Si quieres que solo valide cuando el periodo está activo,
                    // saltamos asistencias de periodos inactivos
                    continue;
                }

                for (ExclusividadLaboral e : exclusividades) {
                    if (e.getDocente() == null || !e.getDocente().getDocenteId().equals(d.getDocenteId())) {
                        continue;
                    }

                    String exclusividadVigencia = (e.getEsVigente() == 1)
                            ? "Es Vigente"
                            : "No es Vigente";

                    for (Nombramiento n : nombramientos) {
                        if (n.getDocente() == null || !n.getDocente().getDocenteId().equals(d.getDocenteId())) {
                            continue;
                        }

                        // Para mostrar en tabla usamos una sanción cualquiera de ese docente
                        for (Sancion s : sanciones) {
                            if (s.getDocente() == null || !s.getDocente().getDocenteId().equals(d.getDocenteId())) {
                                continue;
                            }

                            String validacion = ValidacionDeRRh(
                                    d.getDocenteId(),
                                    d.getCorreoInstitucional(),
                                    d.getRfc(),
                                    d.getCurp(),
                                    d.getEstatus(),
                                    vigenciaDocente,
                                    a.getPorcentaje(),          // BigDecimal porcentaje
                                    tieneSancionActivaParaDocente,
                                    periodoActivo
                            );

                            TablaRhDto dto = new TablaRhDto(
                                    d.getDocenteId(),
                                    d.getNombreCompleto(),
                                    d.getRfc(),
                                    d.getCurp(),
                                    d.getCorreoInstitucional(),
                                    n.getCategoria(),
                                    vigenciaDocente,
                                    n.getEstatusNombramiento(),
                                    a.getPorcentaje(),          // se muestra también en la tabla
                                    exclusividadVigencia,
                                    s.getTipoSancion(),
                                    " ",
                                    validacion
                            );

                            tablaRhDtos.add(dto);
                        }
                    }
                }
            }
        }

        return tablaRhDtos;
    }
}
