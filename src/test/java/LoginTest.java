import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LoginTest {

    @Test
    public void testCheckUserNameCorrect() {

        Login user = new Login();

        user.setUsername("mdl_2");

        assertTrue(user.checkUserName());
    }

    @Test
    public void testCheckUserNameIncorrect() {

        Login user = new Login();

        user.setUsername("kyl!!!!!!!");

        assertFalse(user.checkUserName());
    }


    @Test
    public void testCheckPasswordComplexityCorrect() {

        Login user = new Login();

        user.setPassword("Ch&&sec@ke99!");

        assertTrue(user.checkPasswordComplexity());
    }

    @Test
    public void testCheckPasswordComplexityIncorrect() {

        Login user = new Login();

        user.setPassword("password");

        assertFalse(user.checkPasswordComplexity());
    }


    @Test
    public void testCheckCellPhoneNumberCorrect() {

        Login user = new Login();

        user.setCellPhoneNumber("+27796790776");

        assertTrue(user.checkCellPhoneNumber());
    }

    @Test
    public void testCheckCellPhoneNumberIncorrect() {

        Login user = new Login();

        user.setCellPhoneNumber("08966553");

        assertFalse(user.checkCellPhoneNumber());
    }


    @Test
    public void testLoginSuccessful() {

        Login user = new Login(
                "Sinenhlanhla",
                "Mabuza",
                "mdl_2",
                "Ch&&sec@ke99!",
                "+27796790776"
        );

        assertTrue(
                user.loginUser("mdl_2", "Ch&&sec@ke99!")
        );
    }

    @Test
    public void testLoginFailed() {

        Login user = new Login(
                "Sinenhlanhla",
                "Mabuza",
                "mdl_2",
                "Ch&&sec@ke99!",
                "+27796790776"
        );

        assertFalse(
                user.loginUser("wrong_username", "wrong_password")
        );
    }


    @Test
    public void testRegisterUserSuccessful() {

        Login user = new Login(
                "Sinenhlanhla",
                "Mabuza",
                "mdl_2",
                "Ch&&sec@ke99!",
                "+27796790776"
        );

        assertEquals(
                "User registered successfully.",
                user.registerUser()
        );
    }



    @Test
    public void testReturnLoginStatusSuccessful() {

        Login user = new Login(
                "Sinenhlanhla",
                "Mabuza",
                "mdl_2",
                "Ch&&sec@ke99!",
                "+27796790776"
        );

        assertEquals(
                "Welcome Sinenhlanhla Mabuza, it is good to see you again",
                user.returnLoginStatus(true)
        );
    }

    @Test
    public void testReturnLoginStatusFailed() {

        Login user = new Login(
                "Sinenhlanhla",
                "Mabuza",
                "mdl_2",
                "Ch&&sec@ke99!",
                "+27796790776"
        );

        assertEquals(
                "Username or password incorrect please try again",
                user.returnLoginStatus(false)
        );
    }
}
