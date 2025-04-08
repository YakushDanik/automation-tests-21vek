package tests;

import com.yakush.pages.HomePage;
import com.yakush.pages.LoginPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTests extends BaseTest {
    private static final String VALID_EMAIL = "yakushdanik9@gmail.com";
    private static final String VALID_PASSWORD = "582591d9";
    private static final String INVALID_EMAIL = "wrong@example.com";
    private static final String INVALID_PASSWORD = "wrongPass";

    @Test
    public void testSuccessfulLogin() {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.navigateToLoginPage();

        loginPage.enterEmail(VALID_EMAIL);
        loginPage.enterPassword(VALID_PASSWORD);
        loginPage.submitForm();

        assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    }

    @Test
    public void testInvalidCredentials() {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.navigateToLoginPage();

        loginPage.enterEmail(INVALID_EMAIL);
        loginPage.enterPassword(INVALID_PASSWORD);
        loginPage.submitForm();

        assertTrue(loginPage.isErrorDisplayed());
    }

    @Test
    public void testLogout() {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.navigateToLoginPage();
        loginPage.enterEmail(VALID_EMAIL);
        loginPage.enterPassword(VALID_PASSWORD);
        loginPage.submitForm();

        homePage.logout();

        assertTrue(homePage.isUserLoggedIn(), "User should be logged out");
    }
}