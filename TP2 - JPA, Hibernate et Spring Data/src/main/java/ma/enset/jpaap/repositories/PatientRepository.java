package ma.enset.jpaap.repositories;

import ma.enset.jpaap.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
