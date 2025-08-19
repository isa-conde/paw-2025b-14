package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, String> data = Map.of(
            "bruno", "Bruno Taccone",
            "ana", "Ana Perez"
    );

    @Override
    public String findDisplayNameByUsername(String username) {
        return data.getOrDefault(username, username);
    }

}
