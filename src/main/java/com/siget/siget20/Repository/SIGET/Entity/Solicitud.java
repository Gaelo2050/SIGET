package com.siget.siget20.Repository.SIGET.Entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitud_id",nullable = true)
    private Long solicitudId;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(name = "area_destino", nullable = false, length = 30)
    private String areaDestino; // "RH", etc

    @Column(name = "documento_nombre", nullable = false, length = 120)
    private String documentoNombre;

    @Column(name = "convocatoria", nullable = false, length = 20)
    private String convocatoria;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;   // PENDIENTE, APROBADO, RECHAZADO

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    public Long getSolicitudId() {
        return solicitudId;
    }
    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }
    public Long getDocenteId() {
        return docenteId;
    }
    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
    public String getAreaDestino() {
        return areaDestino;
    }
    public void setAreaDestino(String areaDestino) {
        this.areaDestino = areaDestino;
    }
    public String getDocumentoNombre() {
        return documentoNombre;
    }
    public void setDocumentoNombre(String documentoNombre) {
        this.documentoNombre = documentoNombre;
    }
    public String getConvocatoria() {
        return convocatoria;
    }
    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }


}

