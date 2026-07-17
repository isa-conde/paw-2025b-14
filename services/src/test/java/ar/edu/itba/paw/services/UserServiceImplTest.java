package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.UserNotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAccount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String PASSWORD = "1234567890";

    @Mock
    private UserDao userDao;
    @Mock
    private TokenDao tokenDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private ImageDao imageDao;
    @Mock
    private CommentDao commentDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testSameAsOldPassword() {
        User user = new User(USER_ID, "user", "user@mail.com", PASSWORD, true, null, null, null, "es");

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);

        Assert.assertTrue(userService.sameAsOldPassword(PASSWORD, USER_ID));
    }

    @Test
    public void testGetUserAccountsUsesDaoQuery() {
        User user = new User(USER_ID, "user", "user@mail.com", PASSWORD, true, null, null, null, "es");
        List<UserAccount> accounts = List.of();

        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.getUserAccounts(USER_ID)).thenReturn(accounts);

        Assert.assertSame(accounts, userService.getUserAccounts(USER_ID));
        Mockito.verify(userDao).getUserAccounts(USER_ID);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserAccountsForUnknownUser() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        userService.getUserAccounts(USER_ID);
    }

    @Test
    public void testFindAllByNameWithLimitDelegatesLimit() {
        List<User> users = List.of(new User(USER_ID, "user", "user@mail.com", PASSWORD, true, null, null, null, "es"));

        Mockito.when(userDao.findAllByName("us", 5)).thenReturn(users);

        Assert.assertSame(users, userService.findAllByName("us", 5));
        Mockito.verify(userDao).findAllByName("us", 5);
    }
}
