package com.yakush.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import java.time.Duration;

public class LoginPage extends BasePage {
    private static final By EMAIL_INPUT = By.cssSelector("input#login-email");
    private static final By PASSWORD_INPUT = By.cssSelector("input#login-password");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[data-testid='loginSubmit']");
    private static final By ERROR_MESSAGE = By.cssSelector(".ErrorMessageLink_container__7D0yM");

    public LoginPage(WebDriver driver) {
        super(driver);
        waitForPageToLoad();
    }

    private void waitForPageToLoad() {
        new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .until(d -> {
                    try {
                        return d.findElement(EMAIL_INPUT).isDisplayed() &&
                                d.findElement(PASSWORD_INPUT).isDisplayed();
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public void enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(EMAIL_INPUT));
        field.clear();
        slowType(field, email);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(PASSWORD_INPUT));
        field.clear();
        slowType(field, password);
    }

    public void submitForm() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
        scrollAndClick(button);
    }

    private WebElement customWaitForElement(By locator) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .until(driver -> {
                    WebElement element = driver.findElement(locator);
                    if (element.isDisplayed() && element.isEnabled()) {
                        scrollIntoView(element);
                        return element;
                    }
                    throw new NoSuchElementException("Element not interactable");
                });
    }
    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});",
                element
        );
    }

    public boolean isErrorDisplayed() {
        try {
            return customWaitForElement(ERROR_MESSAGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    private void slowType(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        }
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
        try {
            element.click();
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
        }
    }
}