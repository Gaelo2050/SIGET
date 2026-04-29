package com.siget.siget20.Controllers;

import com.siget.siget20.Model.DTO.RevisadoRhDto;
import com.siget.siget20.Model.DTO.SolicitudRhDto;
import com.siget.siget20.Model.DTO.TablaDSyCDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.Imp.DSyCServiceImp;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosDSyCServices;
import com.siget.siget20.Services.Login.SolicitudDSyCService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/DSyC")
public class DSyCController {

    @Autowired
    @Qualifier("solicitudDSyCServiceImp")
    private SolicitudDSyCService solicitudDSyCService;

    @Autowired
    @Qualifier("DSyCService")
    private DSyCServiceImp dSyCService;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Autowired
    @Qualifier("servicioQueLleneRevisadosDSyCServices")
    private ServicioLleneRevisadosDSyCServices servicioLleneRevisadosDSyCServices;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    // IDs en la tabla DOCUMENTOS
    private static final Long ID_DOC_INNOVACION_ACADEMICA = 7L;
    private static final Long ID_DOC_RECURSO_EDUCATIVO    = 8L;

    // Nombres que se guardan en SOLICITUD.documento_nombre (en minúsculas)
    private static final String DOC_NOMBRE_INNOVACION_ACADEMICA = "innovacion academica";
    private static final String DOC_NOMBRE_RECURSO_EDUCATIVO    = "recurso educativo digital";

    // ================== QUEJAS ==================
    @GetMapping("/QuejasDSyC")
    public String quejasDSyC(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/DSyC/QuejasSIGET";
    }

    @GetMapping("/Quejas")
    public String quejasAmbarDSyC(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/DSyC/QuejasAMBAR";
    }

    // ================== EVALUAR ==================
    @GetMapping("/Evaluar")
    public String dSycEvaluar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<SolicitudRhDto> dto = solicitudDSyCService.SolicitudDSyC();
        model.addAttribute("SolicitudDSyCDto", dto);

