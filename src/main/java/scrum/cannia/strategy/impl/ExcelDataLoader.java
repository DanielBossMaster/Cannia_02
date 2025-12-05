package scrum.cannia.strategy.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.PetModel;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelDataLoader implements DataLoaderStrategy {

    @Override
    public List<PetModel> loadData(MultipartFile file) throws Exception {

        List<PetModel> pets = new ArrayList<>();

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

            PetModel pet = PetModel.builder()
                    .nombrePet(nombreCell.getStringCellValue())
                    .razaPet(razaCell.getStringCellValue())
                    .edadPet(edad)
                    .colorPet(colorCell.getStringCellValue())
                    .build();

            pets.add(pet);
        }

        workbook.close();
        return pets;
    }
}

