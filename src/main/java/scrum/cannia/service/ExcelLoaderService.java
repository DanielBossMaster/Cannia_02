package scrum.cannia.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//@Service
//public class ExcelLoaderService {
//
//    public List<Pet> loadPetsFromExcel(InputStream inputStream) throws IOException {
//        List<Pet> pets = new ArrayList<>();
//
//        Workbook workbook = new XSSFWorkbook(inputStream);
//        Sheet sheet = workbook.getSheetAt(0);
//
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // empieza desde fila 1
//            Row row = sheet.getRow(i);
//
//            if (row == null) continue;
//
//            Pet pet = new Pet();
//            pet.setName(row.getCell(0).getStringCellValue());
//            pet.setBreed(row.getCell(1).getStringCellValue());
//            pet.setAge((int) row.getCell(2).getNumericCellValue());
//            pet.setColor(row.getCell(3).getStringCellValue());
//            pet.setDescription(row.getCell(4).getStringCellValue());
//
//            pets.add(pet);
//        }
//
//        workbook.close();
//        return pets;
//    }
//}

