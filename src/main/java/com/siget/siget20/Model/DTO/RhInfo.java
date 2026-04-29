package com.siget.siget20.Model.DTO;

public class RhInfo {

    private String nombreEncargado;

    public RhInfo() {}

    public RhInfo(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }

    public String getNombreEncargado() {
        return nombreEncargado;
    }

    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }
}
