package com.siget.siget20.Controllers;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Services.Login.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @GetMapping
    public String redirectToInicio() {
        return "redirect:/login/inicio";
    }

    // Muestra la pantalla de login  -> GET /login/inicio
    @GetMapping("/inicio")
    public String loginPage(Model model) {
        // opcional: limpiar datos
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", "");
        }
        return "Login/Vista/index";
    }

    // Procesa el login -> POST /login/inicio
    @PostMapping("/inicio")
    public String doLogin(@RequestParam String usuario,
                          @RequestParam String contrasena,
                          Model model,
                          HttpSession session) {

        Usuario user = loginService.login(usuario, contrasena);

        if (user == null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            model.addAttribute("usuario", usuario);
            return "Login/Vista/index";
        }

        // Guardamos usuario en sesión
        session.setAttribute("usuarioLogueado", user);

        // Redirecciones según el tipo
        if ("Docente".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/Docente/Solicitar";
        }
        if ("Rh".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/Rh/Evaluar";
        }
        if ("Servicios Escolares".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/ServiciosEscolares/Evaluar";
        }
        if ("Desarrollo Academico".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/DesarrolloAcademico/Evaluar";
        }
        if ("DSyC".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/DSyC/Evaluar";
        }
        if ("Administracion".equalsIgnoreCase(user.getTipo())) {
            return "redirect:/Administracion/Evaluar";
        }

        return "redirect:/home";
    }


    @GetMapping("/vista")
    public String Vista() {
        return "Login/Vista/inicio";
    }

    @GetMapping("/fueradehorario")
    public String fueradehorario() {
        return "Login/Vista/fueraHorario";
    }

    @GetMapping("/logout")
    public String logout() {
        return "Login/Vista/inicio";
    }







}
