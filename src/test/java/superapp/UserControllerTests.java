package superapp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.user.UserBoundary;

import javax.annotation.PostConstruct;

public class UserControllerTests extends ApplicationTests {

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.url = "http://localhost:" + this.port + "/superapp/users";
    }

    @Test
    public void testCreateNewUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userBodyRequest = "{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"role\": \"ADMIN\",\n" +
                "    \"username\": \"testUser\",\n" +
                "    \"avatar\": \"avatar\"\n" +
                "}";
        HttpEntity<String> userRequest = new HttpEntity<>(userBodyRequest,headers);
        UserBoundary createdUser = this.restTemplate.postForObject(this.url,
                userRequest,
                UserBoundary.class);

        assertThat(createdUser).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(createdUser.getUserId().getEmail()).isEqualTo("test@test.com");
    }
}
