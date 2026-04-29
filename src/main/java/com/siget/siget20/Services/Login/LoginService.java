package com.siget.siget20.Services.Login;

import com.siget.siget20.Model.Usuario;


public interface LoginService {
    public Usuario login(String username, String password);
}
