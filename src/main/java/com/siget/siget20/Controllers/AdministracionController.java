package com.siget.siget20.Controllers;

import com.siget.siget20.Model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Administracion")
public class AdministracionController {

    @Autowired
    @Qualifier("solicitudAdministracionServiceImp")
    private com.siget.siget20.Services.Login.SolicitudAdministracionService solicitudAdministracionService;

    @Autowired
    @Qualifier("AdministracionService")
    com.siget.siget20.Services.Login.Imp.AdministracionServiceImp administracionService;

    //quejas-SIGET
    @GetMapping("/QuejasRh")
    public String QuejasRh(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/Administracion/QuejasSIGET";
    }

    //quejasAmbar
    @GetMapping("/Quejas")
    public String QuejasAmbar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/Administracion/QuejasAMBAR";
    }

    //vista Evaluar
    @GetMapping("/Evaluar")
    public String Evaluar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<com.siget.siget20.Model.DTO.SolicitudAdministracionDto> dto = solicitudAdministracionService.SolicitudAdministracion();
        model.addAttribute("SolicitudAdministracionDto", dto);
        return "Login/Administracion/AdministracionEvaluar";
    }

    //vista Revisados
    @GetMapping("/Revisados")
    public String Revisados(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/Administracion/AdministracionRevisados";
    }

    //vista Confirmado
    @GetMapping("/Confirmado/{id}")
    public String Confirmado(@PathVariable("id") Long id,
                             Model model,
                             HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<com.siget.siget20.Model.DTO.TablaAdministracionDto> list = administracionService.InformacionConfirmacion();
        com.siget.siget20.Model.DTO.TablaAdministracionDto tablaDto = null;

        for (com.siget.siget20.Model.DTO.TablaAdministracionDto item : list) {
            if (item.getDocente_id() != null && item.getDocente_id().equals(id)) {
                tablaDto = item;
                break;
            }
        }

        model.addAttribute("TablaAdministracionDto", tablaDto);

        return "Login/Administracion/AdministracionConfirmar";
    }

}
