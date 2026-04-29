package com.siget.siget20.Repository.AMBAR.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SANCIONES")
public class Sancion {

    @Id
    @Column(name = "sancion_id", nullable = false)
    private Long sancionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "periodo_id", nullable = false)
    private PeriodoEscolar periodo;

    @Column(name = "tipo_sancion", nullable = false, length = 80)
    private String tipoSancion;

    @Column(name = "fecha_sancion", nullable = false)
    private LocalDate fechaSancion;

    @Column(name = "activa", nullable = false)
    private Boolean activa;

    // Getters y setters

    public Long getSancionId() {
        return sancionId;
    }

    public void setSancionId(Long sancionId) {
        this.sancionId = sancionId;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public PeriodoEscolar getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoEscolar periodo) {
        this.periodo = periodo;
    }

    public String getTipoSancion() {
        return tipoSancion;
    }

    public void setTipoSancion(String tipoSancion) {
        this.tipoSancion = tipoSancion;
    }

    public LocalDate getFechaSancion() {
        return fechaSancion;
    }

    public void setFechaSancion(LocalDate fechaSancion) {
        this.fechaSancion = fechaSancion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}