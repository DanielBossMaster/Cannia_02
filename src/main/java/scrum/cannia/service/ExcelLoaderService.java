package scrum.cannia.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.PetModel;
import scrum.cannia.repository.PetRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelLoaderService {

    public static void loadPetsFromExcel(MultipartFile file, PetRepository petRepository) throws Exception {
        List<PetModel> pets = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Limpiar tabla si quieres reemplazar datos anteriores
        // petRepository.deleteAll();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // Empieza en fila 1 (fila 0 = encabezados)
            Row row = sheet.getRow(i);

            if (row == null) continue; // Saltar filas vacías

            // Leer celdas
            Cell nombreCell = row.getCell(0);
            Cell razaCell = row.getCell(1);
            Cell edadCell = row.getCell(2);
            Cell colorCell = row.getCell(3);

            // Validar que no estén vacías
            if (nombreCell == null || razaCell == null || edadCell == null || colorCell == null)
                continue;

            // Crear mascota
            PetModel pet = new PetModel();
            pet.setNombrePet(nombreCell.getStringCellValue());
            pet.setRazaPet(razaCell.getStringCellValue());

            // Edad: asegurarse que sea número
            if (edadCell.getCellType() == CellType.NUMERIC) {
                pet.setEdadPet((int) edadCell.getNumericCellValue());
            } else {
                try {
                    pet.setEdadPet(Integer.parseInt(edadCell.getStringCellValue()));
                } catch (Exception e) {
                    continue; // Si la edad no es válida, se salta
                }
            }

            pet.setColorPet(colorCell.getStringCellValue());

            // Guardar en la BD
            petRepository.save(pet);
        }

        workbook.close();
    }
}

