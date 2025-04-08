package tests;

import com.yakush.pages.CatalogPage;
import com.yakush.pages.HomePage;
import com.yakush.pages.ProductCard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogTests extends BaseTest {
    private static final String SEARCH_QUERY = "iPhone";
        private static final String SEARCH_QUERY_INVALID = "////////////////";

    @Test
    public void testProductSearch() {
        HomePage homePage = new HomePage(driver);
        CatalogPage catalogPage = homePage.navigateToCatalog();

        catalogPage.searchForProduct(SEARCH_QUERY);

        int productsCount = catalogPage.getProductsCount();
        assertTrue(productsCount > 0,
                "Should find at least one product for query: " + SEARCH_QUERY);
        System.out.println("Found products: " + productsCount);

        assertTrue(catalogPage.areAllProductsContainKeyword("iphone"),
                "All products should contain search keyword");
    }

    @Test
    public void testEmptySearchResults() {
        HomePage homePage = new HomePage(driver);
        CatalogPage catalogPage = homePage.navigateToCatalog();

        catalogPage.searchForProduct(SEARCH_QUERY_INVALID);

        assertEquals(0, catalogPage.getProductsCount(),
                "Should not find products for invalid query: " + SEARCH_QUERY_INVALID);
    }

    @Test
    public void testProductCardDetails() {
        HomePage homePage = new HomePage(driver);
        CatalogPage catalogPage = homePage.navigateToCatalog();

        catalogPage.searchForProduct(SEARCH_QUERY);

        ProductCard productCard = catalogPage.openFirstProduct();

        assertTrue(productCard.verifyProductDetails(),
                "Product details should match between list and card");
    }
}