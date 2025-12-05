package scrum.cannia.strategy.impl;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.PetModel;
import scrum.cannia.strategy.DataLoaderStrategy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader implements DataLoaderStrategy {

    @Override
    public List<PetModel> loadData(MultipartFile file) throws Exception {

        List<PetModel> pets = new ArrayList<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;

        br.readLine(); // Saltar cabecera

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            PetModel pet = PetModel.builder()
                    .nombrePet(data[0])
                    .razaPet(data[1])
                    .edadPet(Integer.parseInt(data[2]))
                    .colorPet(data[3])
                    .build();

            pets.add(pet);
        }

        return pets;
    }
}

