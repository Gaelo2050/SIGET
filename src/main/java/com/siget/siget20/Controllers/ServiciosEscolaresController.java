package com.siget.siget20.Controllers;

import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Model.DTO.RevisadoServiciosEscolaresDto;
import com.siget.siget20.Model.DTO.TablaServiciosEscolaresDto;
import com.siget.siget20.Repository.AMBAR.Entity.MateriasDocente;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosServiciosEscolaresServices;
import com.siget.siget20.Services.Login.ServiciosEscolaresService;
import com.siget.siget20.Repository.AMBAR.MateriasDocenteRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/ServiciosEscolares")
public class ServiciosEscolaresController {

    @Autowired
    @Qualifier("solicitudServiciosEscolaresServiceImp")
    private com.siget.siget20.Services.Login.SolicitudServiciosEscolaresService solicitudServiciosEscolaresService;

    @Autowired
    @Qualifier("ServiciosEscolaresService")
    com.siget.siget20.Services.Login.Imp.ServiciosEscolaresServiceImp serviciosEscolaresService;

    @Autowired
    @Qualifier("servicioQueLleneRevisadosServiciosEscolaresServices")
    private ServicioLleneRevisadosServiciosEscolaresServices servicioLleneRevisadosServiciosEscolaresServices;

    @Autowired
    private MateriasDocenteRepository materiasDocenteRepository;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    // ID del tipo de documento "Constancia de Servicios Escolares"
    private static final Long ID_DOC_SERVICIOS_ESCOLARES = 2L;

    //quejas-SIGET
    @GetMapping("/QuejasRh")
    public String QuejasRh(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/ServiciosEscolares/QuejasSIGET";
    }

