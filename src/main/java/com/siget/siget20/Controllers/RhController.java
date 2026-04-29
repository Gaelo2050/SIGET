package com.siget.siget20.Controllers;

import com.siget.siget20.Model.DTO.*;
import com.siget.siget20.Model.Usuario;
import com.siget.siget20.Repository.SIGET.DocumentoEmitidoRepository;
import com.siget.siget20.Repository.SIGET.Entity.DocumentoEmitido;
import com.siget.siget20.Repository.SIGET.Entity.Solicitud;
import com.siget.siget20.Repository.SIGET.Login;
import com.siget.siget20.Repository.SIGET.SolicitudRepository;
import com.siget.siget20.Services.Login.Imp.RhServiceImp;
import com.siget.siget20.Services.Login.SolicitudRhService;
import com.siget.siget20.Services.Login.ServicioLleneRevisadosRhServices;
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
@RequestMapping("/Rh")
public class RhController {

    @Autowired
    @Qualifier("solicitudRhServiceImp")
    private SolicitudRhService solicitudRhService;

    @Autowired
    @Qualifier("RhService")
    private RhServiceImp rhService;

    @Autowired
    private DocumentoEmitidoRepository documentoEmitidoRepository;

    @Autowired
    @Qualifier("servicioQueLleneRevisadosServices")
    private ServicioLleneRevisadosRhServices servicioLleneRevisadosRhServices;

    @Autowired
    @Qualifier("solicitudRepository")
    private SolicitudRepository solicitudRepository;

    @Autowired
    @Qualifier("loginRepository")
    private Login loginRepository;

    // IDs de tipos de documento en la tabla DOCUMENTOS
    private static final Long ID_DOC_CONSTANCIA                 = 1L;
    private static final Long ID_DOC_TALON_PAGO                 = 9L;
    private static final Long ID_DOC_OFICIO_SABATICO            = 10L;
    private static final Long ID_DOC_CONSTANCIA_LABORAL         = 12L;
    private static final Long ID_DOC_LIBERACION_ACTIVIDADES     = 13L;
    private static final Long ID_DOC_EXCLUSIVIDAD_LABORAL       = 16L;

    // Nombres que se guardan en solicitud.documento_nombre (en minúsculas)
    private static final String DOC_NOMBRE_CONSTANCIA                = "constancia de adscripcion";
    private static final String DOC_NOMBRE_TALON                     = "talon de pago";
    private static final String DOC_NOMBRE_OFICIO_SABATICO           = "oficio sabatico";
    private static final String DOC_NOMBRE_CONSTANCIA_LABORAL        = "constancia laboral";
    private static final String DOC_NOMBRE_LIBERACION_ACTIVIDADES    = "liberacion de actividades";
    private static final String DOC_NOMBRE_EXCLUSIVIDAD_LABORAL      = "exclusividad laboral";

    // ================== QUEJAS ==================
    @GetMapping("/QuejasRh")
    public String QuejasRh(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/Rh/QuejasSIGET";
    }

