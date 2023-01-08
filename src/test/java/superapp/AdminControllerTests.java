package superapp;

import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
//import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.user.UserBoundary;
import superapp.converters.UserConverter;
import superapp.dal.UserEntityRepository;
import javax.annotation.PostConstruct;
import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
public class AdminControllerTests extends ApplicationTests {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    UserConverter userConverter;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    // check we get Forbidden error while request get all users without admin user.
    @Test
    public void testGetAllUserForbidden() {

        this.url = "http://localhost:" + this.port + "/superapp/admin/users";
        try {
            this.restTemplate.getForEntity(this.url, String.class);
        } catch(Exception e){
            assertThat(e.getMessage().contains("\"error\":\"Forbidden\"")).isEqualTo(true);
        }
    }

    @Test
    public void testGetAllUser() {
        this.url = "http://localhost:" + this.port + "/superapp/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Create user Admin
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

        // Save to DB
        userEntityRepository.save(userConverter.toEntity(createdUser));

        this.url = "http://localhost:" +
                this.port +
                "/superapp/admin/users?userSuperapp=2023a.noam.levy&userEmail=test@test.com";
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> result = restTemplate
                .getForEntity(this.url, String.class);
        assertThat(result.getBody()).isEqualTo("[{" +
                "\"userId\":{\"superapp\":\"2023a.noam.levy\"," +
                "\"email\":\"test@test.com\"}," +
                "\"role\":\"ADMIN\"," +
                "\"username\":\"testUser\"," +
                "\"avatar\":\"avatar\"" +
                "}]");
        userEntityRepository.deleteAll();
    }

    // TODO exportMiniAppsCommands

    // TODO exportSpecificMiniAppsCommands

    // TODO deleteUsers

    // TODO deleteObjects

    // TODO deleteMiniApp
}
