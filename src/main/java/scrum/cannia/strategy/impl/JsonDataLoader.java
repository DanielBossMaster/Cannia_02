package scrum.cannia.strategy.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.MascotaCargaDTO;
import scrum.cannia.strategy.DataLoaderStrategy;

import java.util.List;

public class JsonDataLoader implements DataLoaderStrategy {

    @Override
    public List<MascotaCargaDTO> loadData(MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<MascotaCargaDTO>>() {}
        );
    }
}

