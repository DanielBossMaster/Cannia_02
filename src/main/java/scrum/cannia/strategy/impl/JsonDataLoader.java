package scrum.cannia.strategy.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.PetModel;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.util.Arrays;
import java.util.List;


public class JsonDataLoader implements DataLoaderStrategy {

    @Override
    public List<PetModel> loadData(MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        return Arrays.asList(mapper.readValue(
                file.getInputStream(),
                PetModel[].class
        ));
    }
}
