package com.siget.siget20.Controllers;

import com.siget.siget20.Model.DTO.RevisadoDocenteDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Model.DTO.TablaDocenteDto;
import com.siget.siget20.Services.Login.DocenteService;
import com.siget.siget20.Services.Login.Imp.DocenteRevisadosServiceImp;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/Docente")
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    @Qualifier("docenterevisadosService")
    DocenteRevisadosServiceImp docenteRevisadosService;


    // Vista Docente - Solicitar  -> /Docente/Solicitar (GET)
    @GetMapping("/Solicitar")
    public String docenteSolicitar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        // usuario logueado para el header
        model.addAttribute("usuario", user);

        // lista para la tabla
        List<TablaDocenteDto> docentes = docenteService.obtenerUsuarioTabla();
        model.addAttribute("docentes", docentes);

        return "Login/Docente/DocenteSolicitar";
    }

    // Cuando el docente presiona el botón "Solicitar" (POST)
    @PostMapping("/Solicitar")
    public String enviarSolicitud(@RequestParam("areaDestino") String areaDestino,
                                  @RequestParam("documentoNombre") String documentoNombre,
                                  @RequestParam("convocatoria") String convocatoria,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "La sesión ha expirado. Vuelve a iniciar sesión.");
            return "redirect:/login/inicio";
        }

        // Id del docente logueado
        Long docenteId = user.getId();

        // Guardar en la BD (solicitudes + notificaciones)
        docenteService.crearSolicitudYNotificacion(
                docenteId,
                documentoNombre,
                areaDestino,
                convocatoria
        );

        redirectAttributes.addFlashAttribute("mensaje", "Solicitud enviada correctamente.");
        return "redirect:/Docente/Solicitar";
    }


    // Vista Docente - Quejas  -> /Docente/Quejas
    @GetMapping("/Quejas")
    public String QuejasDocente(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);
        return "Login/Docente/QuejasSIGET";
    }

    // quejasAmbar-Docente
    @GetMapping("/QuejasAmbarDocente")
    public String QuejasAmbarDocente(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);
        return "Login/Docente/QuejasAMBAR";
    }

    @GetMapping("/Confirmado")
    public String docenteConfirmado(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<RevisadoDocenteDto> revisados =
                docenteRevisadosService.obtenerRevisadosDocente(user.getId());

        model.addAttribute("revisados", revisados);

        return "Login/Docente/DocenteConfirmado";
    }
}
