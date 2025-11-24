package scrum.cannia.repository;
import scrum.cannia.model.FundacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundacionRepository extends JpaRepository<FundacionModel, Long> {
}