    @GetMapping("/Quejas")
    public String QuejasAmbarRh(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);
        return "Login/Rh/QuejasAMBAR";
    }

    // ================== EVALUAR / REVISADOS ==================
    @GetMapping("/Evaluar")
    public String RhEvaluar(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<SolicitudRhDto> dto = solicitudRhService.SolicitudRh();
        model.addAttribute("SolicitudRhDto", dto);

        return "Login/RH/RhEvaluar";
    }

    @GetMapping("/Revisados")
    public String RhRevisados(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }
        model.addAttribute("usuario", user);

        List<RevisadoRhDto> revisados = servicioLleneRevisadosRhServices.obtenerRevisadosRh();
        model.addAttribute("revisados", revisados);

        return "Login/RH/RhRevisados";
    }

    // ================== CONFIRMADO ==================
    @GetMapping("/Confirmado/{id}")
    public String RhConfirmado(@PathVariable("id") Long docenteId,
                               @RequestParam(value = "solicitudId", required = false) Long solicitudId,
                               Model model,
                               HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        model.addAttribute("usuario", user);

        List<TablaRhDto> rhList = rhService.InformacionConfirmacion();
        TablaRhDto tablaRhDto = null;

        for (TablaRhDto rh : rhList) {
            if (rh.getDocente_id() != null && rh.getDocente_id().equals(docenteId)) {
                tablaRhDto = rh;
                break;
            }
        }

        model.addAttribute("TablaRhDto", tablaRhDto);

        // Para que el fragmento genérico tenga el id de la solicitud
        if (solicitudId != null) {
            model.addAttribute("solicitudId", solicitudId);
            // Guardamos en sesión para saber qué documento generar
            session.setAttribute("solicitudSeleccionadaRh", solicitudId);
        }

        // Departamento actual
        model.addAttribute("departamento", "Rh");

        return "Login/Rh/RhConfirmar";
    }

    // ================== GENERAR DOCUMENTO (todos los tipos) ==================
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
        List<TablaRhDto> rhList = rhService.InformacionConfirmacion();
        TablaRhDto tablaRhDto = rhList.stream()
                .filter(r -> r.getDocente_id() != null && r.getDocente_id().equals(docenteId))
                .findFirst()
                .orElse(null);

        if (tablaRhDto == null) {
            return "redirect:/Rh/Confirmado/" + docenteId;
        }

        // 2) Determinar la solicitud seleccionada:
        //    primero el parámetro de la URL, luego la sesión, luego la última solicitud de RH
        Long solicitudIdSeleccionada = solicitudId;
        if (solicitudIdSeleccionada == null) {
            solicitudIdSeleccionada = (Long) session.getAttribute("solicitudSeleccionadaRh");
        }

        Solicitud solicitudSeleccionada = null;

        if (solicitudIdSeleccionada != null) {
            solicitudSeleccionada = solicitudRepository.findById(solicitudIdSeleccionada).orElse(null);
            if (solicitudSeleccionada != null &&
                    !docenteId.equals(solicitudSeleccionada.getDocenteId())) {
                // No coincide el docente, la ignoramos
                solicitudSeleccionada = null;
            }
        }

        if (solicitudSeleccionada == null) {
            // Fallback: última solicitud de RH de ese docente
            List<Solicitud> solicitudes = solicitudRepository.findAll();
            solicitudSeleccionada = solicitudes.stream()
                    .filter(s -> "Rh".equalsIgnoreCase(s.getAreaDestino()))
                    .filter(s -> docenteId.equals(s.getDocenteId()))
                    .sorted(Comparator.comparing(Solicitud::getCreadoEn).reversed())
                    .findFirst()
                    .orElse(null);
        }

        // Guardamos en sesión la que finalmente usamos
        if (solicitudSeleccionada != null) {
            session.setAttribute("solicitudSeleccionadaRh", solicitudSeleccionada.getSolicitudId());
        }

        String documentoNombre = (solicitudSeleccionada != null)
                ? solicitudSeleccionada.getDocumentoNombre()
                : null;

        String docNombreNormalizado = documentoNombre != null
                ? documentoNombre.trim().toLowerCase()
                : "";

        // ================= TALÓN DE PAGO =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_TALON)) {

            model.addAttribute("TablaRhDto", tablaRhDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_TALON_PAGO)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_TALON_PAGO);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());

                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            // Datos de ejemplo para el talón
            model.addAttribute("hoy", LocalDate.now());
            model.addAttribute("plaza", "11007402");
            model.addAttribute("montoMensual", "$0,000.00");
            model.addAttribute("periodoPagado", "Enero–Junio 2025");
            model.addAttribute("nombreJefaRH", "LAURA LILIANA BARRAZA CARDENAS");

            return "Login/Rh/talon-pago";
        }

        // ================= OFICIO SABÁTICO =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_OFICIO_SABATICO)) {

            model.addAttribute("TablaRhDto", tablaRhDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_OFICIO_SABATICO)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_OFICIO_SABATICO);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());

                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            model.addAttribute("fechaOficio", LocalDate.now());
            model.addAttribute("departamento", "Departamento de Sistemas y Computación");
            model.addAttribute("nombreDirectora", "NOMBRE DE LA DIRECTORA");

            return "Login/Rh/oficio-sabatico";
        }

        // ================= CONSTANCIA LABORAL =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_CONSTANCIA_LABORAL)) {

            model.addAttribute("TablaRhDto", tablaRhDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_CONSTANCIA_LABORAL)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_CONSTANCIA_LABORAL);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());

                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            // Armar "docente" para el HTML de Constancia Laboral
            ConstanciaLaboralDocenteView docenteView = new ConstanciaLaboralDocenteView(
                    tablaRhDto.getNombre(),
                    tablaRhDto.getRfc(),
                    tablaRhDto.getCategoria(),                    // puesto
                    "Departamento de Sistemas y Computación",     // departamento
                    tablaRhDto.getFechaIng(),
                    tablaRhDto.getEstatusNombramiento()
            );
            model.addAttribute("docente", docenteView);

            LocalDate hoy = LocalDate.now();
            model.addAttribute("fechaActualDia", hoy.getDayOfMonth());
            model.addAttribute("fechaActualMesLargo",
                    hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX")));
            model.addAttribute("fechaActualAnio", hoy.getYear());

            // Datos de RH
            RhInfo rhInfo = new RhInfo("LAURA LILIANA BARRAZA CARDENAS");
            model.addAttribute("rh", rhInfo);

            return "Login/Rh/ConstanciaRH";
        }

        // ================= LIBERACIÓN DE ACTIVIDADES =================
        if (docNombreNormalizado.equals(DOC_NOMBRE_LIBERACION_ACTIVIDADES)) {

            model.addAttribute("TablaRhDto", tablaRhDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_LIBERACION_ACTIVIDADES)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_LIBERACION_ACTIVIDADES);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());

                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            // Docente para el HTML de liberación
            LiberacionActividadesDocenteView docenteView = new LiberacionActividadesDocenteView(
                    tablaRhDto.getNombre(),
                    "S/N", // si luego tienes número de empleado real, lo cambias aquí
                    "Departamento de Sistemas y Computación"
            );
            model.addAttribute("docente", docenteView);

            // Datos de la liberación (por ahora valores de ejemplo)
            LiberacionActividadesInfo liberacionInfo = new LiberacionActividadesInfo(
                    "2025-03-01",
                    "2025-03-15",
                    "participación en curso / comisión académica"
            );
            model.addAttribute("liberacion", liberacionInfo);

            LocalDate hoy = LocalDate.now();
            model.addAttribute("fechaActualDia", hoy.getDayOfMonth());
            model.addAttribute("fechaActualMesLargo",
                    hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX")));
            model.addAttribute("fechaActualAnio", hoy.getYear());

            // Datos de RH y Dirección
            RhInfo rhInfo = new RhInfo("LAURA LILIANA BARRAZA CARDENAS");
            model.addAttribute("rh", rhInfo);

            DireccionInfo direccionInfo = new DireccionInfo("DIRECTOR(A) DEL INSTITUTO");
            model.addAttribute("direccion", direccionInfo);

            return "Login/Rh/LiberacionActividades";
        }


        // ================= EXCLUSIVIDAD LABORAL =================
        if (docNombreNormalizado.contains("exclucividad") && docNombreNormalizado.contains("laboral")) {

            model.addAttribute("TablaRhDto", tablaRhDto);

            DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                    .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_EXCLUSIVIDAD_LABORAL)
                    .orElse(null);

            if (documentoEmitido == null) {
                documentoEmitido = new DocumentoEmitido();
                documentoEmitido.setDocenteId(docenteId);
                documentoEmitido.setDocumentoId(ID_DOC_EXCLUSIVIDAD_LABORAL);
                documentoEmitido.setEstado("GENERADO");
                documentoEmitido.setFirmadoArea(false);
                documentoEmitido.setCreadoEn(LocalDateTime.now());

                documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
            }

            model.addAttribute("documentoEmitido", documentoEmitido);

            ExclusividadLaboralInfo exclusividadInfo = new ExclusividadLaboralInfo(
                    tablaRhDto.getNombre(),
                    tablaRhDto.getRfc(),
                    "110071402 E381500.0100489"
            );
            model.addAttribute("exclusividad", exclusividadInfo);

            LocalDate hoy = LocalDate.now();
            model.addAttribute("fechaActualDia", hoy.getDayOfMonth());
            model.addAttribute("fechaActualMesLargo",
                    hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "MX")));
            model.addAttribute("fechaActualAnio", hoy.getYear());

            return "Login/Rh/exclusividadlaboral";
        }

        // ================= CONSTANCIA DE ADSCRIPCIÓN (por defecto) =================
        model.addAttribute("TablaRhDto", tablaRhDto);

        DocumentoEmitido documentoEmitido = documentoEmitidoRepository
                .findByDocenteIdAndDocumentoId(docenteId, ID_DOC_CONSTANCIA)
                .orElse(null);

        if (documentoEmitido == null) {
            documentoEmitido = new DocumentoEmitido();
            documentoEmitido.setDocenteId(docenteId);
            documentoEmitido.setDocumentoId(ID_DOC_CONSTANCIA);
            documentoEmitido.setEstado("GENERADO");
            documentoEmitido.setFirmadoArea(false);
            documentoEmitido.setCreadoEn(LocalDateTime.now());

            documentoEmitido = documentoEmitidoRepository.save(documentoEmitido);
        }

        model.addAttribute("documentoEmitido", documentoEmitido);

        return "Login/Rh/DocumentoRh";
    }

    // ================== GUARDAR FIRMA (todos los tipos) ==================
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
        List<TablaRhDto> rhList = rhService.InformacionConfirmacion();
        TablaRhDto tablaRhDto = null;

        for (TablaRhDto rh : rhList) {
            if (rh.getDocente_id() != null && rh.getDocente_id().equals(doc.getDocenteId())) {
                tablaRhDto = rh;
                break;
            }
        }

        if (tablaRhDto != null) {
            String htmlFirmado = null;

            if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_CONSTANCIA)) {
                htmlFirmado = generarHtmlConstanciaFirmada(tablaRhDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_TALON_PAGO)) {
                htmlFirmado = generarHtmlTalonFirmado(tablaRhDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_OFICIO_SABATICO)) {
                htmlFirmado = generarHtmlOficioSabaticoFirmado(tablaRhDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_CONSTANCIA_LABORAL)) {
                htmlFirmado = generarHtmlConstanciaLaboralFirmada(tablaRhDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_LIBERACION_ACTIVIDADES)) {
                htmlFirmado = generarHtmlLiberacionActividadesFirmada(tablaRhDto, base64, doc.getFechaFirmaArea());
            } else if (doc.getDocumentoId() != null && doc.getDocumentoId().equals(ID_DOC_EXCLUSIVIDAD_LABORAL)) {
                htmlFirmado = generarHtmlExclusividadLaboralFirmada(tablaRhDto, base64, doc.getFechaFirmaArea());
            }

            if (htmlFirmado != null) {
                doc.setContenidoHtml(htmlFirmado);
            }
        }

        documentoEmitidoRepository.save(doc);

        return "redirect:/Rh/Constancia/" + doc.getDocenteId();
    }

    // ================== APROBAR / RECHAZAR SOLICITUD ==================
    @PostMapping("/Solicitud/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable("id") Long solicitudId,
                                   HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudRhService.aprobarSolicitud(solicitudId);

        return "redirect:/Rh/Evaluar";
    }

    @PostMapping("/Solicitud/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable("id") Long solicitudId,
                                    HttpSession session) {

        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            return "redirect:/login/inicio";
        }

        solicitudRhService.rechazarSolicitud(solicitudId);

        return "redirect:/Rh/Evaluar";
    }

    // ================== GENERADORES DE HTML ==================
    private String generarHtmlConstanciaFirmada(TablaRhDto t,
                                                String base64Firma,
                                                LocalDateTime fechaFirma) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Constancia de adscripción</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h3 style=\"text-align:center;\">A QUIEN CORRESPONDA:</h3>\n");

        sb.append("  <p>\n");
        sb.append("    La que suscribe, Jefa del Departamento de Administración de Recursos Humanos del Instituto Tecnológico de Culiacán,\n");
        sb.append("    hace CONSTAR que el (la) <strong>C. ")
                .append(t.getNombre())
                .append("</strong>, con filiación <em>")
                .append(t.getRfc())
                .append("</em>, presta sus servicios en este Instituto desde el <strong>")
                .append(t.getFechaIng())
                .append("</strong> a la fecha.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Tenía hasta el 1° de Enero de 2024, la(s) categoría(s) de <strong>")
                .append(t.getCategoria())
                .append("</strong>, y actualmente cuenta con estatus de nombramiento <strong>")
                .append(t.getEstatusNombramiento())
                .append("</strong>.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    El (la) maestro(a) antes mencionado(a) cumplió con <strong>")
                .append(t.getPorcentaje())
                .append("%</strong> de su jornada laboral y horario de trabajo, manteniendo la exclusividad <strong>")
                .append(t.getExclusividad())
                .append("</strong>. Respecto a sanciones, se registra: <strong>")
                .append(t.getSancion())
                .append("</strong>.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Se extiende la presente constancia para los fines legales que al(a) interesado(a) convengan,\n");
        sb.append("    en la ciudad de Culiacán, Sinaloa.\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:40px;\">\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma RH\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("    <div>LAURA LILIANA BARRAZA CARDENAS</div>\n");
        sb.append("    <div>JEFA DEL DEPARTAMENTO DE ADMINISTRACIÓN DE RECURSOS HUMANOS</div>\n");
        sb.append("    <div style=\"font-size:11px; margin-top:6px;\">\n");
        sb.append("      Firmado electrónicamente por el área de Recursos Humanos el ")
                .append(fechaFirma.format(formatter))
                .append("\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String generarHtmlTalonFirmado(TablaRhDto t,
                                           String base64Firma,
                                           LocalDateTime fechaFirma) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Talón de pago</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h2 style=\"text-align:center;\">TALÓN DE PAGO DEL PROGRAMA DE ESTÍMULOS</h2>\n");

        sb.append("  <p>Nombre del docente: <strong>")
                .append(t.getNombre())
                .append("</strong></p>\n");
        sb.append("  <p>RFC: <strong>")
                .append(t.getRfc())
                .append("</strong></p>\n");
        sb.append("  <p>CURP: <strong>")
                .append(t.getCurp())
                .append("</strong></p>\n");

        sb.append("  <p>El presente comprobante ampara el pago del estímulo al desempeño del personal docente ");
        sb.append("de acuerdo con las disposiciones del Programa de Estímulos al Desempeño del Personal Docente del TecNM.</p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:40px;\">\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma RH\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("    <div>LAURA LILIANA BARRAZA CARDENAS</div>\n");
        sb.append("    <div>Departamento de Recursos Humanos</div>\n");
        sb.append("    <div style=\"font-size:11px; margin-top:6px;\">\n");
        sb.append("      Firmado electrónicamente el ")
                .append(fechaFirma.format(formatter))
                .append("\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String generarHtmlOficioSabaticoFirmado(TablaRhDto t,
                                                    String base64Firma,
                                                    LocalDateTime fechaFirma) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Oficio de Comisión Sabática</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h2 style=\"text-align:center;\">OFICIO DE COMISIÓN SABÁTICA</h2>\n");

        sb.append("  <p>\n");
        sb.append("    Por este conducto se hace constar que la C. <strong>")
                .append(t.getNombre())
                .append("</strong>, adscrita al <strong>Departamento de Sistemas y Computación</strong>, ");
        sb.append("cuenta con nombramiento <strong>")
                .append(t.getCategoria())
                .append("</strong> y tiene una antigüedad de <strong>")
                .append(t.getFechaIng())
                .append("</strong> en esta institución.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    De acuerdo con la normativa vigente y la autorización del Consejo correspondiente, ");
        sb.append("se le otorga comisión sabática para desarrollar las actividades descritas en su proyecto aprobado.\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:40px;\">\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma Directora\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("    <div>NOMBRE DE LA DIRECTORA</div>\n");
        sb.append("    <div>Directora del Instituto Tecnológico de Culiacán</div>\n");
        sb.append("    <div style=\"font-size:11px; margin-top:6px;\">\n");
        sb.append("      Firmado electrónicamente el ")
                .append(fechaFirma.format(formatter))
                .append("\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String generarHtmlConstanciaLaboralFirmada(TablaRhDto t,
                                                       String base64Firma,
                                                       LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Constancia Laboral</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h2 style=\"text-align:center;\">INSTITUTO TECNOLÓGICO DE CULIACÁN</h2>\n");
        sb.append("  <h3 style=\"text-align:center;\">DEPARTAMENTO DE RECURSOS HUMANOS</h3>\n");
        sb.append("  <hr/>\n");

        sb.append("  <h2 style=\"text-align:center;\">CONSTANCIA LABORAL</h2>\n");

        sb.append("  <p>A quien corresponda:</p>\n");

        sb.append("  <p>\n");
        sb.append("    Por medio de la presente hago constar que el(la) C. <strong>")
                .append(t.getNombre())
                .append("</strong>, con RFC <strong>")
                .append(t.getRfc())
                .append("</strong>, labora en esta institución como <strong>")
                .append(t.getCategoria())
                .append("</strong> adscrito al departamento de <strong>Departamento de Sistemas y Computación</strong>, ");
        sb.append("desde el día <strong>")
                .append(t.getFechaIng())
                .append("</strong>, manteniendo a la fecha un estatus <strong>")
                .append(t.getEstatusNombramiento())
                .append("</strong>.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Se extiende la presente constancia a petición del interesado para los fines legales que a la misma convengan,\n");
        sb.append("    en la ciudad de Culiacán, Sinaloa, a los ")
                .append(dia)
                .append(" días del mes de ")
                .append(mesLargo)
                .append(" de ")
                .append(anio)
                .append(".\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:60px;\">\n");
        sb.append("    <p>ATENTAMENTE</p>\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma RH\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:30px auto 5px auto;\" />\n");
        sb.append("    <div><strong>LAURA LILIANA BARRAZA CARDENAS</strong></div>\n");
        sb.append("    <div>Jefe del Departamento de Recursos Humanos</div>\n");
        sb.append("    <div style=\"font-size:11px; margin-top:6px;\">\n");
        sb.append("      Firmado electrónicamente por el Departamento de Recursos Humanos el ")
                .append(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(".\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String generarHtmlLiberacionActividadesFirmada(TablaRhDto t,
                                                           String base64Firma,
                                                           LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Liberación de actividades</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append("  <h2 style=\"text-align:center;\">INSTITUTO TECNOLÓGICO DE CULIACÁN</h2>\n");
        sb.append("  <h3 style=\"text-align:center;\">DEPARTAMENTO DE RECURSOS HUMANOS</h3>\n");
        sb.append("  <hr/>\n");

        sb.append("  <h2 style=\"text-align:center;\">CONSTANCIA DE LIBERACIÓN DE ACTIVIDADES</h2>\n");

        sb.append("  <p>\n");
        sb.append("    Por este conducto se hace constar que el(la) C. <strong>")
                .append(t.getNombre())
                .append("</strong>, adscrito(a) al departamento de <strong>Departamento de Sistemas y Computación</strong>, ");
        sb.append("queda liberado(a) temporalmente de sus actividades en esta institución, de acuerdo con la programación autorizada.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Se extiende la presente en Culiacán, Sinaloa, a ")
                .append(dia)
                .append(" de ")
                .append(mesLargo)
                .append(" de ")
                .append(anio)
                .append(".\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:60px;\">\n");
        sb.append("    <table style=\"width:100%; text-align:center;\">\n");
        sb.append("      <tr>\n");
        sb.append("        <td>\n");
        sb.append("          <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma RH\" ")
                .append("style=\"width:220px; height:auto; display:block; margin:0 auto 5px auto;\" />\n");
        sb.append("          <div><strong>LAURA LILIANA BARRAZA CARDENAS</strong></div>\n");
        sb.append("          <div>Recursos Humanos</div>\n");
        sb.append("        </td>\n");
        sb.append("      </tr>\n");
        sb.append("    </table>\n");
        sb.append("  </div>\n");

        sb.append("</body>\n");
        sb.append("</html>\n");

        return sb.toString();
    }

    private String generarHtmlExclusividadLaboralFirmada(TablaRhDto t,
                                                         String base64Firma,
                                                         LocalDateTime fechaFirma) {

        LocalDate fecha = fechaFirma.toLocalDate();
        String dia = String.valueOf(fecha.getDayOfMonth());
        String mesLargo = fecha.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "MX"));
        int anio = fecha.getYear();

        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"es\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <title>Carta de Exclusividad Laboral</title>\n");
        sb.append("</head>\n");
        sb.append("<body style=\"font-family:'Times New Roman',serif; font-size:16px; line-height:1.5; margin:50px 70px; text-align:justify;\">\n");

        sb.append("  <div style=\"text-align:right; font-style:italic; margin-bottom:40px;\">\n");
        sb.append("    Culiacán, Sinaloa a ")
                .append(dia)
                .append(" de ")
                .append(mesLargo)
                .append(" de ")
                .append(anio)
                .append("\n");
        sb.append("  </div>\n");

        sb.append("  <div style=\"text-align:center; font-weight:bold; font-size:20px; margin-bottom:5px;\">\n");
        sb.append("    CARTA DE EXCLUSIVIDAD LABORAL\n");
        sb.append("  </div>\n");

        sb.append("  <div style=\"text-align:center; font-weight:bold; font-size:18px; margin-bottom:30px;\">\n");
        sb.append("    DOCENTES CON PLAZA DE TIEMPO COMPLETO\n");
        sb.append("  </div>\n");

        sb.append("  <p>\n");
        sb.append("    El (La) que suscribe <strong>")
                .append(t.getNombre())
                .append("</strong> con filiación: <strong>")
                .append(t.getRfc())
                .append("</strong>, Docente de tiempo completo, con categoría <strong>")
                .append(t.getCategoria())
                .append("</strong>, por medio de este documento manifiesto <strong>MI COMPROMISO</strong> con el Tecnológico Nacional de México, campus <strong>INSTITUTO TECNOLÓGICO DE CULIACÁN</strong>.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Declaro que en caso de haber laborado en otra(s) institución(es) pública(s) o federal(es), la jornada no excedió las 12 horas-semana-mes durante el período a evaluar del estímulo, y en caso de estar laborando actualmente en otra(s) institución(es), la jornada no excederá las 12 horas-semana-mes y los horarios establecidos para el desempeño de las mismas, por lo que autorizo que se revise con el departamento de recursos humanos la compatibilidad de horarios de mi institución de adscripción.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    Asimismo, manifiesto mi disposición para realizar las actividades propias de la Educación Superior Tecnológica enfocadas a satisfacer las necesidades de la dedicación, la calidad en el desempeño y permanencia en las actividades de la docencia, y me comprometo a no incurrir en conflicto de intereses.\n");
        sb.append("  </p>\n");

        sb.append("  <p>\n");
        sb.append("    En caso de que se me compruebe la <strong>NO EXCLUSIVIDAD LABORAL</strong>, me haré acreedor(a) a las sanciones correspondientes de la normatividad vigente y perderé de manera permanente el derecho a participar en el Programa de Estímulos al Desempeño del Personal Docente.\n");
        sb.append("  </p>\n");

        sb.append("  <div style=\"text-align:center; margin-top:60px;\">\n");
        sb.append("    <img src=\"data:image/png;base64,")
                .append(base64Firma)
                .append("\" alt=\"Firma\" ")
                .append("style=\"height:180px; margin-bottom:-20px;\" />\n");
        sb.append("    <div style=\"font-weight:bold; margin-top:-10px;\">")
                .append(t.getNombre())
                .append("</div>\n");
        sb.append("    <div style=\"font-size:14px; margin-top:-5px;\">Docente</div>\n");
        sb.append("  </div>\n");

        sb.append("  <div style=\"margin-top:60px; font-size:12px; text-align:left;\">\n");
        sb.append("    1.- Artículo 8 de los Lineamientos para la Operación del Programa de Estímulos al Desempeño del Personal Docente.\n");
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

        // 1) Generamos el PDF en memoria
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

        // 2) Configuramos la respuesta HTTP y escribimos el PDF
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


