package superapp;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.user.UserBoundary;
import superapp.converters.UserConverter;
import superapp.dal.UserEntityRepository;
import superapp.data.UserEntity;
import superapp.data.UserPK;

import javax.annotation.PostConstruct;
import java.util.Optional;


public class UserControllerTests extends ApplicationTests {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private UserConverter converter;
    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void testCreateNewUserAndLoginAndUpdateUser() {
        this.url = "http://localhost:" + this.port + "/superapp/users";
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

        userEntityRepository.save(converter.toEntity(createdUser));

        // Login user:
        this.url = "http://localhost:" + this.port + "/superapp/users/login/2023a.noam.levy/test@test.com";
        ResponseEntity<String> result = restTemplate
                .getForEntity(this.url, String.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Update User
        this.url = "http://localhost:" + this.port + "/superapp/users/2023a.noam.levy/test@test.com";
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userUpdateBodyRequest = "{\n" +
                "    \"email\": \"test@test.com\",\n" +
                "    \"role\": \"SUPERAPP_USER\",\n" +
                "    \"username\": \"testUser2\",\n" +
                "    \"avatar\": \"avatar2\"\n" +
                "}";
        HttpEntity<String> userUpdateRequest = new HttpEntity<>(userUpdateBodyRequest,headers);

        restTemplate.exchange(this.url,HttpMethod.PUT, userUpdateRequest, Void.class);
        Optional<UserEntity> user = userEntityRepository.findById(
                new UserPK("2023a.noam.levy", "test@test.com"));
        user.ifPresent(userEntity -> assertThat(userEntity.getAvatar()).isEqualTo("avatar2"));
        userEntityRepository.deleteAll();
    }
}
