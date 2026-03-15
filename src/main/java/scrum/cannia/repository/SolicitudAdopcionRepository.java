package scrum.cannia.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import scrum.cannia.model.MascotaModel;
import scrum.cannia.model.SolicitudAdopcionModel;

import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcionModel, Long> {
    List<SolicitudAdopcionModel> findByMascota(MascotaModel mascota);
    }

