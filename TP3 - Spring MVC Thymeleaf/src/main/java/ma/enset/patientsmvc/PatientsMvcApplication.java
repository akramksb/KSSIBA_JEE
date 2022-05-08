package ma.enset.patientsmvc;

import ma.enset.patientsmvc.entities.Patient;
import ma.enset.patientsmvc.repositories.PatientRepository;
import ma.enset.patientsmvc.sec.service.SecurityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication
public class PatientsMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientsMvcApplication.class, args);
    }

//    @Bean
    CommandLineRunner saveUsers(SecurityService securityService){
        return args -> {
            securityService.saveNewUser( "akram", "1234", "1234" );
            securityService.saveNewUser( "tarik", "1234", "1234" );
            securityService.saveNewUser( "zakaria", "1234", "1234" );

            securityService.saveNewRole("USER", "user");
            securityService.saveNewRole("ADMIN", "admin");

            securityService.addRoleToUser("akram", "ADMIN");
            securityService.addRoleToUser("akram", "USER");
            securityService.addRoleToUser("tarik", "USER");
            securityService.addRoleToUser("zakaria", "USER");
        };
    }

    @Bean
    CommandLineRunner commandLineRunner(PatientRepository etudiantRepository){
        return args -> {
            etudiantRepository.save(
                    new Patient( null, "Akram", new Date(), true));
            etudiantRepository.save(
                    new Patient( null, "Tarik", new Date(), true));
            etudiantRepository.save(
                    new Patient( null, "Zakaria", new Date(), true));
        };
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
