package sa.edu.kau.fcit.cpit252.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthEndpointRespondsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.service").value("project-sanad-api"));
    }

    @Test
    void categoryTreeEndpointReturnsNestedData() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].children").exists());
    }

    @Test
    void productListingSupportsFiltering() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("category", "plumbing")
                        .param("minPrice", "10")
                        .param("maxPrice", "30")
                        .param("availableOnly", "true")
                        .param("minRating", "4")
                        .param("sort", "price_asc")
                        .param("page", "1")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.items[0].categorySlug").value("plumbing"));
    }

    @Test
    void compareEndpointSupportsStrategySorting() throws Exception {
        long productId = findProductIdByName("Copper Pipe");

        JsonNode asc = readJson(mockMvc.perform(get("/api/compare")
                        .param("productId", Long.toString(productId))
                        .param("sort", "price_asc"))
                .andExpect(status().isOk())
                .andReturn());

        JsonNode desc = readJson(mockMvc.perform(get("/api/compare")
                        .param("productId", Long.toString(productId))
                        .param("sort", "price_desc"))
                .andExpect(status().isOk())
                .andReturn());

        BigDecimal lowest = asc.get(0).get("price").decimalValue();
        BigDecimal highest = desc.get(0).get("price").decimalValue();

        assertThat(highest).isGreaterThanOrEqualTo(lowest);
    }

    @Test
    void productDetailsAndOffersEndpointsWork() throws Exception {
        long productId = findProductIdByName("Copper Pipe");

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.bestPrice").exists());

        mockMvc.perform(get("/api/products/{id}/offers", productId)
                        .param("sort", "price_desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").exists());
    }

    @Test
    void reviewValidationRejectsOutOfRangeRating() throws Exception {
        String token = loginToken("customer@sanad.sa", "customer123");
        long productId = findProductIdByName("Copper Pipe");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetType": "PRODUCT",
                                  "targetId": %d,
                                  "rating": 6
                                }
                                """.formatted(productId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reviewEndpointRejectsInvalidAuthorizationHeader() throws Exception {
        long productId = findProductIdByName("Copper Pipe");
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Token wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetType": "PRODUCT",
                                  "targetId": %d,
                                  "rating": 4
                                }
                                """.formatted(productId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reviewSummaryReturnsAverageAndCount() throws Exception {
        long productId = findProductIdByName("Copper Pipe");

        mockMvc.perform(get("/api/reviews/summary")
                        .param("targetType", "PRODUCT")
                        .param("targetId", Long.toString(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(org.hamcrest.Matchers.greaterThan(0.0)))
                .andExpect(jsonPath("$.totalReviews").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.visualRating").exists());
    }

    @Test
    void authRegisterLoginAndMeWork() throws Exception {
        String email = "newuser+" + System.nanoTime() + "@sanad.sa";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "API User",
                                  "email": "%s",
                                  "password": "sanad123",
                                  "role": "CUSTOMER"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email));

        JsonNode login = readJson(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "sanad123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn());

        String token = login.get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    private long findProductIdByName(String productName) throws Exception {
        JsonNode response = readJson(mockMvc.perform(get("/api/products")
                        .param("limit", "100"))
                .andExpect(status().isOk())
                .andReturn());

        for (JsonNode item : response.get("items")) {
            if (productName.equals(item.get("name").asText())) {
                return item.get("id").asLong();
            }
        }

        throw new IllegalStateException("Product not found in seed data: " + productName);
    }

    private String loginToken(String email, String password) throws Exception {
        JsonNode login = readJson(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn());

        return login.get("token").asText();
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
