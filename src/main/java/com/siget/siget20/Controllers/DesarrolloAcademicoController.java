package com.siget.siget20.Controllers;

import com.siget.siget20.Model.DTO.RevisadoRhDto;
import com.siget.siget20.Model.DTO.SolicitudRhDto;
import com.siget.siget20.Model.DTO.TablaDesarrolloAcademicoDto;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.Imp.DesarrolloAcademicoServiceImp;
import com.siget.siget20.Services.Login.Imp.SolicitudDesarrolloAcademicoServiceImp;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosDesarrolloAcademicoServices;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/DesarrolloAcademico")
public class DesarrolloAcademicoController {

    @Autowired
    @Qualifier("solicitudDesarrolloAcademicoServiceImp")
    private SolicitudDesarrolloAcademicoServiceImp solicitudDesarrolloAcademicoService;

    @Autowired
    @Qualifier("DesarrolloAcademicoService")
    private DesarrolloAcademicoServiceImp desarrolloAcademicoService;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Autowired
    @Qualifier("servicioQueLleneRevisadosDesarrolloAcademicoServices")
    private ServicioLleneRevisadosDesarrolloAcademicoServices servicioLleneRevisadosDesarrolloAcademicoServices;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    // IDs en la tabla DOCUMENTOS (ajusta si tu BD cambia)
    private static final Long ID_DOC_ACTIVIDADES_TUTORIAS = 5L;
    private static final Long ID_DOC_CVU                  = 6L;

    // Nombres en SOLICITUD.documento_nombre (en minúsculas)
    private static final String DOC_NOMBRE_ACTIVIDADES_TUTORIAS = "actividades-tutorias";
    private static final String DOC_NOMBRE_CVU                  = "cvu";

    // ================== QUEJAS ==================
    @GetMapping("/QuejasRh")
    public String QuejasRh(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/DesarrolloAcademico/QuejasSIGET";
    }

