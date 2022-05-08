package ma.enset.patientsmvc.sec.service;

import ma.enset.patientsmvc.sec.entities.AppRole;
import ma.enset.patientsmvc.sec.entities.AppUser;

public interface SecurityService {
    AppUser saveNewUser( String username, String password, String rePassword );
    AppRole saveNewRole( String roleName, String description );
    void addRoleToUser( String username, String roleName );
    void removeRoleFromUser( String username, String roleName );
    AppUser loadUserByUserName(String username);
}
