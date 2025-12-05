package scrum.cannia.strategy;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.MascotaModel;

import java.util.List;

public class DataLoaderContext {

    private DataLoaderStrategy strategy;

    public void setStrategy(DataLoaderStrategy strategy) {
        this.strategy = strategy;
    }

    public List<MascotaModel> executeStrategy(MultipartFile file) throws Exception {
        return strategy.loadData(file);
    }
}

