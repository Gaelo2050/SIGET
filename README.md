# SIGET 2.0

Sistema de Gestión de Trámites para Docentes del Instituto Tecnológico de Culiacán (SIGET 2.0).  
Aplicación web desarrollada con **Spring Boot** y **Thymeleaf** que permite a las distintas áreas (Docente, Recursos Humanos, Desarrollo Académico, DSyC, Servicios Escolares, etc.) gestionar solicitudes y documentos de manera centralizada.

> **Nota:** Este repositorio contiene únicamente el código de la aplicación.  
> Las bases de datos (SIGET / AMBAR) deben configurarse de forma local en tu entorno.

---

## Características principales

- Autenticación de usuarios y acceso por **área**:
    - Docente
    - Recursos Humanos (RH)
    - Desarrollo Académico
    - DSyC
    - Servicios Escolares
    - Otras áreas administrativas según la configuración del sistema.

- Registro y seguimiento de **solicitudes** de documentos:
    - Constancias
    - Nombramientos
    - Exclusividad laboral
    - Otros documentos académicos/administrativos

- **Validaciones automáticas** antes de que el área apruebe:
    - Estatus del docente (por ejemplo, debe ser `ACTIVO`).
    - Porcentaje de asistencia mínimo.
    - Horas trabajadas (por ejemplo, al menos 40 horas registradas).
    - Sanciones activas.
    - Vigencia como docente.
    - Periodo escolar activo.

- **Programación orientada a aspectos (AOP)**:
    - Restricciones de acceso en ciertos horarios para tareas de mantenimiento.
    - Ejemplo: permitir ingreso sólo de *7 a 7* (u otro rango configurado) durante ventanas específicas de mantenimiento.
    - Uso de **anotaciones de Spring Boot** para aplicar estas reglas de forma transversal.

- **Firmas digitales con Canvas en HTML**:
    - La docente o el usuario puede **firmar el documento** directamente en la interfaz usando un `<canvas>`.
    - La firma se captura y se asocia al documento.
    - El documento firmado se devuelve a la docente/área correspondiente.

- **Generación de documentos en PDF**:
    - A partir de plantillas HTML (Thymeleaf) almacenadas en base de datos o en archivos.
    - Se renderiza el HTML y se genera un PDF descargable para el usuario.

- **Envío de correos electrónicos automáticos**:
    - Notificaciones a docentes cuando su solicitud cambia de estado (por ejemplo, aprobada, rechazada, en revisión).
    - Notificaciones a áreas de soporte (SIGET / AMBAR) cuando se requiere revisión o seguimiento.
    - Integración con un servidor SMTP configurable (por ejemplo, cuenta institucional).

- Vistas específicas por área, por ejemplo:
    - Docente: solicitar documentos, ver historial, ver estado de solicitudes.
    - RH, DSyC, Desarrollo Académico, Servicios Escolares: evaluar, confirmar, rechazar y generar documentos.

- Integración con una segunda base de datos (**AMBAR**) para obtener información complementaria de docentes (periodos, asistencias, sanciones, etc.).

---

## 🛠 Tecnologías utilizadas

- **Backend**
    - Java 21 (o la versión configurada en `pom.xml`)
    - Spring Boot
        - Spring Web
        - Spring Data JPA
        - Spring Thymeleaf
        - Spring Validation
        - Spring AOP (programación orientada a aspectos)

- **Frontend**
    - Thymeleaf
    - HTML5, CSS3
    - Uso de `<canvas>` para captura de firmas digitales

- **Base de datos**
    - Microsoft SQL Server
        - Base de datos **SIGET**
        - Base de datos **AMBAR**

- **Otros**
    - Maven para gestión de dependencias y construcción
    - Herramientas de generación de PDF a partir de HTML (por ejemplo, OpenHTMLtoPDF / PDFBox)

---