    @GetMapping("/Quejas")
    public String QuejasAmbar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/DesarrolloAcademico/QuejasAMBAR";
    }

    // ================== EVALUAR / REVISADOS ==================
    @GetMapping("/Evaluar")
    public String DesarrolloAcademicoEvaluar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<SolicitudRhDto> dto = solicitudDesarrolloAcademicoService.SolicitudDesarrollo();
        model.addAttribute("SolicitudDesarrolloDto", dto);

        return "Login/DesarrolloAcademico/DesarrolloAcademicoEvaluar";
    }

    @GetMapping("/Revisados")
    public String DesarrolloAcademicoRevisados(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);

        List<RevisadoRhDto> revisados = servicioLleneRevisadosDesarrolloAcademicoServices.obtenerRevisadosDesarrollo();
        model.addAttribute("revisados", revisados);

        return "Login/DesarrolloAcademico/DesarrolloAcademicoRevisados";
    }

    // ================== CONFIRMADO ==================
    @GetMapping("/Confirmado/{id}")
    public String Confirmado(@PathVariable("id") Long docenteId,
                             @RequestParam(value = "solicitudId", required = false) Long solicitudId,
                             Model model,
                             HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<TablaDesarrolloAcademicoDto> list = desarrolloAcademicoService.InformacionConfirmacion();
        TablaDesarrolloAcademicoDto tablaDto = null;

        for (TablaDesarrolloAcademicoDto item : list) {
            if (item.getDocente_id() != null && item.getDocente_id().equals(docenteId)) {
                tablaDto = item;
                break;
            }
        }

        model.addAttribute("TablaDesarrolloDto", tablaDto);

        if (solicitudId != null) {
            model.addAttribute("solicitudId", solicitudId);
            session.setAttribute("solicitudSeleccionadaDesarrollo", solicitudId);
        }

        model.addAttribute("departamento", "DesarrolloAcademico");

        return "Login/DesarrolloAcademico/DesarrolloAcademicoConfirmar";
    }

    // ================== GENERAR DOCUMENTO (Actividades / CVU) ==================
    @GetMapping("/Constancia/{id}")
    public String constanciaODocumento(@PathVariable("id") Long docenteId,
                                       @RequestParam(value = "solicitudId", required = false) Long solicitudId,
                                       Model model,
                                       HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        // 1) Datos del docente desde AMBAR
        List<TablaDesarrolloAcademicoDto> list = desarrolloAcademicoService.InformacionConfirmacion();
        TablaDesarrolloAcademicoDto tablaDto = list.stream()
                .filter(t -> t.getDocente_id() != null && t.getDocente_id().equals(docenteId))
                .findFirst()
                .orElse(null);

        if (tablaDto == null) {
            return "redirect:/DesarrolloAcademico/Confirmado/" + docenteId;
        }

        // 2) Determinar la solicitud seleccionada
        Long solicitudIdSeleccionada = solicitudId;
        if (solicitudIdSeleccionada == null) {
            solicitudIdSeleccionada = (Long) session.getAttribute("solicitudSeleccionadaDesarrollo");
        }

        Solicitud solicitudSeleccionada = null;

        if (solicitudIdSeleccionada != null) {
            solicitudSeleccionada = solicitudRepository.findById(solicitudIdSeleccionada).orElse(null);
            if (solicitudSeleccionada != null &&
                    !docenteId.equals(solicitudSeleccionada.getDocenteId())) {
                solicitudSeleccionada = null;
            }
        }

        if (solicitudSeleccionada == null) {
            // fallback: última solicitud de DesarrolloAcademico del docente
            List<Solicitud> solicitudes = solicitudRepository.findAll();
            solicitudSeleccionada = solicitudes.stream()
                    .filter(s -> "DesarrolloAcademico".equalsIgnoreCase(s.getAreaDestino()))
                    .filter(s -> docenteId.equals(s.getDocenteId()))
                    .sorted(Comparator.comparing(Solicitud::getCreadoEn).reversed())
                    .findFirst()
                    .orElse(null);
        }

        if (solicitudSeleccionada != null) {
            session.setAttribute("solicitudSeleccionadaDesarrollo", solicitudSeleccionada.getSolicitudId());
        }

        String documentoNombre = (solicitudSeleccionada != null)
                ? solicitudSeleccionada.getDocumentoNombre()
                : null;

        String docNombreNormalizado = documentoNombre != null
                ? documentoNombre.trim().toLowerCase()
                : "";

        // ================= ACTIVIDADES - TUTORÍAS =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_ACTIVIDADES_TUTORIAS)) {

            model.addAttribute("TablaDesarrolloDto", tablaDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_ACTIVIDADES_TUTORIAS)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_ACTIVIDADES_TUTORIAS);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());
                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            // Datos específicos para el PDF de actividades
            model.addAttribute("nombreResponsableAcad", "BERTHA LUCÍA PATRON ARELLANO");
            model.addAttribute("leyendaPie", "2025, Año de la Mujer Indígena");

            return "Login/DesarrolloAcademico/actividades_tutorias";
        }

        // ================= CONSTANCIA DE CVU =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_CVU)) {

            model.addAttribute("TablaDesarrolloDto", tablaDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_CVU)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_CVU);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());
                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            // Datos para la constancia de CVU
            LocalDate hoy = LocalDate.now();
            String fechaLarga = hoy.getDayOfMonth() + " de " +
                    hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX")) +
                    " de " + hoy.getYear();

            model.addAttribute("nombreDocente", tablaDto.getNombre());
            model.addAttribute("fechaLarga", fechaLarga);
            model.addAttribute("nombreJefaDesarrollo", "MARÍA HIDAAELIA SÁNCHEZ LÓPEZ");
            model.addAttribute("numeroOficio", "DDA-000-00-2025");
            model.addAttribute("registroCvu", "IT19B258");
            model.addAttribute("anioCvu", "2025");
            model.addAttribute("urlCvu", "https://cvu.dpii.tecnm.mx");

            return "Login/DesarrolloAcademico/constancia_cvu";
        }

        // Si por alguna razón no coincide, regresamos a confirmar
        return "redirect:/DesarrolloAcademico/Confirmado/" + docenteId;
    }

    // ================== GUARDAR FIRMA ==================
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

        // Generar HTML firmado
        List<TablaDesarrolloAcademicoDto> list = desarrolloAcademicoService.InformacionConfirmacion();
        TablaDesarrolloAcademicoDto tablaDto = null;

        for (TablaDesarrolloAcademicoDto t : list) {
            if (t.getDocente_id() != null && t.getDocente_id().equals(doc.getDocenteId())) {
                tablaDto = t;
                break;
            }
        }

        if (tablaDto != null) {
            String htmlFirmado = null;

            if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_ACTIVIDADES_TUTORIAS)) {
                htmlFirmado = generarHtmlActividadesTutoriasFirmado(tablaDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_CVU)) {
                htmlFirmado = generarHtmlConstanciaCvuFirmado(tablaDto, base64, doc.getFechaFirmaArea());
            }

            if (htmlFirmado != null) {
                doc.setContenidoHtml(htmlFirmado);
            }
        }

        documentoEmitidoRepository.save(doc);

        return "redirect:/DesarrolloAcademico/Constancia/" + doc.getDocenteId();
    }

    // ================== APROBAR / RECHAZAR SOLICITUD ==================
    @PostMapping("/Solicitud/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId,
                                   HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        // AQUÍ el cambio
        solicitudDesarrolloAcademicoService.aprobarSolicitudDesarrollo(solicitudId);

        return "redirect:/DesarrolloAcademico/Evaluar";
    }

    @PostMapping("/Solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId,
                                    HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        // AQUÍ el cambio
        solicitudDesarrolloAcademicoService.rechazarSolicitudDesarrollo(solicitudId);

        return "redirect:/DesarrolloAcademico/Evaluar";
    }

    // ================== GENERAR HTML FIRMADO ==================
    private String generarHtmlActividadesTutoriasFirmado(TablaDesarrolloAcademicoDto t,
                                                         String base64Firma,
                                                         LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\" />\n<title>Constancia de actividades de tutorías</title>\n");
        sb.append("</head>\n<body style=\"font-family:'Times New Roman',serif;\">\n");

        sb.append("<h3 style=\"text-align:right;\">Culiacán, Sinaloa, ")
                .append(dia).append(" de ").append(mesLargo).append(" de ").append(anio).append("</h3>\n");

        sb.append("<h3>COMISIÓN DE EVALUACIÓN DEL PROGRAMA DE ESTÍMULOS AL DESEMPEÑO DEL PERSONAL DOCENTE</h3>\n");
        sb.append("<h4>P R E S E N T E</h4>\n");

        sb.append("<p>Por medio del presente se hace constar que el(la) C. <strong>")
                .append(t.getNombre())
                .append("</strong> realizó actividades de tutoría y seguimiento académico ")
                .append("durante el periodo evaluado, cumpliendo satisfactoriamente con las responsabilidades asignadas.</p>\n");

        sb.append("<p>Se extiende la presente para los fines académicos que al interesado convengan.</p>\n");

        sb.append("<div style=\"text-align:center; margin-top:60px;\">\n");
        sb.append("<img src=\"data:image/png;base64,").append(base64Firma)
                .append("\" alt=\"Firma\" style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("<div><strong>BERTHA LUCÍA PATRON ARELLANO</strong></div>\n");
        sb.append("<div>Responsable del Despacho de la Subdirección Académica</div>\n");
        sb.append("</div>\n");

        sb.append("</body>\n</html>\n");

        return sb.toString();
    }

    private String generarHtmlConstanciaCvuFirmado(TablaDesarrolloAcademicoDto t,
                                                   String base64Firma,
                                                   LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\" />\n<title>Constancia de CVU</title>\n");
        sb.append("</head>\n<body style=\"font-family:'Times New Roman',serif;\">\n");

        sb.append("<h3 style=\"text-align:right;\">Culiacán, Sinaloa, ")
                .append(dia).append(" de ").append(mesLargo).append(" de ").append(anio).append("</h3>\n");

        sb.append("<h3>H. COMISIÓN DICTAMINADORA DEL EDD</h3>\n");
        sb.append("<h4>P R E S E N T E</h4>\n");

        sb.append("<p>La que suscribe, Jefa del Departamento de Desarrollo Académico del Instituto Tecnológico de Culiacán, ");
        sb.append("hace constar que el(la) C. <strong>")
                .append(t.getNombre())
                .append("</strong> cuenta con su Currículum Vitae actualizado en el sistema CVU-TecNM, ");
        sb.append("correspondiente al año evaluado.</p>\n");

        sb.append("<p>Lo anterior se certifica para los fines académicos y administrativos que al interesado convengan.</p>\n");

        sb.append("<div style=\"text-align:center; margin-top:60px;\">\n");
        sb.append("<img src=\"data:image/png;base64,").append(base64Firma)
                .append("\" alt=\"Firma\" style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("<div><strong>MARÍA HIDAELIA SÁNCHEZ LÓPEZ</strong></div>\n");
        sb.append("<div>Jefa del Departamento de Desarrollo Académico</div>\n");
        sb.append("</div>\n");

        sb.append("</body>\n</html>\n");

        return sb.toString();
    }

    // ================== DESCARGAR PDF ==================
    @GetMapping(value = "/Documento/Pdf/{id}", produces = "application/pdf")
    public void descargarDocumentoPdf(@PathVariable("id") Long documentoEmitidoId,
                                      HttpServletResponse response) throws Exception {

        DocumentoEmitido doc = documentoEmitidoRepository.findById(documentoEmitidoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento emitido no encontrado"));

        String html = doc.getContenidoHtml();
        if (html == null || html.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "El documento no tiene contenido HTML para generar el PDF.");
            return;
        }

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
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentLength(pdfBytes.length);

        try (java.io.OutputStream os = response.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }
    }
}
