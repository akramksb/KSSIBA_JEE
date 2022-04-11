package ma.enset.jpaap;

import ma.enset.jpaap.entities.Patient;
import ma.enset.jpaap.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class JpaApplication implements CommandLineRunner {
    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i<30; i++)
        {
            patientRepository.save(
                    new Patient(null, "akram", new Date(), false, (int) (Math.random()*120)));
            patientRepository.save(
                    new Patient(null, "zakaria", new Date(), true, (int) (Math.random()*120)));
            patientRepository.save(
                    new Patient(null, "tarik", new Date(), Math.random()>0.5?true:false, (int) (Math.random()*120)));
        }

//        List<Patient> patients = patientRepository.findAll();
        Page<Patient> patients = patientRepository.findAll(PageRequest.of(0,5));

        System.out.println("Total pages : "+patients.getTotalPages());
        System.out.println("Total elements : "+patients.getTotalElements());
        System.out.println("Num page : "+patients.getNumber());

//        List<Patient> content = patients.getContent();
        Page<Patient> malades = patientRepository.findByMalade(true, PageRequest.of(1,5));

        malades.forEach(patient -> {
            System.out.println("===============");
            System.out.println(patient.getId());
            System.out.println(patient.getNom());
            System.out.println(patient.getDateNaissance());
            System.out.println(patient.isMalade());
            System.out.println(patient.getScore());
                });

        System.out.println("-----------");
        Patient patient = patientRepository.findById(1L).orElse(null);
        if (patient != null)
        {
            System.out.println(patient.getNom());
            System.out.println(patient.isMalade());
        }
        patient.setScore(100);
        patientRepository.save(patient);
        patientRepository.deleteById(1L);

    }
}
