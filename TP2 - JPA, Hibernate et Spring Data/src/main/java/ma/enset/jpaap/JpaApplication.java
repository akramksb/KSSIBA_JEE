package ma.enset.jpaap;

import ma.enset.jpaap.entities.*;
import ma.enset.jpaap.repositories.ConsultationRepository;
import ma.enset.jpaap.repositories.MedecinRepository;
import ma.enset.jpaap.repositories.PatientRepository;
import ma.enset.jpaap.repositories.RendezVousRepository;
import ma.enset.jpaap.service.IHospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class JpaApplication{

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }

    @Bean
    CommandLineRunner start(IHospitalService hospitalService,
                            PatientRepository patientRepository,
                            MedecinRepository medecinRepository,
                            RendezVousRepository rendezVousRepository){
        return arg->{
            Stream.of("Akram", "Tarik", "Zakaria")
                    .forEach( name->{
                        Patient patient = new Patient();
                        patient.setNom(name);
                        patient.setDateNaissance(new Date());
                        patient.setMalade( (Math.random()<0.5)? false: true);
                        hospitalService.savePatient( patient );
                    });
            Stream.of("Kssiba", "Ofkir", "Hadoumi")
                    .forEach( name->{
                        Medecin medecin = new Medecin();
                        medecin.setNom( name );
                        medecin.setEmail( name+"@gmail.com" );
                        medecin.setSpecialite( Math.random()<0.5? "Cardio":"Dentist" );
                        hospitalService.saveMedecin( medecin );
                    });

            Patient patient = patientRepository.findByNom("Akram");
            Medecin medecin = medecinRepository.findByNom("Hadoumi");

            RendezVous rendezVous = new RendezVous();
            rendezVous.setDate( new Date() );
            rendezVous.setStatus( StatusRDV.PENDING );
            rendezVous.setMedecin(medecin);
            rendezVous.setPatient(patient);

            hospitalService.saveRDV( rendezVous );

            RendezVous rendezVous1 = rendezVousRepository.findById(1L).orElse(null);
            Consultation consultation = new Consultation();
            consultation.setDateConsultation( new Date() );
            consultation.setRendezVous(rendezVous1);
            consultation.setRapport("Rapport ...");

            hospitalService.saveConsultation(consultation);

        };
    }

}
