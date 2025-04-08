package com.yakush.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class PageWithUpButton {
    private final WebDriver driver;
    private static final By UP_BUTTON = By.cssSelector("button.style_upButton__MUSZA");
    private static final By UP_BUTTON_VISIBLE = By.cssSelector("button.style_upButton__MUSZA.style_show__BRLkA");

    public PageWithUpButton(WebDriver driver) {
        this.driver = driver;
    }

    public void scrollDownAndCheckUpButton() {
        ((JavascriptExecutor)driver).executeScript("window.scrollBy(0, 1000)");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement upButton = wait.until(ExpectedConditions.visibilityOfElementLocated(UP_BUTTON_VISIBLE));

        upButton.click();

        wait.until(d -> {
            Long scrollY = (Long)((JavascriptExecutor)driver).executeScript("return window.pageYOffset;");
            return scrollY == 0;
        });

        wait.until(ExpectedConditions.invisibilityOfElementLocated(UP_BUTTON_VISIBLE));
    }

    public void testUpButtonFunctionality() {
        long initialPosition = (Long)((JavascriptExecutor)driver).executeScript("return window.pageYOffset;");

        ((JavascriptExecutor)driver).executeScript("window.scrollBy(0, 1000)");

        WebElement upButton = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(UP_BUTTON_VISIBLE));

        String buttonText = upButton.findElement(By.cssSelector(".style_upButtonLabel__LPAA4")).getText();
        if (!"Наверх".equals(buttonText)) {
            throw new AssertionError("Текст кнопки не соответствует ожидаемому");
        }

        upButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> {
                    long currentPosition = (Long)((JavascriptExecutor)d).executeScript("return window.pageYOffset;");
                    return currentPosition == initialPosition;
                });

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(UP_BUTTON_VISIBLE));
    }
}
