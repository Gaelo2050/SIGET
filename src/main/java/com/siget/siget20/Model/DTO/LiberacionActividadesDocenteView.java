package com.siget.siget20.Model.DTO;

public class LiberacionActividadesDocenteView {

    private String nombreCompleto;
    private String numeroEmpleado;
    private String departamento;

    public LiberacionActividadesDocenteView() {
    }

    public LiberacionActividadesDocenteView(String nombreCompleto,
                                            String numeroEmpleado,
                                            String departamento) {
        this.nombreCompleto = nombreCompleto;
        this.numeroEmpleado = numeroEmpleado;
        this.departamento = departamento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
