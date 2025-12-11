package scrum.cannia.strategy.impl;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.Dto.ErrorCargaDTO;
import scrum.cannia.model.Genero;
import scrum.cannia.model.TipoEstadoMascota;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.io.InputStream;
import java.util.*;

public class ExcelDataLoader implements DataLoaderStrategy {

    private static final List<String> ENCABEZADOS_OBLIGATORIOS = Arrays.asList(
            "nombre", "especie", "raza", "genero", "edad",
            "color", "tipoestado", "fechanacimiento", "fechavacunacion",
            "foto", "medicamento"
    );

    @Override
    public List<MascotaCargaDTO> loadData(MultipartFile file) throws Exception {

        List<MascotaCargaDTO> lista = new ArrayList<>();
        List<ErrorCargaDTO> errores = new ArrayList<>();

        InputStream is = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);

        if (sheet == null) {
            throw new RuntimeException("El archivo Excel está vacío o no tiene hojas.");
        }

        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) {
            throw new RuntimeException("El archivo Excel no contiene filas.");
        }

        Row rowEncabezados = rowIterator.next();
        Map<String, Integer> indiceColumnas = validarEncabezados(rowEncabezados);

        int numeroFila = 1;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            numeroFila++;

            try {
                MascotaCargaDTO dto = leerFila(row, indiceColumnas);
                lista.add(dto);

            } catch (Exception e) {
                throw new RuntimeException("Error en fila " + numeroFila + ": " + e.getMessage());
            }
        }

        workbook.close();

        if (!errores.isEmpty()) {

            StringBuilder mensaje = new StringBuilder("Errores encontrados:\n");

            for (ErrorCargaDTO err : errores) {
                mensaje.append("Fila ").append(err.getFila()).append(": ")
                        .append(err.getMensaje()).append("\n");
            }

            throw new RuntimeException(mensaje.toString());
        }
        return lista;
    }

    private Map<String, Integer> validarEncabezados(Row encabezadosRow) {

        Map<String, Integer> mapa = new HashMap<>();

        for (Cell cell : encabezadosRow) {
            String nombre = cell.getStringCellValue().trim().toLowerCase();
            mapa.put(nombre, cell.getColumnIndex());
        }

        // Validación fuerte — todos los encabezados deben existir
        for (String requerido : ENCABEZADOS_OBLIGATORIOS) {
            if (!mapa.containsKey(requerido)) {
                throw new RuntimeException("Falta el encabezado obligatorio: " + requerido);
            }
        }

        return mapa;
    }

    private MascotaCargaDTO leerFila(Row row, Map<String, Integer> columnas) throws Exception {

        MascotaCargaDTO dto = new MascotaCargaDTO();

        dto.setNombre(leerString(row, columnas.get("nombre")));
        dto.setEspecie(leerString(row, columnas.get("especie")));
        dto.setRaza(leerString(row, columnas.get("raza")));
        String g = leerString(row, columnas.get("genero"));
        dto.setGenero(convertirGenero(g));
        String edadStr = leerString(row, columnas.get("edad"));
        dto.setEdad(convertirEdad(edadStr));
        dto.setColor(leerString(row, columnas.get("color")));
        String t = leerString(row, columnas.get("tipoestado"));
        dto.setTipoEstado(convertirTipoEstado(t));
        dto.setFechaNacimiento(leerFecha(row, columnas.get("fechanacimiento")));
        dto.setFechaVacunacion(leerFecha(row, columnas.get("fechavacunacion")));
        dto.setFoto(leerString(row, columnas.get("foto")));
        dto.setMedicamento(leerString(row, columnas.get("medicamento")));

        return dto;
    }

    private String leerString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }

        return cell.getStringCellValue().trim();
    }

    private Date leerFecha(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }

        throw new RuntimeException("La columna fecha debe ser de tipo fecha válida.");
    }

    private Genero convertirGenero(String valor) {
        if (valor == null) throw new RuntimeException("El género es obligatorio.");

        valor = valor.trim().toUpperCase();

        try {
            return Genero.valueOf(valor);
        } catch (Exception e) {
            throw new RuntimeException("Género inválido: " + valor);
        }
    }

    private TipoEstadoMascota convertirTipoEstado(String valor) {
        if (valor == null) throw new RuntimeException("El tipoEstado es obligatorio.");

        valor = valor.trim().toUpperCase();

        try {
            return TipoEstadoMascota.valueOf(valor);
        } catch (Exception e) {
            throw new RuntimeException("TipoEstado inválido: " + valor);
        }
    }

    private Integer convertirEdad(String valor) {
        if (valor == null || valor.isEmpty()) return null;

        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            throw new RuntimeException("Edad inválida: " + valor);
        }
    }
}



