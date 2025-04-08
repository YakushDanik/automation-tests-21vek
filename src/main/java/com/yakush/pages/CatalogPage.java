package com.yakush.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import java.time.Duration;
import java.util.List;

public class CatalogPage extends BasePage {
    private static final By SEARCH_INPUT = By.cssSelector("#catalogSearch");
    private static final By SEARCH_BUTTON = By.cssSelector(".Search_searchBtn__Tk7Gw");
    private static final By SEARCH_CONTAINER = By.cssSelector(".Search_searchInputContainer__rDgxi");

    private static final By PRODUCT_CARDS = By.cssSelector("div[class='style_product__xVGB6']");
    private static final By PRODUCT_TITLE = By.cssSelector("p[data-testid='card-info'] a");
    private static final By PRODUCT_PRICE = By.cssSelector("p[class='CardPrice_currentPrice__EU_7r']");
    private static final By PRODUCT_ARTICLE = By.xpath("//p[contains(text(), 'Артикул')]/following-sibling::p");
    private static final By LOADING_INDICATOR = By.cssSelector(".loading-indicator");

    public CatalogPage(WebDriver driver) {
        super(driver);
    }

    public void searchForProduct(String query) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_CONTAINER));

            WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
            searchInput.clear();
            slowType(searchInput, query);

            WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BUTTON));
            clickWithJS(searchBtn);

            waitForResultsToLoad();

        } catch (Exception e) {
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    private void waitForResultsToLoad() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class)
                    .until(driver -> {
                        try {
                            List<WebElement> noResultsElements = driver.findElements(
                                    By.xpath("//*[contains(text(), 'Ничего не найдено') or contains(text(), 'Не найдено')]"));
                            if (!noResultsElements.isEmpty() && noResultsElements.get(0).isDisplayed()) {
                                return true;
                            }
                        } catch (Exception e) {
                            // Продолжаем проверку
                        }

                        List<WebElement> products = driver.findElements(PRODUCT_CARDS);
                        if (!products.isEmpty()) {
                            try {
                                return products.get(0).isDisplayed();
                            } catch (StaleElementReferenceException e) {
                                return false;
                            }
                        }
                        try {
                            return driver.findElement(By.cssSelector(".empty-results, .no-results")).isDisplayed();
                        } catch (Exception e) {
                            return false;
                        }
                    });
        } catch (TimeoutException e) {
            throw new TimeoutException("Не удалось дождаться результатов поиска", e);
        }
    }

    public boolean areAllProductsContainKeyword(String keyword) {
        List<WebElement> products = driver.findElements(PRODUCT_TITLE);
        if (products.isEmpty()) return false;

        for (WebElement product : products) {
            if (!product.getText().toLowerCase().contains(keyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public ProductCard openFirstProduct() {
        String originalWindow = driver.getWindowHandle();
        try {
            By productLinkLocator = By.cssSelector("[data-testid='card-info'] a");
            WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(productLinkLocator));
            String expectedTitleBeforeClick = productLink.getText(); // Запомнили название ДО клика
            System.out.println("Название перед кликом: " + expectedTitleBeforeClick);

            String title = productLink.getText();
            String price = driver.findElement(PRODUCT_PRICE).getText();

            System.out.println("Список - Название: " + title);
            System.out.println("Список - Цена: " + price);


            System.out.println("Текущий URL перед кликом: " + driver.getCurrentUrl());
            clickWithRetry(productLink);
            System.out.println("Текущий URL после клика: " + driver.getCurrentUrl());

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains(".html"),
                    ExpectedConditions.visibilityOfElementLocated(PRODUCT_ARTICLE)
            ));

            String article = wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_ARTICLE)).getText();
            System.out.println("Карточка - Артикул: " + article);

            return new ProductCard(driver, title, price, article);
        } catch (Exception e) {
            for (String windowHandle : driver.getWindowHandles()) {
                if (!originalWindow.equals(windowHandle)) {
                    driver.switchTo().window(windowHandle).close();
                }
            }
            driver.switchTo().window(originalWindow);

            System.err.println("Ошибка при открытии карточки: " + e.getClass().getName() + " - " + e.getMessage());
            throw new RuntimeException("Не удалось открыть карточку товара", e);
        }
    }

    public int getProductsCount() {
        return driver.findElements(PRODUCT_CARDS).size();
    }

    private void slowType(WebElement element, String text) {
        element.clear();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        }
    }

    private void clickWithRetry(WebElement element) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                scrollToElement(element);
                element.click();
                return;
            } catch (ElementClickInterceptedException e) {
                attempts++;
                if (attempts == 3) {
                    clickWithJS(element);
                }
                try { Thread.sleep(1000); } catch (InterruptedException ie) {}
            }
        }
    }

    private void clickWithJS(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element
        );
    }
}