    //quejasAmbar
    @GetMapping("/Quejas")
    public String QuejasAmbar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/ServiciosEscolares/QuejasAMBAR";
    }

    //vista Evaluar
    @GetMapping("/Evaluar")
    public String Evaluar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<com.siget.siget20.Model.DTO.SolicitudServiciosEscolaresDto> dto = solicitudServiciosEscolaresService.SolicitudServiciosEscolares();
        model.addAttribute("SolicitudServiciosDto", dto);
        return "Login/ServiciosEscolares/ServiciosEscolaresEvaluar";
    }

    @GetMapping("/Revisados")
    public String ServiciosEscolaresRevisados(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        model.addAttribute("departamento", "ServiciosEscolares");

        List<RevisadoServiciosEscolaresDto> revisados = servicioLleneRevisadosServiciosEscolaresServices.obtenerRevisadosServiciosEscolares();
        model.addAttribute("revisadosServiciosEscolares", revisados);

        return "Login/ServiciosEscolares/ServiciosEscolaresRevisados";
    }

    //vista Confirmado
    @GetMapping("/Confirmado/{id}")
    public String Confirmado(@PathVariable("id") Long id,
                             @org.springframework.web.bind.annotation.RequestParam(value = "solicitudId", required = false) Long solicitudId,
                             Model model,
                             HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<com.siget.siget20.Model.DTO.TablaServiciosEscolaresDto> list = serviciosEscolaresService.InformacionConfirmacion();
        com.siget.siget20.Model.DTO.TablaServiciosEscolaresDto tablaDto = null;

        for (com.siget.siget20.Model.DTO.TablaServiciosEscolaresDto item : list) {
            if (item.getDocente_id() != null && item.getDocente_id().equals(id)) {
                tablaDto = item;
                break;
            }
        }

        if (solicitudId != null) {
            model.addAttribute("solicitudId", solicitudId);
        }
        model.addAttribute("TablaServiciosDto", tablaDto);
        model.addAttribute("departamento", "ServiciosEscolares");

        // Obtener las materias del docente
        if (tablaDto != null && tablaDto.getDocente_id() != null) {
            List<MateriasDocente> materias = materiasDocenteRepository.findByDocente_DocenteId(tablaDto.getDocente_id());
            model.addAttribute("materias", materias);
        }

        return "Login/ServiciosEscolares/ServiciosEscolaresConfirmar";
    }

    @GetMapping("/Constancia/{id}")
    public String constanciaDocente(@PathVariable("id") Long docenteId,
                                    Model model,
                                    HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<TablaServiciosEscolaresDto> seList = serviciosEscolaresService.InformacionConfirmacion();
        TablaServiciosEscolaresDto tablaSEDto = null;

        for (TablaServiciosEscolaresDto se : seList) {
            if (se.getDocente_id() != null && se.getDocente_id().equals(docenteId)) {
                tablaSEDto = se;
                break;
            }
        }

        if (tablaSEDto == null) {
            return "redirect:/ServiciosEscolares/Confirmado/" + docenteId;
        }

        model.addAttribute("TablaServiciosDto", tablaSEDto);

        // Obtener las materias del docente
        if (tablaSEDto.getDocente_id() != null) {
            List<MateriasDocente> materias = materiasDocenteRepository.findByDocente_DocenteId(tablaSEDto.getDocente_id());
            model.addAttribute("materias", materias);
        }

        // Buscar si ya existe un DocumentoEmitido para este docente + tipo de documento
        DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_SERVICIOS_ESCOLARES)
                .orElse(null);

        // Si NO existe, lo creamos
        if (documentoEmitido == null) {
            documentoEmitido = new DocumentoEmitido();
            documentoEmitido.setDocenteId(docenteId);
            documentoEmitido.setDocumentoId(ID_DOC_SERVICIOS_ESCOLARES);
            documentoEmitido.setEstado("GENERADO");
            documentoEmitido.setFirmadoArea(false);
            documentoEmitido.setCreadoEn(LocalDateTime.now());

            documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
        }

        model.addAttribute("documentoEmitido", documentoEmitido);

        return "Login/ServiciosEscolares/DocumentoServiciosEscolares";
    }

    @PostMapping("/Solicitud/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId,
                                   HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudServiciosEscolaresService.aprobarSolicitud(solicitudId);

        return "redirect:/ServiciosEscolares/Evaluar";
    }

    @PostMapping("/Solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId,
                                    HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudServiciosEscolaresService.rechazarSolicitud(solicitudId);

        return "redirect:/ServiciosEscolares/Evaluar";
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

        // Generar y guardar el HTML firmado
        List<TablaServiciosEscolaresDto> seList = serviciosEscolaresService.InformacionConfirmacion();
        TablaServiciosEscolaresDto tablaSEDto = null;

        for (TablaServiciosEscolaresDto se : seList) {
            if (se.getDocente_id() != null && se.getDocente_id().equals(doc.getDocenteId())) {
                tablaSEDto = se;
                break;
            }
        }

        if (tablaSEDto != null) {
            List<MateriasDocente> materias = materiasDocenteRepository.findByDocente_DocenteId(doc.getDocenteId());
            String htmlFirmado = generarHtmlConstanciaFirmada(tablaSEDto, materias, base64, doc.getFechaFirmaArea());
            doc.setContenidoHtml(htmlFirmado);
        }

        documentoEmitidoRepository.save(doc);

        return "redirect:/ServiciosEscolares/Constancia/" + doc.getDocenteId();
    }

    private String generarHtmlConstanciaFirmada(TablaServiciosEscolaresDto t,
                                                List<MateriasDocente> materias,
                                                String base64Firma,
                                                LocalDateTime fechaFirma) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Constancia</title>\n");
        sb.append("  <style>\n");
        sb.append("    body { margin: 40px 55px; font-family: Arial, sans-serif; font-size: 14px; }\n");
        sb.append("    table { width: 100%; border-collapse: collapse; margin-top: 25px; font-size: 13px; }\n");
        sb.append("    th, td { border: 1px solid black; padding: 4px 6px; text-align: center; }\n");
        sb.append("  </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h3 style=\"text-align:center;\">COMISIÓN DE EVALUACIÓN DEL TECNM</h3>\n");
        sb.append("  <h4 style=\"text-align:center;\">PROGRAMA DE ESTÍMULOS AL DESEMPEÑO DEL PERSONAL DOCENTE</h4>\n");

        sb.append("  <p>\n");
        sb.append("    La que suscribe, hace constar que según registros que existen en el archivo escolar, el/la C. ");
        sb.append("<strong>").append(t.getNombre()).append("</strong>, impartió las siguientes materias ");
        sb.append("durante los Periodos Enero-Junio y Agosto-Diciembre del año 2024:\n");
        sb.append("  </p>\n");

        sb.append("  <table>\n");
        sb.append("    <tr>\n");
        sb.append("      <th>PERIODO</th>\n");
        sb.append("      <th>NIVEL</th>\n");
        sb.append("      <th>CLAVE DE LA MATERIA</th>\n");
        sb.append("      <th>NOMBRE DE LA MATERIA</th>\n");
        sb.append("      <th>ALUMNOS ATENDIDOS</th>\n");
        sb.append("    </tr>\n");

        int totalAlumnos = 0;
        if (materias != null) {
            for (MateriasDocente m : materias) {
                sb.append("    <tr>\n");
                sb.append("      <td>").append(m.getPeriodo() != null ? m.getPeriodo() : "").append("</td>\n");
                sb.append("      <td>").append(m.getNivel() != null ? m.getNivel() : "").append("</td>\n");
                sb.append("      <td>").append(m.getClave() != null ? m.getClave() : "").append("</td>\n");
                sb.append("      <td>").append(m.getNombre() != null ? m.getNombre() : "").append("</td>\n");
                sb.append("      <td>").append(m.getAlumnos() != null ? m.getAlumnos() : 0).append("</td>\n");
                sb.append("    </tr>\n");
                if (m.getAlumnos() != null) {
                    totalAlumnos += m.getAlumnos();
                }
            }
        }

        sb.append("    <tr>\n");
        sb.append("      <td colspan=\"4\" style=\"text-align:right; font-weight:bold;\">Total</td>\n");
        sb.append("      <td><strong>").append(totalAlumnos).append("</strong></td>\n");
        sb.append("    </tr>\n");
        sb.append("  </table>\n");

        sb.append("  <p>\n");
        sb.append("    Se extiende la presente, en la ciudad de Culiacán, Sinaloa, a los nueve días del mes de junio de dos mil veinticinco, ");
        sb.append("para los fines que más convengan al interesado.\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:40px;\">\n");
        sb.append("    <p><strong>A T E N T A M E N T E</strong></p>\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma Servicios Escolares\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("    <div>Dinorah García</div>\n");
        sb.append("    <div>JEFA DEL DEPARTAMENTO DE SERVICIOS ESCOLARES</div>\n");
        sb.append("    <div style=\"font-size:11px; margin-top:6px;\">\n");
        sb.append("      Firmado electrónicamente por el área de Servicios Escolares el ")
                .append(fechaFirma.format(formatter))
                .append("\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    // ================== DESCARGAR PDF ==================
    @GetMapping(value = "/Documento/Pdf/{id}", produces = "application/pdf")
    public void descargarConstanciaPdf(@PathVariable("id") Long documentoEmitidoId,
                                       HttpServletResponse response) throws Exception {

        DocumentoEmitido doc = documentoEmitidoRepository.findById(documentoEmitidoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento emitido no encontrado"));

        String html = doc.getContenidoHtml();
        if (html == null || html.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "El documento no tiene contenido HTML para generar el PDF.");
            return;
        }

        // Generamos el PDF en memoria
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

        // Configuramos la respuesta HTTP y escribimos el PDF
        response.setContentType("application/pdf");
        String nombreArchivo = "constancia_servicios_escolares_" + documentoEmitidoId + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
        response.setContentLength(pdfBytes.length);

        try (java.io.OutputStream os = response.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }
    }

}
