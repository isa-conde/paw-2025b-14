package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.EmailAlreadyUsedException;
import ar.edu.itba.paw.interfaces.exception.UsernameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.TokenDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.MailService;
import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest{
    private static final String USERNAME="johndoe";
    private static final String EMAIL="some@mail.com";
    private static final String PASSWORD="1234567890";
    private static final String SOME_URL = "url.com";
    private final static int DAYS_DURATION = 1;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenDao mockTokenDao;

    @Mock
    private MailService mockMailService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testCreate(){
        Mockito.when(passwordEncoder.encode(PASSWORD))
                .thenReturn(PASSWORD);
        Mockito.when(mockUserDao.checkUsernameExists(USERNAME))
                .thenReturn(false);
        Mockito.when(mockUserDao.checkEmailExists(EMAIL))
                .thenReturn(false);
        Mockito.when(mockUserDao.create(Mockito.eq(USERNAME),Mockito.eq(EMAIL),Mockito.eq(PASSWORD)))
                .thenReturn(new User(1,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L));

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);

        Assert.assertNotNull(maybeUser);
        Assert.assertEquals(USERNAME, maybeUser.getUsername());
        Assert.assertEquals(EMAIL,maybeUser.getEmail());
        Assert.assertEquals(PASSWORD,maybeUser.getPassword());
        Assert.assertEquals(1L,maybeUser.getId());
    }

    @Test(expected = UsernameAlreadyUsedException.class)
    public void testUsernameExists(){
        Mockito.when(mockUserDao.checkUsernameExists(USERNAME))
                .thenReturn(true);

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void testEmailExists(){
        Mockito.when(mockUserDao.checkUsernameExists(USERNAME))
                .thenReturn(false);
        Mockito.when(mockUserDao.checkEmailExists(EMAIL))
                .thenReturn(true);

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);
    }

    @Test
    public void testSameAsOldPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L)));
        Mockito.when(passwordEncoder.matches(PASSWORD,PASSWORD)).thenReturn(true);

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertTrue(ans);
    }

    @Test
    public void testSameAsNoPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,null, false, null, 1L, 1L)));

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertFalse(ans);
    }

    @Test
    public void testNotTheOldPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L)));
        Mockito.when(passwordEncoder.matches(PASSWORD,PASSWORD)).thenReturn(false);

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertFalse(ans);
    }

    @Test
    public void testCheckTokenValidity(){
        Mockito.when(mockTokenDao.findByToken(1L))
                .thenReturn(Optional.of(new Token(1L,1L,1L,LocalDate.now().plusDays(DAYS_DURATION))));

        Optional<Token> ans = userService.checkTokenValidity(1L,1L);

        Mockito.verify(mockTokenDao).markAsUsed(1L);
        Assert.assertTrue(ans.isPresent());
        Assert.assertEquals(Long.valueOf(1L), ans.get().getUser_id());
        // con esto, se analiza que user dao recibe los userId correctos en resetPassword y verifyEmail
    }

    @Test
    public void testNoTokenValidity(){
        Mockito.when(mockTokenDao.findByToken(1L))
                .thenReturn(Optional.empty());

        Optional<Token> ans = userService.checkTokenValidity(1L,1L);

        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testWrongTokenValidity(){
        Mockito.when(mockTokenDao.findByToken(1L))
                .thenReturn(Optional.of(new Token(1L,2L,1L,LocalDate.now().plusDays(DAYS_DURATION))));

        Optional<Token> ans = userService.checkTokenValidity(1L,1L);

        Assert.assertTrue(ans.isEmpty());
    }

    @Test
    public void testLateTokenValidity(){
        Mockito.when(mockTokenDao.findByToken(1L))
                .thenReturn(Optional.of(new Token(1L,1L,1L,LocalDate.now())));

        Optional<Token> ans = userService.checkTokenValidity(1L,1L);

        Assert.assertTrue(ans.isEmpty());
    }

    /// Redundante
//    @Test
//    public void testFindById(){
//        Mockito.when(mockUserDao.findById(1)).thenReturn(Optional.of(new User(1, USERNAME, EMAIL,PASSWORD, false)));
//
//        Optional<User> maybeUser = userService.findById(1);
//
//        Assert.assertNotNull(maybeUser);
//        Assert.assertTrue(maybeUser.isPresent());
//        Assert.assertEquals(USERNAME, maybeUser.get().getUsername());
//        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
//        Assert.assertEquals(PASSWORD,maybeUser.get().getPassword());
//        Assert.assertEquals(1L, maybeUser.get().getId());
//    }

    /// Redundante
//    @Test
//    public void testFindNonExistent(){
//        Mockito.when(mockUserDao.findById(1)).thenReturn(Optional.empty());
//
//        Optional<User> maybeUser = userService.findById(1);
//
//        Assert.assertNotNull(maybeUser);
//        Assert.assertTrue(maybeUser.isEmpty());
//    }
}