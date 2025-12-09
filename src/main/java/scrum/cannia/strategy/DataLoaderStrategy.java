package scrum.cannia.strategy;

import org.springframework.web.multipart.MultipartFile;
import scrum.cannia.dto.MascotaCargaDTO;
import java.util.List;

public interface DataLoaderStrategy {
    List<MascotaCargaDTO> loadData(MultipartFile file) throws Exception;
}
