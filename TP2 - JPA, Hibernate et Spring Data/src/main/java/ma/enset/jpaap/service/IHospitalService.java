package ma.enset.jpaap.service;

import ma.enset.jpaap.entities.Consultation;
import ma.enset.jpaap.entities.Medecin;
import ma.enset.jpaap.entities.Patient;
import ma.enset.jpaap.entities.RendezVous;

public interface IHospitalService {
    Patient savePatient(Patient patient);
    Medecin saveMedecin(Medecin medecin);
    RendezVous saveRDV(RendezVous rendezVous);
    Consultation saveConsultation(Consultation consultation);
}
