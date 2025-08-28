package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long VALID_ID = 1;

    @InjectMocks
    private UserServiceImpl us;

    @Mock
    private UserDao userDaoMock;

    @Test
    public void testFindByIdSuccess() {
        Optional<User> maybeUser = us.findById(VALID_ID);

        assertNotNull(maybeUser);
        assertTrue(maybeUser.isPresent());
        assertEquals(VALID_ID, maybeUser.get().getId());

    }

}
