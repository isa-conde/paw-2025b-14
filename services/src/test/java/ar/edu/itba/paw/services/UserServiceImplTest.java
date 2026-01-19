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
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest{
    private static final String USERNAME="johndoe";
    private static final String EMAIL="some@mail.com";
    private static final String PASSWORD="1234567890";
    private static final Locale LOCALE = Locale.of("es");
    private final static int RESET_PASSWORD_DAYS_DURATION = 1;
    private final static int VERIFICATION_DAYS_DURATION = 2;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenDao mockTokenDao;

    @Mock
    private MailService ms;

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
        Mockito.when(mockUserDao.create(Mockito.eq(USERNAME),Mockito.eq(EMAIL),Mockito.eq(PASSWORD),Mockito.eq(LOCALE.getLanguage())))
                .thenReturn(new User(1L,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L,"es"));
        Mockito.when(mockTokenDao.findByToken(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(mockTokenDao.create(ArgumentMatchers.eq(1L),ArgumentMatchers.anyLong(),ArgumentMatchers.eq(LocalDate.now().plusDays(VERIFICATION_DAYS_DURATION))))
                .thenReturn(new Token(1L,1L,1L,LocalDate.now().plusDays(VERIFICATION_DAYS_DURATION)));

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);

        Assert.assertNotNull(maybeUser);
        Assert.assertEquals(USERNAME, maybeUser.getUsername());
        Assert.assertEquals(EMAIL,maybeUser.getEmail());
        Assert.assertEquals(PASSWORD,maybeUser.getPassword());
        Assert.assertEquals(1L,maybeUser.getId().longValue());
        Assert.assertEquals("es",maybeUser.getLocale());
    }

    @Test(expected = UsernameAlreadyUsedException.class)
    public void testUsernameExists(){
        Mockito.when(mockUserDao.checkUsernameExists(USERNAME))
                .thenReturn(true);

         userService.create(USERNAME,EMAIL,PASSWORD,LOCALE);
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void testEmailExists(){
        Mockito.when(mockUserDao.checkUsernameExists(USERNAME))
                .thenReturn(false);
        Mockito.when(mockUserDao.checkEmailExists(EMAIL))
                .thenReturn(true);

        userService.create(USERNAME,EMAIL,PASSWORD,LOCALE);
    }

    @Test
    public void testSameAsOldPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L,"")));
        Mockito.when(passwordEncoder.matches(PASSWORD,PASSWORD)).thenReturn(true);

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertTrue(ans);
    }

    @Test
    public void testSameAsNoPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,null, false, null, 1L, 1L,"")));

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertFalse(ans);
    }

    @Test
    public void testNotTheOldPassword(){
        Mockito.when(mockUserDao.findById(1L))
                .thenReturn(Optional.of(new User(1L,USERNAME,EMAIL,PASSWORD, false, null, 1L, 1L,"")));
        Mockito.when(passwordEncoder.matches(PASSWORD,PASSWORD)).thenReturn(false);

        boolean ans = userService.sameAsOldPassword(PASSWORD,1L);

        Assert.assertFalse(ans);
    }

    @Test
    public void testCheckTokenValidity(){
        Token fakeToken = new Token(1L,1L,1L,LocalDate.now().plusDays(VERIFICATION_DAYS_DURATION));
        User fakeUser = new User(1L,USERNAME,EMAIL,PASSWORD,false,null,1L,1L,"");
        fakeToken.setUser(fakeUser);
        Mockito.when(mockTokenDao.findByToken(1L))
                .thenReturn(Optional.of(fakeToken));

        Token ans = userService.checkTokenValidity(1L);

        Assert.assertEquals(Long.valueOf(1L), ans.getUserId());
    }

}