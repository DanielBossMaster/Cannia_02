package scrum.cannia.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import scrum.cannia.model.HistoriaClinicaModel;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.PropietarioModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelExportService {

    /**
     * Genera un archivo Excel con la historia clínica
     * @param historiaClinica Datos de la historia clínica
     * @param mascota Información de la mascota
     * @param propietario Información del propietario
     * @return InputStream del archivo Excel listo para descargar
     */
    public ByteArrayInputStream exportHistoriaClinicaToExcel(HistoriaClinicaModel historiaClinica,
                                                             MascotaModel mascota,
                                                             PropietarioModel propietario) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historia Clínica");

            // Estilos para el título
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Crear filas y celdas
            int rowNum = 0;

            // Título
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HISTORIA CLÍNICA VETERINARIA");
            titleCell.setCellStyle(titleStyle);

            // Información del propietario
            rowNum++; // línea en blanco
            Row propietarioHeaderRow = sheet.createRow(rowNum++);
            propietarioHeaderRow.createCell(0).setCellValue("INFORMACIÓN DEL PROPIETARIO");
            propietarioHeaderRow.getCell(0).setCellStyle(headerStyle);

            crearFila(sheet, rowNum++, "Nombre:", propietario.getNombrePro());
            crearFila(sheet, rowNum++, "Documento:", propietario.getNumDoc());
            crearFila(sheet, rowNum++, "Teléfono:", propietario.getTelefonoPro());
            crearFila(sheet, rowNum++, "Correo:", propietario.getCorreoPro());

            // Información de la mascota
            rowNum++;
            Row mascotaHeaderRow = sheet.createRow(rowNum++);
            mascotaHeaderRow.createCell(0).setCellValue("INFORMACIÓN DE LA MASCOTA");
            mascotaHeaderRow.getCell(0).setCellStyle(headerStyle);

            crearFila(sheet, rowNum++, "Nombre:", mascota.getNomMascota());
            crearFila(sheet, rowNum++, "Especie:", mascota.getEspecie());
            crearFila(sheet, rowNum++, "Raza:", mascota.getRaza());
            crearFila(sheet, rowNum++, "Color:", mascota.getColor());

            // Información de la historia clínica
            rowNum++;
            Row historiaHeaderRow = sheet.createRow(rowNum++);
            historiaHeaderRow.createCell(0).setCellValue("HISTORIA CLÍNICA");
            historiaHeaderRow.getCell(0).setCellStyle(headerStyle);

            crearFila(sheet, rowNum++, "Fecha y Hora:",
                    historiaClinica.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            crearFila(sheet, rowNum++, "Peso (kg):", historiaClinica.getPeso().toString());

            // Anamnesis (en múltiples líneas)
            rowNum++;
            Row anamnesisHeaderRow = sheet.createRow(rowNum++);
            anamnesisHeaderRow.createCell(0).setCellValue("ANAMNESIS");
            anamnesisHeaderRow.getCell(0).setCellStyle(headerStyle);

            Row anamnesisRow = sheet.createRow(rowNum++);
            anamnesisRow.createCell(0).setCellValue(historiaClinica.getAnamnesis());

            // Diagnóstico
            rowNum++;
            Row diagnosticoHeaderRow = sheet.createRow(rowNum++);
            diagnosticoHeaderRow.createCell(0).setCellValue("DIAGNÓSTICO");
            diagnosticoHeaderRow.getCell(0).setCellStyle(headerStyle);

            Row diagnosticoRow = sheet.createRow(rowNum++);
            diagnosticoRow.createCell(0).setCellValue(historiaClinica.getDiagnostico());

            // Tratamiento
            rowNum++;
            Row tratamientoHeaderRow = sheet.createRow(rowNum++);
            tratamientoHeaderRow.createCell(0).setCellValue("TRATAMIENTO");
            tratamientoHeaderRow.getCell(0).setCellStyle(headerStyle);

            Row tratamientoRow = sheet.createRow(rowNum++);
            tratamientoRow.createCell(0).setCellValue(historiaClinica.getTratamiento());

            // Ajustar el ancho de las columnas
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convertir a ByteArrayInputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para crear filas con etiqueta y valor
     */
    private void crearFila(Sheet sheet, int rowNum, String etiqueta, String valor) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(etiqueta);
        row.createCell(1).setCellValue(valor != null ? valor : "");
    }
}