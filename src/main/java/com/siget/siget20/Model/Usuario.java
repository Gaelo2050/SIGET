package com.siget.siget20.Model;

import com.siget.siget20.Repository.SIGET.Entity.usuario;

public class Usuario {
    private  Long id;
    private String usuario;
    private String contrasena;
    private String nombrecompleto;
    private String tipo;
    private Long docenteid;
    private String area;
    private String creado_en;

    public Usuario(usuario entity) {
        this.id = entity.getUsuarioId();
        this.usuario = entity.getUsuario();
        this.nombrecompleto = entity.getNombreCompleto();
        this.tipo = entity.getTipo();
        this.docenteid = entity.getDocenteId();
        this.creado_en = entity.getAreaCodigo();
        this.area= entity.getAreaCodigo();
    }
    public Usuario() {}

    public Long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombrecompleto() {
        return nombrecompleto;
    }

    public void setNombrecompleto(String nombrecompleto) {
        this.nombrecompleto = nombrecompleto;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public long getDocenteid() {
        return docenteid;
    }
    public void setDocenteid(long docenteid) {
        this.docenteid = docenteid;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public String getCreado_en() {
        return creado_en;
    }
    public void setCreado_en(String creado_en) {
        this.creado_en = creado_en;
    }

}

