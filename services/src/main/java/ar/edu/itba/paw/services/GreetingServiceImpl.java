package ar.edu.itba.paw.services;


import ar.edu.itba.paw.interfaces.GreetingService;
import ar.edu.itba.paw.interfaces.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {

    private final UserRepository userRepository;

    public GreetingServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String greet(String username) {
        String display = userRepository.findDisplayNameByUsername(username);
        return "Hello " + display + "!";
    }

}
