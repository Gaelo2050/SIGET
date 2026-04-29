package com.siget.siget20.Repository.AMBAR.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "NOMBRAMIENTOS")
public class Nombramiento {

    @Id
    @Column(name = "nombramiento_id", nullable = false)
    private Long nombramientoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(name = "categoria", nullable = false, length = 80)
    private String categoria;

    @Column(name = "estatus_nombramiento", nullable = false, length = 30)
    private String estatusNombramiento;

    @Column(name = "horas", nullable = false)
    private Integer horas;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "es_vigente", nullable = false)
    private Boolean esVigente;

    // Getters y setters

    public Long getNombramientoId() {
        return nombramientoId;
    }

    public void setNombramientoId(Long nombramientoId) {
        this.nombramientoId = nombramientoId;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstatusNombramiento() {
        return estatusNombramiento;
    }

    public void setEstatusNombramiento(String estatusNombramiento) {
        this.estatusNombramiento = estatusNombramiento;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Boolean getEsVigente() {
        return esVigente;
    }

    public void setEsVigente(Boolean esVigente) {
        this.esVigente = esVigente;
    }
}
