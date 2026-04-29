package com.siget.siget20.Repository.AMBAR.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EXCLUSIVIDAD_LABORAL")
public class ExclusividadLaboral {

    @Id
    @Column(name = "exclusividad_id", nullable = false)
    private Long exclusividadId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @Column(name = "fecha_firma", nullable = false)
    private LocalDate fechaFirma;

    @Column(name = "vigente_hasta", nullable = false)
    private LocalDate vigenteHasta;

    @Column(name = "es_vigente", nullable = false)
    private int esVigente;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    // Getters y setters

    public Long getExclusividadId() {
        return exclusividadId;
    }

    public void setExclusividadId(Long exclusividadId) {
        this.exclusividadId = exclusividadId;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public LocalDate getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDate fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public LocalDate getVigenteHasta() {
        return vigenteHasta;
    }

    public void setVigenteHasta(LocalDate vigenteHasta) {
        this.vigenteHasta = vigenteHasta;
    }

    public int getEsVigente() {
        return esVigente;
    }

    public void setEsVigente(int esVigente) {
        this.esVigente = esVigente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}