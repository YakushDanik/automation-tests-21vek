package com.yakush.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class HomePage extends BasePage {

    private static final By ACCOUNT_TOGGLER = By.cssSelector("button.styles_userToolsToggler__c2aHe");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-testid='loginButton']");
    private static final By LOGOUT_BUTTON = By.cssSelector("a.ProfileItem_itemLogout__RFHqc");
    private static final By COOKIE_BANNER = By.cssSelector(".AgreementCookie_modal__x3nra");
    private static final By ACCEPT_COOKIES_BTN = By.cssSelector("button.Button-module__blue-primary");
    private static final By ACCOUNT_MENU = By.cssSelector("div[data-testid='userToolsDropDown']");

    private static final By UP_BUTTON_VISIBLE = By.cssSelector("button[class*='upButton_'][class*='show_']");
    private static final By UP_BUTTON_LABEL = By.cssSelector("span[class*='upButtonLabel_']");

    public HomePage(WebDriver driver) {
        super(driver);
        handleCookieBanner();
    }

    private void handleCookieBanner() {
        try {
            WebElement banner = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(COOKIE_BANNER));
            WebElement acceptBtn = banner.findElement(ACCEPT_COOKIES_BTN);
            acceptBtn.click();
            wait.until(ExpectedConditions.invisibilityOf(banner));
        } catch (Exception e) {
            System.out.println("Cookie banner not found or already accepted");
        }
    }

    public LoginPage navigateToLoginPage() {
        try {
            WebElement accountToggler = wait.until(ExpectedConditions.elementToBeClickable(ACCOUNT_TOGGLER));
            clickWithJS(accountToggler);

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
            clickWithJS(loginBtn);

            return new LoginPage(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to login page", e);
        }
    }

    public boolean isUserLoggedIn() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_TOGGLER))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    public void logout() {
        if (isUserLoggedIn()) {
            try {
                openAccountMenuWithRetry(3);

                clickLogoutButton();

                verifySuccessfulLogout();
            } catch (Exception e) {
                throw new RuntimeException("Logout failed after multiple attempts", e);
            }
        }
    }

    private void openAccountMenuWithRetry(int attempts) {
        int currentAttempt = 0;
        while (currentAttempt < attempts) {
            try {
                WebElement accountToggler = wait.until(ExpectedConditions.elementToBeClickable(ACCOUNT_TOGGLER));
                highlightElement(accountToggler);
                clickWithJS(accountToggler);

                wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_MENU));
                return;
            } catch (Exception e) {
                currentAttempt++;
                if (currentAttempt == attempts) {
                    throw e;
                }
                try { Thread.sleep(1000); } catch (InterruptedException ie) {}
            }
        }
    }

    private void clickLogoutButton() {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementNotInteractableException.class);

        WebElement logoutButton = fluentWait.until(driver -> {
            WebElement button = driver.findElement(LOGOUT_BUTTON);
            if (button.isDisplayed() && button.isEnabled()) {
                highlightElement(button);
                return button;
            }
            return null;
        });

        clickWithJS(logoutButton);
    }

    private void verifySuccessfulLogout() {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .until(driver -> {
                    try {
                        WebElement accountToggler = wait.until(ExpectedConditions.elementToBeClickable(ACCOUNT_TOGGLER));
                        clickWithJS(accountToggler);
                        return driver.findElement(ACCOUNT_TOGGLER).isDisplayed();
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    private void highlightElement(WebElement element) {
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].style.border='3px solid red';", element);
    }


    private void clickWithJS(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
    }

    public CatalogPage navigateToCatalog() {
        try {
            WebElement catalogLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("catalogSearch")));
            catalogLink.click();
            return new CatalogPage(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to catalog", e);
        }
    }

    public void testScrollUpButton() {
        try {
            scrollDown(5000);

            WebElement upButton = wait.until(ExpectedConditions.visibilityOfElementLocated(UP_BUTTON_VISIBLE));

            String buttonText = upButton.findElement(UP_BUTTON_LABEL).getText();
            if (!"Наверх".equals(buttonText)) {
                throw new AssertionError("Неверный текст кнопки: " + buttonText);
            }

            clickWithJS(upButton);

            wait.until(d -> (Long)((JavascriptExecutor)d).executeScript("return window.pageYOffset;") < 100);

            wait.until(ExpectedConditions.invisibilityOfElementLocated(UP_BUTTON_VISIBLE));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке кнопки 'Наверх'", e);
        }
    }

    private void scrollDown(int pixels) {
        ((JavascriptExecutor)driver).executeScript("window.scrollBy(0, arguments[0])", pixels);
    }
}