package api.ytter.backend.service;

import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    public void validateAndRegisterUser(RegistrationData registrationData){

    }

    public String checkLoginAndGenerateToken(LoginData loginData){
        return "";
    }
}