        return "Login/DSyC/DSyCEvaluar";
    }

    // ================== REVISADOS ==================
    @GetMapping("/Revisados")
    public String dSyCRevisados(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);

        List<RevisadoRhDto> revisados = servicioLleneRevisadosDSyCServices.obtenerRevisadosDSyC();
        model.addAttribute("revisados", revisados);

        return "Login/DSyC/DSyCRevisados";
    }

    // ================== CONFIRMADO (info del docente) ==================
    @GetMapping("/Confirmado/{id}")
    public String dSyCConfirmado(@PathVariable("id") Long docenteId,
                                 @RequestParam(value = "solicitudId", required = false) Long solicitudId,
                                 Model model,
                                 HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<TablaDSyCDto> lista = dSyCService.InformacionConfirmacion();
        TablaDSyCDto tablaDSyCDto = null;

        for (TablaDSyCDto d : lista) {
            if (d.getDocente_id() != null && d.getDocente_id().equals(docenteId)) {
                tablaDSyCDto = d;
                break;
            }
        }

        model.addAttribute("TablaDSyCDto", tablaDSyCDto);

        if (solicitudId != null) {
            model.addAttribute("solicitudId", solicitudId);
            session.setAttribute("solicitudSeleccionadaDSyC", solicitudId);
        }

        model.addAttribute("departamento", "DSyC");

        return "Login/DSyC/DSyCConfirmar";
    }

    // ================== GENERAR CONSTANCIA (Innovación / Recurso) ==================
    @GetMapping("/Constancia/{id}")
    public String constanciaRecursoEducativo(@PathVariable("id") Long docenteId,
                                             @RequestParam(value = "solicitudId", required = false) Long solicitudId,
                                             Model model,
                                             HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);

        // 1) Datos del docente desde AMBAR (versión DSyC)
        List<TablaDSyCDto> lista = dSyCService.InformacionConfirmacion();
        TablaDSyCDto tablaDSyCDto = lista.stream()
                .filter(t -> t.getDocente_id() != null && t.getDocente_id().equals(docenteId))
                .findFirst()
                .orElse(null);

        if (tablaDSyCDto == null) {
            return "redirect:/DSyC/Confirmado/" + docenteId;
        }

        // 2) Determinar la solicitud seleccionada
        Long solicitudIdSeleccionada = solicitudId;
        if (solicitudIdSeleccionada == null) {
            solicitudIdSeleccionada = (Long) session.getAttribute("solicitudSeleccionadaDSyC");
        }

        Solicitud solicitudSeleccionada = null;

        if (solicitudIdSeleccionada != null) {
            solicitudSeleccionada = solicitudRepository.findById(solicitudIdSeleccionada).orElse(null);
            if (solicitudSeleccionada != null &&
                    !docenteId.equals(solicitudSeleccionada.getDocenteId())) {
                // No corresponde al docente, la ignoramos
                solicitudSeleccionada = null;
            }
        }

        if (solicitudSeleccionada == null) {
            // fallback: última solicitud del área DSyC para ese docente
            List<Solicitud> solicitudes = solicitudRepository.findAll();
            solicitudSeleccionada = solicitudes.stream()
                    .filter(s -> "DSyC".equalsIgnoreCase(s.getAreaDestino()))
                    .filter(s -> docenteId.equals(s.getDocenteId()))
                    .sorted(Comparator.comparing(Solicitud::getCreadoEn).reversed())
                    .findFirst()
                    .orElse(null);
        }

        if (solicitudSeleccionada != null) {
            session.setAttribute("solicitudSeleccionadaDSyC", solicitudSeleccionada.getSolicitudId());
        }

        // Usamos documento_nombre de la solicitud
        String documentoNombre = (solicitudSeleccionada != null)
                ? solicitudSeleccionada.getDocumentoNombre()
                : null;

        String docNombreNormalizado = documentoNombre != null
                ? documentoNombre.trim().toLowerCase()
                : "";

        // ================== INNOVACIÓN ACADÉMICA ==================
        if (docNombreNormalizado.equals(DOC_NOMBRE_INNOVACION_ACADEMICA)) {

            DocumentoEmitido doc = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_INNOVACION_ACADEMICA)
                    .orElse(null);

            if (doc == null) {
                doc = new DocumentoEmitido();
                doc.setDocenteId(docenteId);
                doc.setDocumentoId(ID_DOC_INNOVACION_ACADEMICA);
                doc.setEstado("GENERADO");
                doc.setFirmadoArea(false);
                doc.setCreadoEn(LocalDateTime.now());
                doc = documentoEmitidoRepository.save(doc);
            }

            model.addAttribute("TablaDSyCDto", tablaDSyCDto);
            model.addAttribute("documentoEmitido", doc);
            model.addAttribute("nombreDocente", tablaDSyCDto.getNombre());

            return "Login/DSyC/InnovacionAcademica";
        }

        // ================== RECURSO EDUCATIVO DIGITAL ==================
        if (docNombreNormalizado.equals(DOC_NOMBRE_RECURSO_EDUCATIVO)) {

            DocumentoEmitido doc = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_RECURSO_EDUCATIVO)
                    .orElse(null);

            if (doc == null) {
                doc = new DocumentoEmitido();
                doc.setDocenteId(docenteId);
                doc.setDocumentoId(ID_DOC_RECURSO_EDUCATIVO);
                doc.setEstado("GENERADO");
                doc.setFirmadoArea(false);
                doc.setCreadoEn(LocalDateTime.now());
                doc = documentoEmitidoRepository.save(doc);
            }

            model.addAttribute("TablaDSyCDto", tablaDSyCDto);
            model.addAttribute("documentoEmitido", doc);
            model.addAttribute("nombreDocente", tablaDSyCDto.getNombre());

            return "Login/DSyC/RecursoEducativo";
        }

        // Si no coincide ninguno, regresamos a confirmar
        return "redirect:/DSyC/Confirmado/" + docenteId;
    }

    // ================== GUARDAR FIRMA DSyC ==================
    @PostMapping("/Documento/Firma/{id}")
    public String guardarFirmaDocumento(@PathVariable("id") Long documentoEmitidoId,
                                        @RequestParam("firma") String dataUrl,
                                        HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        DocumentoEmitido doc = documentoEmitidoRepository.findById(documentoEmitidoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado"));

        String base64 = dataUrl.replace("data:image/png;base64,", "");

        doc.setFirmaBase64(base64);
        doc.setFirmadoArea(true);
        doc.setUsuarioFirmaId(user.getId());
        doc.setFechaFirmaArea(LocalDateTime.now());
        doc.setEstado("FIRMADO");

        // Generar HTML firmado según el tipo de documento
        List<TablaDSyCDto> lista = dSyCService.InformacionConfirmacion();
        TablaDSyCDto tabla = null;

        for (TablaDSyCDto t : lista) {
            if (t.getDocente_id() != null && t.getDocente_id().equals(doc.getDocenteId())) {
                tabla = t;
                break;
            }
        }

        if (tabla != null) {
            String htmlFirmado = null;

            if (doc.getDocumentoId() != null &&
                    doc.getDocumentoId().equals(ID_DOC_RECURSO_EDUCATIVO)) {
                htmlFirmado = generarHtmlRecursoEducativoFirmado(tabla, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null &&
                    doc.getDocumentoId().equals(ID_DOC_INNOVACION_ACADEMICA)) {
                htmlFirmado = generarHtmlInnovacionAcademicaFirmado(tabla, base64, doc.getFechaFirmaArea());
            }

            if (htmlFirmado != null) {
                doc.setContenidoHtml(htmlFirmado);
            }
        }

        documentoEmitidoRepository.save(doc);

        return "redirect:/DSyC/Constancia/" + doc.getDocenteId();
    }

    // ================== APROBAR / RECHAZAR SOLICITUD ==================
    @PostMapping("/Solicitud/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId,
                                   HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudDSyCService.aprobarSolicitudDSyC(solicitudId);

        return "redirect:/DSyC/Evaluar";
    }

    @PostMapping("/Solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId,
                                    HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudDSyCService.rechazarSolicitudDSyC(solicitudId);

        return "redirect:/DSyC/Evaluar";
    }

    // ================== GENERAR HTML FIRMADO (RECURSO EDUCATIVO) ==================
    private String generarHtmlRecursoEducativoFirmado(TablaDSyCDto t,
                                                      String base64Firma,
                                                      LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\" />\n<title>Constancia de Recurso Educativo</title>\n");
        sb.append("<style>")
                .append("body{margin:0;padding:20px;font-family:'Times New Roman',serif;background:#e5e5e5;}")
                .append(".page{width:800px;margin:0 auto;background:#fff;padding:130px 60px 40px 60px;}")
                .append(".header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:40px;}")
                .append(".header-left-text{font-size:13px;line-height:1.2;}")
                .append(".header-left-text span{display:block;}")
                .append(".header-left-text .title{font-size:22px;font-weight:bold;}")
                .append(".header-right{text-align:right;font-size:12px;line-height:1.3;}")
                .append(".main-title{margin-top:30px;text-align:left;font-size:13px;font-weight:bold;text-transform:uppercase;line-height:1.5;}")
                .append(".main-title span{display:block;}")
                .append(".body-text{margin-top:25px;font-size:13px;text-align:justify;line-height:1.5;}")
                .append(".closing{margin-top:25px;font-size:13px;text-align:justify;line-height:1.5;}")
                .append(".atentamente{margin-top:40px;font-size:12px;text-align:left;letter-spacing:2px;}")
                .append(".atentamente small{display:block;font-size:10px;letter-spacing:normal;margin-top:3px;}")
                .append(".signature-center{margin-top:55px;text-align:center;font-size:12px;text-transform:uppercase;font-weight:bold;line-height:1.3;}")
                .append(".signature-row{margin-top:70px;display:flex;justify-content:space-between;font-size:11px;text-transform:uppercase;font-weight:bold;text-align:center;}")
                .append(".signature-row div{width:45%;line-height:1.3;}")
                .append(".footer{margin-top:70px;display:flex;align-items:center;justify-content:space-between;font-size:10px;}")
                .append(".footer-center{text-align:center;line-height:1.3;margin-left:40px;}")
                .append(".signature-center::before,.signature-row div::before{content:'';display:block;margin:0 auto 4px auto;width:60%;border-top:1px solid #000;}")
                .append("</style>\n");
        sb.append("</head>\n<body>\n<div class=\"page\">\n");

        // ENCABEZADO
        sb.append("<div class=\"header\">")
                .append("<div class=\"header-left-text\">")
                .append("<span class=\"title\">Educación</span>")
                .append("<span>Secretaría de Educación Pública</span>")
                .append("</div>")
                .append("<div class=\"header-right\">")
                .append("<div>Instituto Tecnológico de Culiacán</div><br/>")
                .append("<div>Culiacán, Sinaloa, <strong>")
                .append(dia).append(" de ").append(mesLargo).append(" de ").append(anio)
                .append("</strong></div>")
                .append("<div>OFICIO No.: <strong>DSyC-388/2025</strong></div>")
                .append("<div><strong>ASUNTO: Constancia.</strong></div>")
                .append("</div>")
                .append("</div>");

        // TÍTULO
        sb.append("<div class=\"main-title\">")
                .append("<span>COMISIÓN DE EVALUACIÓN DEL</span>")
                .append("<span>PROGRAMA DE ESTÍMULOS AL DESEMPEÑO DEL PERSONAL DOCENTE</span>")
                .append("<span>PARA LOS INSTITUTOS TECNOLÓGICOS FEDERALES Y CENTROS.</span>")
                .append("<span>PRESENTE.</span>")
                .append("</div>");

        // CUERPO
        sb.append("<div class=\"body-text\">")
                .append("Por medio del presente se hace constar que la C. <strong>")
                .append(t.getNombre())
                .append("</strong>, elaboró durante el semestre <strong>enero-junio 2025</strong> ")
                .append("un recurso educativo digital afín al contenido de una asignatura de su programa educativo, ")
                .append("aprobado y utilizado por la academia correspondiente, contando con la rúbrica de evaluación respectiva.")
                .append("</div>");

        sb.append("<div class=\"closing\">")
                .append("Se extiende la presente en la ciudad de Culiacán, Sinaloa, a los trece días del mes de junio del año dos mil veinticinco.")
                .append("</div>");

        sb.append("<div class=\"atentamente\">")
                .append("A T E N T A M E N T E<br/>")
                .append("<small>Excelencia en Educación Tecnológica®</small>")
                .append("</div>");

        // FIRMA DSyC CON IMAGEN
        sb.append("<div class=\"signature-center\">")
                .append("<img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma\" style=\"height:120px;display:block;margin:0 auto 5px auto;\" />")
                .append("MARISOL MANJARREZ BELTRÁN<br/>")
                .append("JEFA DEL DEPTO. DE SISTEMAS Y COMPUTACIÓN")
                .append("</div>");

        // OTRAS FIRMAS (solo texto)
        sb.append("<div class=\"signature-row\">")
                .append("<div>ARCELIA JUDITH BUSTILLOS MARTÍNEZ<br/>")
                .append("PRESIDENTA DE ACADEMIA DE ING. EN SISTEMAS<br/>COMPUTACIONALES</div>")
                .append("<div>BERTHA LUCÍA PATRÓN ARELLANO<br/>")
                .append("RESPONSABLE DEL DESPACHO DE LA SUBDIRECCIÓN<br/>ACADÉMICA</div>")
                .append("</div>");

        // FOOTER
        sb.append("<div class=\"footer\">")
                .append("<div></div>")
                .append("<div class=\"footer-center\">")
                .append("Juan de Dios Bátiz 310 Pte. Col. Guadalupe C.P. 80220<br/>")
                .append("Culiacán, Sinaloa. Tel. 667-454-0100<br/>")
                .append("tecnm.mx &#160;&#160; www.culiacan.tecnm.mx")
                .append("</div>")
                .append("</div>");

        sb.append("</div></body></html>");

        return sb.toString();
    }

    // ================== GENERAR HTML FIRMADO (INNOVACIÓN ACADÉMICA) ==================
    private String generarHtmlInnovacionAcademicaFirmado(TablaDSyCDto t,
                                                         String base64Firma,
                                                         LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\" />\n<title>Constancia DSyC - Innovación Académica</title>\n");
        sb.append("<style>")
                .append("body{background:#f5f5f5;font-family:'Times New Roman',serif;margin:0;padding:0;}")
                .append(".page{width:820px;margin:20px auto;background:#ffffff;padding:40px 55px 35px;border:1px solid #d0d0d0;box-sizing:border-box;}")
                .append("header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:35px;}")
                .append(".logos{font-size:12px;}")
                .append(".logos .titulo{font-size:26px;font-weight:700;}")
                .append(".logos .subtitulo{font-size:12px;text-transform:uppercase;}")
                .append(".right-header{text-align:right;font-size:12px;}")
                .append(".right-header .tec{font-weight:bold;margin-bottom:45px;}")
                .append(".right-header .line{margin-bottom:3px;}")
                .append(".title-block{text-align:left;font-size:13px;margin-bottom:18px;}")
                .append(".title-block .line{text-transform:uppercase;font-weight:bold;}")
                .append(".title-block .presente{margin-top:6px;font-weight:bold;}")
                .append(".body-text{font-size:13px;text-align:justify;margin-bottom:15px;}")
                .append(".atentamente{margin-top:30px;margin-bottom:50px;font-size:12px;}")
                .append(".atentamente .excelencia{font-size:10px;letter-spacing:1px;}")
                .append(".firmas{display:flex;justify-content:space-between;margin-top:40px;font-size:11px;text-align:center;}")
                .append(".firma{width:32%;}")
                .append(".firma .nombre{margin-top:55px;font-weight:bold;text-transform:uppercase;}")
                .append(".firma .puesto{margin-top:3px;}")
                .append("footer{border-top:1px solid #c0c0c0;margin-top:40px;padding-top:8px;display:flex;justify-content:space-between;align-items:flex-end;font-size:10px;}")
                .append(".domicilio{width:70%;text-align:center;line-height:1.2;}")
                .append("</style>\n");
        sb.append("</head>\n<body>\n<div class=\"page\">\n");

        // HEADER
        sb.append("<header>")
                .append("<div class=\"logos\">")
                .append("<div class=\"titulo\">Educación</div>")
                .append("<div class=\"subtitulo\">Secretaría de Educación Pública</div>")
                .append("</div>")
                .append("<div class=\"right-header\">")
                .append("<div class=\"tec\">Instituto Tecnológico de Culiacán</div>")
                .append("<div class=\"line\">Culiacán, Sinaloa, ")
                .append(dia).append("/").append(mesLargo).append("/").append(anio).append("</div>")
                .append("<div class=\"line\">OFICIO No.: DSyC-391/2025</div>")
                .append("<div class=\"line\"><strong>ASUNTO: Constancia</strong></div>")
                .append("</div>")
                .append("</header>");

        // TÍTULO
        sb.append("<section class=\"title-block\">")
                .append("<div class=\"line\">COMISIÓN DE EVALUACIÓN DEL</div>")
                .append("<div class=\"line\">PROGRAMA DE ESTÍMULOS AL DESEMPEÑO DEL PERSONAL DOCENTE</div>")
                .append("<div class=\"line\">PARA LOS INSTITUTOS TECNOLÓGICOS FEDERALES Y CENTROS.</div>")
                .append("<div class=\"presente\">P R E S E N T E</div>")
                .append("</section>");

        // CUERPO
        sb.append("<p class=\"body-text\">")
                .append("Por medio del presente se hace constar que el C. <strong>")
                .append(t.getNombre())
                .append("</strong> implementó estrategias didácticas innovadoras en el aula por asignatura, ")
                .append("logrando la participación activa de los estudiantes en su proceso de aprendizaje y ")
                .append("fortaleciendo el desarrollo de sus habilidades técnicas y socioemocionales.")
                .append("</p>");

        sb.append("<p class=\"body-text\">")
                .append("Las estrategias implementadas cuentan con la rúbrica de evaluación correspondiente y ")
                .append("han sido avaladas por la academia del programa educativo.")
                .append("</p>");

        // ATENTAMENTE
        sb.append("<div class=\"atentamente\">")
                .append("<strong>A T E N T A M E N T E</strong><br/>")
                .append("<span class=\"excelencia\">Excelencia en Educación Tecnológica®</span>")
                .append("</div>");

        // FIRMAS (incluimos firma escaneada en la primera)
        sb.append("<div class=\"firmas\">");

        // Firma 1 con imagen
        sb.append("<div class=\"firma\">")
                .append("<img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma\" style=\"height:120px;display:block;margin:0 auto 5px auto;\" />")
                .append("<div class=\"nombre\">MARISOL MANJARREZ BELTRÁN</div>")
                .append("<div class=\"puesto\">JEFA DEL DEPTO. DE SISTEMAS Y COMPUTACIÓN</div>")
                .append("</div>");

        // Firma 2
        sb.append("<div class=\"firma\">")
                .append("<div class=\"nombre\">ARCELIA JUDITH BUSTILLOS MARTÍNEZ</div>")
                .append("<div class=\"puesto\">PRESIDENTA DE ACADEMIA DE ING. EN SISTEMAS<br/>COMPUTACIONALES</div>")
                .append("</div>");

        // Firma 3
        sb.append("<div class=\"firma\">")
                .append("<div class=\"nombre\">BERTHA LUCÍA PATRÓN ARELLANO</div>")
                .append("<div class=\"puesto\">RESPONSABLE DEL DESPACHO DE LA<br/>SUBDIRECCIÓN ACADÉMICA</div>")
                .append("</div>");

        sb.append("</div>"); // cierre .firmas

        // FOOTER
        sb.append("<footer>")
                .append("<div class=\"anio\"></div>")
                .append("<div class=\"domicilio\">")
                .append("Juan de Dios Bátiz 310 Pte. Col. Guadalupe C.P. 80220<br/>")
                .append("Culiacán, Sinaloa. Tel. 667-454-0100<br/>")
                .append("tecnm.mx &#160; | &#160; www.culiacan.tecnm.mx")
                .append("</div>")
                .append("</footer>");

        sb.append("</div></body></html>");

        return sb.toString();
    }

    // ================== DESCARGAR PDF ==================
    @GetMapping(value = "/Documento/Pdf/{id}", produces = "application/pdf")
    public void descargarDocumentoPdf(@PathVariable("id") Long documentoEmitidoId,
                                      HttpServletResponse response) throws Exception {

        DocumentoEmitido doc = documentoEmitidoRepository.findById(documentoEmitidoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento emitido no encontrado"));

        String htmlOriginal = doc.getContenidoHtml();
        if (htmlOriginal == null || htmlOriginal.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "El documento no tiene contenido HTML para generar el PDF.");
            return;
        }

        // Evitar problemas con &nbsp;
        String html = htmlOriginal
                .replace("&nbsp;", "&#160;")
                .replace("&nbsp", "&#160");

        byte[] pdfBytes;
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {

            com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder =
                    new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();

            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();

            pdfBytes = baos.toByteArray();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al generar el PDF: " + e.getMessage());
            return;
        }

        if (pdfBytes == null || pdfBytes.length == 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "El PDF se generó vacío.");
            return;
        }

        response.setContentType("application/pdf");
        String nombreArchivo = "documento_" + documentoEmitidoId + ".pdf";
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentLength(pdfBytes.length);

        try (java.io.OutputStream os = response.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }
    }
}

