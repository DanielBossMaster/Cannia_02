package scrum.cannia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import scrum.cannia.model.HistoriaClinicaModel;
import scrum.cannia.model.VacunaModel;
import scrum.cannia.model.PropietarioModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.repository.HistoriaClinicaRepository;
import scrum.cannia.repository.VacunaRepository;
import scrum.cannia.repository.MascotaRepository;
import scrum.cannia.repository.PropietarioRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
public class HistoriaClinicaController {

    @Autowired
    private VacunaRepository vacunaRepository;

    @Autowired
    private HistoriaClinicaRepository historiaRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    // ★★★ ENDPOINT CORREGIDO PARA OBTENER HISTORIAS CLÍNICAS ★★★
    @GetMapping("/obtenerHistoriasClinicas/{mascotaId}")
    @ResponseBody
    public ResponseEntity<?> obtenerHistoriasClinicas(@PathVariable Long mascotaId) {
        System.out.println(" Solicitando historias para mascota: " + mascotaId);

        try {
            List<HistoriaClinicaModel> historias = historiaRepository.findByMascotaIdOrderByFechaHoraDesc(mascotaId);
            System.out.println(" Historias encontradas: " + historias.size());

            // Crear una lista simplificada para evitar problemas de serialización
            List<Map<String, Object>> historiasSimplificadas = new ArrayList<>();

            for (HistoriaClinicaModel historia : historias) {
                Map<String, Object> historiaMap = new HashMap<>();
                historiaMap.put("idHistoriaClinica", historia.getIdHistoriaClinica());
                historiaMap.put("fechaHora", historia.getFechaHora());
                historiaMap.put("peso", historia.getPeso());
                historiaMap.put("anamnesis", historia.getAnamnesis());
                historiaMap.put("diagnostico", historia.getDiagnostico());
                historiaMap.put("tratamiento", historia.getTratamiento());

                // Solo información básica de la mascota para evitar relaciones circulares
                if (historia.getMascota() != null) {
                    Map<String, Object> mascotaMap = new HashMap<>();
                    mascotaMap.put("idMascota", historia.getMascota().getId());
                    mascotaMap.put("nomMascota", historia.getMascota().getNomMascota());
                    historiaMap.put("mascota", mascotaMap);
                }

                historiasSimplificadas.add(historiaMap);
            }

            return ResponseEntity.ok(historiasSimplificadas);

        } catch (Exception e) {
            System.out.println(" Error en controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>()); // Devolver lista vacía en caso de error
        }
    }

    @GetMapping("/descargarHistoriasPorFecha")
    public void descargarHistoriasPorFecha(@RequestParam String fecha, HttpServletResponse response) {
        // Este endpoint puedes mantenerlo o eliminarlo según lo necesites
        // Por ahora lo dejamos vacío
    }

    /**
     * Guardar vacuna
     */
    @PostMapping("/guardarVacuna")
    public String guardarVacuna(
            @ModelAttribute VacunaModel vacuna,
            @RequestParam("idMascota") Long idMascota) {

        // Buscar la mascota
        MascotaModel mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // Relacionar la vacuna con la mascota
        vacuna.setMascota(mascota);

        // Guardar vacuna
        vacunaRepository.save(vacuna);

        return "redirect:/veterinario/historiaclinica";
    }

    /**
     * Ver propietario con sus mascotas
     */
    @GetMapping("/propietario/{id}")
    public String verPropietario(@PathVariable Long id, Model model) {
        PropietarioModel propietario = propietarioRepository.findById(id).orElse(null);
        List<MascotaModel> mascotas = mascotaRepository.findByPropietarioId(id);

        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotas);
        // 👇 agregar el objeto vacío para que el formulario no falle
        model.addAttribute("historiaClinica", new HistoriaClinicaModel());

        return "veterinario/historiaclinica";
    }

