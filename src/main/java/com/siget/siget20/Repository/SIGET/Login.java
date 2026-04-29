package com.siget.siget20.Repository.SIGET;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("loginRepository")
public interface Login extends JpaRepository<usuario, Integer> {

    @Query("SELECT u FROM usuario  u WHERE u.usuario = :usuario AND u.contrasena = :contrasena")
    List<usuario> obtenerUsuario(String usuario, String contrasena);

    @Query("SELECT u FROM usuario u")
    List<Usuario> obtenerUsuarioCompleto();

}
