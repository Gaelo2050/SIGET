package com.siget.siget20.Repository.SIGET.Entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificacion_id")
    private Long notificacionId;

    @Column(name = "area_destino", nullable = false, length = 30)
    private String areaDestino;

    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "leida", nullable = false)
    private boolean leida;

    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;

    public Long getNotificacionId() {
        return notificacionId;
    }
    public void setNotificacionId(Long notificacionId) {
        this.notificacionId = notificacionId;
    }
    public String getAreaDestino() {
        return areaDestino;
    }
    public void setAreaDestino(String areaDestino) {
        this.areaDestino = areaDestino;
    }
    public Long getSolicitudId() {
        return solicitudId;
    }
    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public boolean isLeida() {
        return leida;
    }
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }
    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }



}

