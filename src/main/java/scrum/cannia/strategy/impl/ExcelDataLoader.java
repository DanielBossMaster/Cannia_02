package scrum.cannia.strategy.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.dto.MascotaCargaDTO;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelDataLoader implements DataLoaderStrategy {

    @Override
    public List<MascotaCargaDTO> loadData(MultipartFile file) throws Exception {

        List<MascotaCargaDTO> mascotas = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell nombreCell = row.getCell(0);
            Cell razaCell = row.getCell(1);
            Cell edadCell = row.getCell(2);
            Cell colorCell = row.getCell(3);

            if (nombreCell == null || razaCell == null || edadCell == null || colorCell == null)
                continue;
            int edad;
            if (edadCell.getCellType() == CellType.NUMERIC) {
                edad = (int) edadCell.getNumericCellValue();
            } else {
                continue;
            }

            MascotaCargaDTO dto = new MascotaCargaDTO();
            dto.setNombre(nombreCell.getStringCellValue());
            dto.setRaza(razaCell.getStringCellValue());
            dto.setEdad(edad);
            dto.setColor(colorCell.getStringCellValue());

            mascotas.add(dto);
        }

        workbook.close();
        return mascotas;
    }
}

