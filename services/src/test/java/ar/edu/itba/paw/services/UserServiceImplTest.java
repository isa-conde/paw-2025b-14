package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exception.EmailAlreadyUsedException;
import ar.edu.itba.paw.interfaces.exception.UsernameAlreadyUsedException;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest{
    private static final String USERNAME="johndoe";
    private static final String EMAIL="some@mail.com";
    private static final String PASSWORD="1234567890";

    @Mock
    private UserDao mockDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testCreate(){
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        Mockito.when(mockDao.checkUsernameExists(USERNAME)).thenReturn(false);
        Mockito.when(mockDao.checkEmailExists(EMAIL)).thenReturn(false);
        Mockito.when(mockDao.create(Mockito.eq(USERNAME),Mockito.eq(EMAIL),Mockito.eq(PASSWORD))).thenReturn(new User(1,USERNAME,EMAIL,PASSWORD));

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);

        Assert.assertNotNull(maybeUser);
        Assert.assertEquals(USERNAME, maybeUser.getUsername());
        Assert.assertEquals(EMAIL,maybeUser.getEmail());
        Assert.assertEquals(PASSWORD,maybeUser.getPassword());
        Assert.assertEquals(1L,maybeUser.getId());
        Mockito.verify(mockDao).checkUsernameExists(USERNAME);
        Mockito.verify(mockDao).checkEmailExists(EMAIL);
        Mockito.verify(mockDao).create(USERNAME, EMAIL,PASSWORD);
        Mockito.verifyNoMoreInteractions(mockDao);
    }

    @Test(expected = UsernameAlreadyUsedException.class)
    public void testUsernameExists(){
        Mockito.when(mockDao.checkUsernameExists(USERNAME)).thenReturn(true);
        //Mockito.when(mockDao.checkEmailExists(EMAIL)).thenReturn(false);
        //Mockito.when(mockDao.create(Mockito.eq(USERNAME),Mockito.eq(EMAIL),Mockito.eq(PASSWORD))).thenReturn(new User(1,USERNAME,EMAIL,PASSWORD));

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void testEmailExists(){
        Mockito.when(mockDao.checkUsernameExists(USERNAME)).thenReturn(false);
        Mockito.when(mockDao.checkEmailExists(EMAIL)).thenReturn(true);
        //Mockito.when(mockDao.create(Mockito.eq(USERNAME),Mockito.eq(EMAIL),Mockito.eq(PASSWORD))).thenReturn(new User(1,USERNAME,EMAIL,PASSWORD));

        User maybeUser = userService.create(USERNAME,EMAIL,PASSWORD);
    }

    @Test
    public void testFindById(){
        Mockito.when(mockDao.findById(1)).thenReturn(Optional.of(new User(1, USERNAME, EMAIL,PASSWORD)));

        Optional<User> maybeUser = userService.findById(1);

        Assert.assertNotNull(maybeUser);
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(USERNAME, maybeUser.get().getUsername());
        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
        Assert.assertEquals(PASSWORD,maybeUser.get().getPassword());
        Assert.assertEquals(1L, maybeUser.get().getId());
        Mockito.verify(mockDao).findById(1);
        Mockito.verifyNoMoreInteractions(mockDao);
    }

    @Test
    public void testFindNonExistent(){
        Mockito.when(mockDao.findById(1)).thenReturn(Optional.empty());

        Optional<User> maybeUser = userService.findById(1);

        Assert.assertNotNull(maybeUser);
        Assert.assertTrue(maybeUser.isEmpty());
        Mockito.verify(mockDao).findById(1);
        Mockito.verifyNoMoreInteractions(mockDao);
    }

//    @Test
//    public void testAuthenticate(){
//        Mockito.when(mockDao.authenticate(USERNAME,EMAIL)).thenReturn(Optional.of(new User(1, USERNAME, EMAIL)));
//
//        Optional<User> maybeUser = userService.authenticate(USERNAME,EMAIL);
//
//        Assert.assertNotNull(maybeUser);
//        Assert.assertTrue(maybeUser.isPresent());
//        Assert.assertEquals(USERNAME, maybeUser.get().getUsername());
//        Assert.assertEquals(EMAIL, maybeUser.get().getEmail());
//        Assert.assertEquals(1L, maybeUser.get().getId());
//        Mockito.verify(mockDao).authenticate(USERNAME,EMAIL);
//        Mockito.verifyNoMoreInteractions(mockDao);
//    }
//
//    @Test
//    public void testAuthenticateNonExistent(){
//        Mockito.when(mockDao.authenticate(USERNAME,EMAIL)).thenReturn(Optional.empty());
//
//        Optional<User> maybeUser = userService.authenticate(USERNAME,EMAIL);
//
//        Assert.assertNotNull(maybeUser);
//        Assert.assertFalse(maybeUser.isPresent());
//        Mockito.verify(mockDao).authenticate(USERNAME,EMAIL);
//        Mockito.verifyNoMoreInteractions(mockDao);
//    }
}