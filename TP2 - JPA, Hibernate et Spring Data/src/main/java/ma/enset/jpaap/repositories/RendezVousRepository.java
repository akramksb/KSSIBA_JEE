package ma.enset.jpaap.repositories;

import ma.enset.jpaap.entities.Patient;
import ma.enset.jpaap.entities.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
}
