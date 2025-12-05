package scrum.cannia.strategy;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.model.PetModel;
import java.util.List;

public interface DataLoaderStrategy {
    List<PetModel> loadData(MultipartFile file) throws Exception;
}
