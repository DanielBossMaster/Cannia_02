package scrum.cannia.strategy.impl;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader implements DataLoaderStrategy {

    @Override
    public List<MascotaCargaDTO> loadData(MultipartFile file) throws Exception {

        List<MascotaCargaDTO> mascotas = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;

        br.readLine(); // Saltar cabecera

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            MascotaCargaDTO dto = new MascotaCargaDTO();
            dto.setNombre(data[0]);
            dto.setRaza(data[1]);
            dto.setEdad(Integer.parseInt(data[2]));
            dto.setColor(data[3]);

            mascotas.add(dto);
        }

        return mascotas;
    }
}


