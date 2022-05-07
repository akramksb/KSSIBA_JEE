package ma.enset.jpaap.repositories;

import ma.enset.jpaap.entities.Consultation;
import ma.enset.jpaap.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