    // ★★★ ENDPOINT PARA DESCARGAR HISTORIAS POR RANGO DE FECHAS ★★★
    @GetMapping("/descargarHistoriasPorRangoFechas")
    public void descargarHistoriasPorRangoFechas(
            @RequestParam Long mascotaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            HttpServletResponse response) throws IOException {

        try {
            System.out.println("Generando Excel para mascota: " + mascotaId +
                    " desde: " + fechaInicio + " hasta: " + fechaFin);

            // Convertir fechas String a LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime fechaInicioDateTime = LocalDate.parse(fechaInicio, formatter).atStartOfDay();
            LocalDateTime fechaFinDateTime = LocalDate.parse(fechaFin, formatter).atTime(23, 59, 59);

            // Buscar la mascota y su propietario
            MascotaModel mascota = mascotaRepository.findById(mascotaId)
                    .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

            PropietarioModel propietario = mascota.getPropietario();

            // Buscar historias en el rango de fechas
            List<HistoriaClinicaModel> historias = historiaRepository
                    .findByMascotaIdAndFechaHoraBetweenOrderByFechaHoraDesc(
                            mascotaId, fechaInicioDateTime, fechaFinDateTime);

            System.out.println("Historias encontradas: " + historias.size());

            // Crear el libro de Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Historias Clínicas");

            // Estilos para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Estilo para información de la mascota
            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setBold(true);
            infoFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            infoStyle.setFont(infoFont);

            // ★★★ INFORMACIÓN DE LA MASCOTA Y PROPIETARIO ★★★
            int rowNum = 0;

            // Fila 1: Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HISTORIAS CLÍNICAS - " + mascota.getNomMascota().toUpperCase());
            titleCell.setCellStyle(headerStyle);

            // Fila 2: Información de la mascota
            Row mascotaInfoRow1 = sheet.createRow(rowNum++);
            mascotaInfoRow1.createCell(0).setCellValue("Mascota:");
            mascotaInfoRow1.createCell(1).setCellValue(mascota.getNomMascota());
            mascotaInfoRow1.createCell(0).setCellStyle(infoStyle);

            // Fila 3: Especie y raza
            Row mascotaInfoRow2 = sheet.createRow(rowNum++);
            mascotaInfoRow2.createCell(0).setCellValue("Especie:");
            mascotaInfoRow2.createCell(1).setCellValue(mascota.getEspecie() != null ? mascota.getEspecie() : "N/A");
            mascotaInfoRow2.createCell(3).setCellValue("Raza:");
            mascotaInfoRow2.createCell(4).setCellValue(mascota.getRaza() != null ? mascota.getRaza() : "N/A");
            mascotaInfoRow2.getCell(0).setCellStyle(infoStyle);
            mascotaInfoRow2.getCell(3).setCellStyle(infoStyle);

            // Fila 4: Propietario
            Row propietarioRow = sheet.createRow(rowNum++);
            propietarioRow.createCell(0).setCellValue("Propietario:");
            propietarioRow.createCell(1).setCellValue(propietario != null ? propietario.getNombrePro() : "N/A");
            propietarioRow.createCell(3).setCellValue("Documento:");
            propietarioRow.createCell(4).setCellValue(propietario != null ? propietario.getNumDoc() : "N/A");
            propietarioRow.getCell(0).setCellStyle(infoStyle);
            propietarioRow.getCell(3).setCellStyle(infoStyle);

            // Fila 5: Contacto
            Row contactoRow = sheet.createRow(rowNum++);
            contactoRow.createCell(0).setCellValue("Teléfono:");
            contactoRow.createCell(1).setCellValue(propietario != null ? propietario.getTelefonoPro() : "N/A");
            contactoRow.createCell(3).setCellValue("Correo:");
            contactoRow.createCell(4).setCellValue(propietario != null ? propietario.getCorreoPro() : "N/A");
            contactoRow.getCell(0).setCellStyle(infoStyle);
            contactoRow.getCell(3).setCellStyle(infoStyle);

            // Fila 6: Rango de fechas
            Row fechasRow = sheet.createRow(rowNum++);
            fechasRow.createCell(0).setCellValue("Período:");
            fechasRow.createCell(1).setCellValue(fechaInicio + " a " + fechaFin);
            fechasRow.getCell(0).setCellStyle(infoStyle);

            // Fila vacía de separación
            rowNum++;

            // ★★★ ENCABEZADOS DE LAS HISTORIAS CLÍNICAS ★★★
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                    "ID Historia", "Fecha y Hora", "Peso (kg)",
                    "Anamnesis", "Diagnóstico", "Tratamiento"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ★★★ LLENAR DATOS DE LAS HISTORIAS CLÍNICAS ★★★
            for (HistoriaClinicaModel historia : historias) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(historia.getIdHistoriaClinica());

                if (historia.getFechaHora() != null) {
                    row.createCell(1).setCellValue(historia.getFechaHora().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                } else {
                    row.createCell(1).setCellValue("N/A");
                }

                row.createCell(2).setCellValue(historia.getPeso() != null ? historia.getPeso() : 0);
                row.createCell(3).setCellValue(historia.getAnamnesis() != null ? historia.getAnamnesis() : "");
                row.createCell(4).setCellValue(historia.getDiagnostico() != null ? historia.getDiagnostico() : "");
                row.createCell(5).setCellValue(historia.getTratamiento() != null ? historia.getTratamiento() : "");
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar respuesta
            String nombreArchivo = "historias_clinicas_" + mascota.getNomMascota() +
                    "_" + fechaInicio + "_a_" + fechaFin + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

            // Escribir el workbook en la respuesta
            workbook.write(response.getOutputStream());
            workbook.close();

            System.out.println("Excel generado exitosamente: " + nombreArchivo);

        } catch (Exception e) {
            System.out.println("Error generando Excel: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error generando el archivo Excel: " + e.getMessage());
        }
    }

    /**
     * Guardar historia clínica
     */
    @PostMapping("/guardarHistoria")
    public ResponseEntity<Map<String, Object>> guardarHistoria(
            @ModelAttribute HistoriaClinicaModel historia,
            @RequestParam("mascotaId") Long mascotaId) {

        MascotaModel mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        historia.setMascota(mascota);

        HistoriaClinicaModel historiaGuardada = historiaRepository.save(historia);

        Map<String, Object> response = new HashMap<>();
        response.put("id", historiaGuardada.getIdHistoriaClinica());
        response.put("mensaje", "Historia guardada con éxito");

        return ResponseEntity.ok(response);
    }
}