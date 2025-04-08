package com.yakush.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

public class ProductCard extends BasePage {
    private final String expectedTitle;
    private final String expectedPrice;
    private final String expectedArticle;

    private static final By PRODUCT_TITLE = By.cssSelector("p[data-testid='card-info'] a");
    private static final By PRODUCT_PRICE = By.cssSelector("p[class='CardPrice_currentPrice__EU_7r']");
    private static final By PRODUCT_ARTICLE = By.xpath("//p[contains(text(), 'Артикул')]/following-sibling::p");
    private static final By PAGE_CONTAINER = By.cssSelector(".Search_searchInputContainer__rDgxi");

    public ProductCard(WebDriver driver, String expectedTitle, String expectedPrice, String expectedArticle) {
        super(driver);
        this.expectedTitle = expectedTitle;
        this.expectedPrice = expectedPrice;
        this.expectedArticle = expectedArticle;
        waitForPageToLoad();
    }


    public void openProductPage() {
        try {
            WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_TITLE));
            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    productLink
            );
            productLink.click();
            waitForPageToLoad();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open product page", e);
        }
    }

    public void waitForPageToLoad() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .until(d -> {
                        try {
                            return d.findElement(PAGE_CONTAINER).isDisplayed() &&
                                    !d.findElement(PRODUCT_TITLE).getText().isEmpty() &&
                                    d.findElement(PRODUCT_ARTICLE).isDisplayed();
                        } catch (Exception e) {
                            return false;
                        }
                    });
        } catch (TimeoutException e) {
            throw new RuntimeException("Страница товара не загрузилась", e);
        }
    }

    public String getProductArticle() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_ARTICLE)).getText();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось найти артикул товара", e);
        }
    }

    public boolean verifyProductDetails() {
        String actualTitle = driver.findElement(PRODUCT_TITLE).getText();
        String actualPrice = driver.findElement(PRODUCT_PRICE).getText().replaceAll("\\s", "");
        String actualArticle = driver.findElement(PRODUCT_ARTICLE).getText();

        boolean titleMatches = actualTitle.contains("iPhone") && expectedTitle.contains("iPhone");
        boolean priceMatches = actualPrice.matches("\\d+,\\d+р.");
        boolean articleMatches = actualArticle.equals(expectedArticle);

        if (!titleMatches || !priceMatches || !articleMatches) {
            System.out.println("Ошибка в данных:");
            System.out.println("Название: " + actualTitle + " vs " + expectedTitle);
            System.out.println("Цена: " + actualPrice + " vs " + expectedPrice);
            System.out.println("Артикул: " + actualArticle + " vs " + expectedArticle);
        }

        return titleMatches && priceMatches && articleMatches;
    }
}