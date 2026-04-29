package com.siget.siget20.Model.DTO;

public class LiberacionActividadesInfo {

    private String fechaInicio;
    private String fechaFin;
    private String motivo;

    public LiberacionActividadesInfo() {
    }

    public LiberacionActividadesInfo(String fechaInicio,
                                     String fechaFin,
                                     String motivo) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
