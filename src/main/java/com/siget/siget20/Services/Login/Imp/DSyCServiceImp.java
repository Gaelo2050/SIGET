package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.DTO.TablaDSyCDto;
import com.siget.siget20.Repository.AMBAR.*;
import com.siget.siget20.Repository.AMBAR.Entity.*;
import com.siget.siget20.Services.Login.DSyCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service("DSyCService")
public class DSyCServiceImp implements DSyCService {

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

    @Override
    public String ValidacionDeDSyC(Long DocenteId, String CorreoInstitucional, String Rfc, String Curp, String Estatus, String vigenciaDocente) {
        boolean valido = false;

        if(Estatus.equals("ACTIVO")){valido = true;}
        if(DocenteId!=null){valido = true;}
        if(CorreoInstitucional!=null){valido = true;}
        if(Rfc!=null){valido = true;}
        if(Curp!=null){valido = true;}
        if (vigenciaDocente != null && !vigenciaDocente.startsWith("Sin")) {
            try {
                String[] partes = vigenciaDocente.split(" ");
                int anios = Integer.parseInt(partes[0]);
                if (anios >= 1) {
                    valido = true;
                }
            } catch (NumberFormatException e) {
            }
        }

        if(valido){
            return "Cumple con los requisitos";
        }else{
            return "No cumple con los requisitos";
        }
    }

    @Override
    public List<TablaDSyCDto> InformacionConfirmacion() {
        List<TablaDSyCDto> tablaDtos = new ArrayList<>();

        List<AsistenciaPeriodo> periodos = AsistenciaPeriodoRepository.findAll();
        List<Docente> docentes = DocenteRepository.findAll();
        List<ExclusividadLaboral> exclusividades = ExclusividadLaboralRepository.findAll();
        List<Nombramiento> nombramientos = NombramientoRepository.findAll();
        List<PeriodoEscolar> periodosEscolares = PeriodoEscolarRepository.findAll();
        List<Sancion> sanciones = SancionRepository.findAll();

        for (Docente d : docentes) {
            String vigenciaDocente = "Sin fecha de ingreso";

            if (d.getFechaIngreso() != null) {
                LocalDate fechaIngreso = d.getFechaIngreso();
                LocalDate hoy = LocalDate.now();
                Period diff = Period.between(fechaIngreso, hoy);
                int years = diff.getYears();
                int months = diff.getMonths();
                vigenciaDocente = years + " años, " + months + " meses";
            }

            for (AsistenciaPeriodo a : periodos) {
                if (a.getDocente() == null || !a.getDocente().getDocenteId().equals(d.getDocenteId())) {
                    continue;
                }

                for (ExclusividadLaboral e : exclusividades) {
                    if (e.getDocente() == null || !e.getDocente().getDocenteId().equals(d.getDocenteId())) {
                        continue;
                    }

                    String exclusividadVigencia;
                    if (e.getEsVigente() == 1) {
                        exclusividadVigencia = "Es Vigente";
                    } else {
                        exclusividadVigencia = "No es Vigente";
                    }

                    for (Nombramiento n : nombramientos) {
                        if (n.getDocente() == null || !n.getDocente().getDocenteId().equals(d.getDocenteId())) {
                            continue;
                        }

                        for (Sancion s : sanciones) {
                            if (s.getDocente() == null || !s.getDocente().getDocenteId().equals(d.getDocenteId())) {
                                continue;
                            }

                            String Validacion = ValidacionDeDSyC(d.getDocenteId(), d.getCorreoInstitucional(), d.getRfc(), d.getCurp(), d.getEstatus(), vigenciaDocente);
                            TablaDSyCDto dto = new TablaDSyCDto(
                                    d.getDocenteId(),
                                    d.getNombreCompleto(),
                                    d.getRfc(),
                                    d.getCurp(),
                                    d.getCorreoInstitucional(),
                                    n.getCategoria(),
                                    vigenciaDocente,
                                    n.getEstatusNombramiento(),
                                    a.getPorcentaje(),
                                    exclusividadVigencia,
                                    s.getTipoSancion(),
                                    " ",
                                    Validacion
                            );

                            tablaDtos.add(dto);
                        }
                    }
                }
            }
        }

        return tablaDtos;
    }
}
