package sa.edu.kau.fcit.cpit252.project.support;

import org.junit.jupiter.api.Test;
import sa.edu.kau.fcit.cpit252.project.api.HealthController;
import sa.edu.kau.fcit.cpit252.project.domain.UserRole;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductOfferEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ReviewEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ShopEntity;
import sa.edu.kau.fcit.cpit252.project.entity.UserEntity;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupportAndEntityUnitTest {

    @Test
    void authHeaderUtilParsesBearerAndRejectsInvalidInput() {
        assertThatThrownBy(() -> AuthHeaderUtil.bearerToken(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("required");
        assertThatThrownBy(() -> AuthHeaderUtil.bearerToken("Token abc"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Bearer");
        assertThat(AuthHeaderUtil.bearerToken("Bearer  abc123  ")).isEqualTo("abc123");
    }

    @Test
    void healthControllerReturnsExpectedPayload() {
        Map<String, String> payload = new HealthController().health();
        assertThat(payload.get("status")).isEqualTo("ok");
        assertThat(payload.get("service")).isEqualTo("project-sanad-api");
    }

    @Test
    void entitiesExposeFieldsAndPrePersistDefaults() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@sanad.sa");
        user.setPasswordHash("hash");
        user.setRole(UserRole.CUSTOMER);
        invokeOnCreate(user);
        assertThat(user.getCreatedAt()).isNotNull();
        user.setCreatedAt(LocalDateTime.of(2026, 1, 1, 1, 1));
        assertThat(user.getCreatedAt().getYear()).isEqualTo(2026);

        CategoryEntity category = new CategoryEntity();
        category.setId(10L);
        category.setName("Plumbing");
        category.setSlug("plumbing");
        category.setParent(null);
        assertThat(category.getId()).isEqualTo(10L);
        assertThat(category.getName()).isEqualTo("Plumbing");
        assertThat(category.getSlug()).isEqualTo("plumbing");
        assertThat(category.getParent()).isNull();

        ProductEntity product = new ProductEntity();
        product.setId(20L);
        product.setName("Pipe");
        product.setVariant("Copper");
        product.setDescription("Desc");
        product.setCategory(category);
        product.setImageUrl("http://img");
        assertThat(product.getCategory().getName()).isEqualTo("Plumbing");

        ShopEntity shop = new ShopEntity();
        shop.setId(30L);
        shop.setOwner(user);
        shop.setName("Shop");
        shop.setDescription("D");
        shop.setContactInfo("C");
        shop.setCategory("Tools");
        assertThat(shop.getId()).isEqualTo(30L);
        assertThat(shop.getOwner().getEmail()).isEqualTo("user@sanad.sa");
        assertThat(shop.getName()).isEqualTo("Shop");
        assertThat(shop.getDescription()).isEqualTo("D");
        assertThat(shop.getContactInfo()).isEqualTo("C");
        assertThat(shop.getCategory()).isEqualTo("Tools");

        ProductOfferEntity offer = new ProductOfferEntity();
        offer.setId(40L);
        offer.setProduct(product);
        offer.setShop(shop);
        offer.setPrice(new BigDecimal("12.50"));
        offer.setAvailable(true);
        offer.setStockQuantity(8);
        invokeOnCreate(offer);
        assertThat(offer.getCreatedAt()).isNotNull();
        assertThat(offer.getPrice()).isEqualByComparingTo("12.50");
        assertThat(offer.isAvailable()).isTrue();
        assertThat(offer.getStockQuantity()).isEqualTo(8);

        ReviewEntity review = new ReviewEntity();
        review.setId(50L);
        review.setCustomer(user);
        review.setTargetType(ReviewTargetType.PRODUCT);
        review.setTargetId(20L);
        review.setRating(5);
        invokeOnCreate(review);
        assertThat(review.getId()).isEqualTo(50L);
        assertThat(review.getCustomer()).isEqualTo(user);
        assertThat(review.getCreatedAt()).isNotNull();
        assertThat(review.getTargetType()).isEqualTo(ReviewTargetType.PRODUCT);
        assertThat(review.getTargetId()).isEqualTo(20L);
        assertThat(review.getRating()).isEqualTo(5);
        LocalDateTime fixed = LocalDateTime.of(2026, 5, 1, 12, 0);
        review.setCreatedAt(fixed);
        assertThat(review.getCreatedAt()).isEqualTo(fixed);
    }

    private static void invokeOnCreate(Object entity) throws Exception {
        Method method = entity.getClass().getDeclaredMethod("onCreate");
        method.setAccessible(true);
        method.invoke(entity);
    }
}
