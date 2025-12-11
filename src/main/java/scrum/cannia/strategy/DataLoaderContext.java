package scrum.cannia.strategy;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.Dto.MascotaCargaDTO;

import java.util.List;

public class DataLoaderContext {

    private DataLoaderStrategy strategy;

    public void setStrategy(DataLoaderStrategy strategy) {
        this.strategy = strategy;
    }

    public List<MascotaCargaDTO> executeStrategy(MultipartFile file) throws Exception {
        return strategy.loadData(file);
    }
}

