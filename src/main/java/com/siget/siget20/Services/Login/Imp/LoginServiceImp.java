package com.siget.siget20.Services.Login.Imp;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.Entity.usuario;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Services.Login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LoginServiceImp implements LoginService {

    @Autowired
    private Login loginRepository;

    @Override
    public Usuario login(String username, String password) {

        List<usuario> userList = loginRepository.obtenerUsuario(username, password);

        if (userList.size() > 0) {
            for (usuario u : userList) {
                if (u.getUsuario().equals(username) &&
                        u.getContrasena().equals(password)) {
                         return new Usuario(u);
                }
            }
        }
        return null;
    }
}
