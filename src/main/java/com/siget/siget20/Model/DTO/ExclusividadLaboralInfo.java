package com.siget.siget20.Model.DTO;

public class ExclusividadLaboralInfo {

    private String nombreDocente;
    private String filiacion;
    private String clavePresupuestal;

    public ExclusividadLaboralInfo() {
    }

    public ExclusividadLaboralInfo(String nombreDocente,
                                   String filiacion,
                                   String clavePresupuestal) {
        this.nombreDocente = nombreDocente;
        this.filiacion = filiacion;
        this.clavePresupuestal = clavePresupuestal;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public String getFiliacion() {
        return filiacion;
    }

    public void setFiliacion(String filiacion) {
        this.filiacion = filiacion;
    }

    public String getClavePresupuestal() {
        return clavePresupuestal;
    }

    public void setClavePresupuestal(String clavePresupuestal) {
        this.clavePresupuestal = clavePresupuestal;
    }
}
