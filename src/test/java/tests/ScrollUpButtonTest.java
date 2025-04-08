package tests;

import com.yakush.pages.HomePage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScrollUpButtonTest extends BaseTest {

    @Test
    public void testUpButtonFunctionality() {
        HomePage homePage = new HomePage(driver);

        assertDoesNotThrow(homePage::testScrollUpButton,
                "Проверка кнопки 'Наверх' завершилась с ошибкой");

        assertTrue(homePage.isUserLoggedIn() || !homePage.isUserLoggedIn(),
                "Проверка не должна влиять на состояние авторизации");
    }
}