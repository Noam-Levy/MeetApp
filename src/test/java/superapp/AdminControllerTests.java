package superapp;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
//import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.UserBoundary;
import superapp.converters.MiniappCommandConverter;
import superapp.converters.SuperAppObjectConverter;
import superapp.converters.UserConverter;
import superapp.dal.MiniAppCommandRepository;
import superapp.dal.SuperAppObjectEntityRepository;
import superapp.dal.UserEntityRepository;
import superapp.data.SuperAppObjectEntity;

import javax.annotation.PostConstruct;
import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
public class AdminControllerTests extends ApplicationTests {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private MiniAppCommandRepository miniAppCommandRepository;

    @Autowired
    private SuperAppObjectEntityRepository superAppObjectRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private MiniappCommandConverter miniappCommandConverter;

    @Autowired
    private SuperAppObjectConverter superAppObjectConverter;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    @AfterEach
    public void tearDown() {
        userEntityRepository.deleteAll();
        miniAppCommandRepository.deleteAll();
        superAppObjectRepository.deleteAll();
    }


    @Test // check we get Forbidden error while request get all users without admin user.
    public void testGetAllUserForbidden() {
        this.url = "http://localhost:" + this.port + "/superapp/admin/users";
        try {
            this.restTemplate.getForEntity(this.url, String.class);
        } catch (Exception e) {
            assertThat(e.getMessage().contains("\"error\":\"Forbidden\"")).isEqualTo(true);
        }
    }

    @Test
    public void testGetAllUsers() {
        createAdmin();
        this.url = "http://localhost:" +
                this.port +
                "/superapp/admin/users?userSuperapp=2023a.noam.levy&userEmail=test@test.com";

        ResponseEntity<String> result = restTemplate
                .getForEntity(this.url, String.class);
        assertThat(result.getBody()).isEqualTo("[{" +
                "\"userId\":{\"superapp\":\"2023a.noam.levy\"," +
                "\"email\":\"test@test.com\"}," +
                "\"role\":\"ADMIN\"," +
                "\"username\":\"testUser\"," +
                "\"avatar\":\"avatar\"" +
                "}]");
    }
    /*
    @Test
    public void testGetAllMiniappCommands() {
        createMiniappCommand();
        this.url = "http://localhost:" + this.port +
                "/superapp/admin/miniapp?userSuperapp=2023a.noam.levy&userEmail=test@test.com";
        ResponseEntity<String> commandHistory =  this.restTemplate.getForEntity(this.url, String.class);

        assertThat(commandHistory).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(commandHistory.getBody()).isEqualTo("[{\n" +
                "    \"command\": \"showDebt\",\n" +
                "    \"targetObject\": {\n" +
                "    \"ObjectId\": {\n" +
                "    \"superapp\": \"2023a.noam.levy,\n" +
                "    \"internalObjectId\": \"1\"\n}\n}," +
                "    \"invokedBy\": \"{\n" +
                "    \"userId\": {\n" +
                "    \"superapp\": \"2023a.noam.levy\",\"\n" +
                "    \"email\": \"u2@test.com\"\n" +
                "     }\n}\n" +
                "}]");
    }
    */

    // TODO exportSpecificMiniAppsCommands

    @Test
    public void deleteAllUsers() {
        createAdmin();
        createMiniappUser();
        createSuperappUser();
        this.url = "http://localhost:" +
                this.port +
                "/superapp/admin/users?userSuperapp=2023a.noam.levy&userEmail=test@test.com";
        this.restTemplate.delete(this.url, String.class);
        assertThat(this.userEntityRepository.findAll()).hasSize(0);
    }
/*
    @Test
    public void deleteAllObjects() {
        createSuperappObject();
        this.url = "http://localhost:" +
                this.port +
                "/superapp/admin/objects?userSuperapp=2023a.noam.levy&userEmail=test@test.com";
        this.restTemplate.delete(this.url, String.class);
        assertThat(this.userEntityRepository.findAll()).hasSize(0);
    }
 */

    // TODO deleteMiniApp

    private void createAdmin() {
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
    }

    private void createSuperappUser() {
        this.url = "http://localhost:" + this.port + "/superapp/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Create MINIAPP_USER
        String userBodyRequest = "{\n" +
                "    \"email\": \"u1@test.com\",\n" +
                "    \"role\": \"SUPERAPP_USER\",\n" +
                "    \"username\": \"testSuperappUser\",\n" +
                "    \"avatar\": \"avatar\"\n" +
                "}";
        HttpEntity<String> userRequest = new HttpEntity<>(userBodyRequest,headers);
        UserBoundary createdUser = this.restTemplate.postForObject(this.url,
                userRequest,
                UserBoundary.class);

        assertThat(createdUser).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(createdUser.getUserId().getEmail()).isEqualTo("u1@test.com");
        // Save to DB
        userEntityRepository.save(userConverter.toEntity(createdUser));
    }

    private void createMiniappUser() {
        this.url = "http://localhost:" + this.port + "/superapp/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Create MINIAPP_USER
        String userBodyRequest = "{\n" +
                "    \"email\": \"u2@test.com\",\n" +
                "    \"role\": \"MINIAPP_USER\",\n" +
                "    \"username\": \"testMiniappUser\",\n" +
                "    \"avatar\": \"avatar\"\n" +
                "}";
        HttpEntity<String> userRequest = new HttpEntity<>(userBodyRequest,headers);
        UserBoundary createdUser = this.restTemplate.postForObject(this.url,
                userRequest,
                UserBoundary.class);

        assertThat(createdUser).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(createdUser.getUserId().getEmail()).isEqualTo("u2@test.com");
        // Save to DB
        userEntityRepository.save(userConverter.toEntity(createdUser));
    }

    private void createSuperappObject() {
        createSuperappUser();
        this.url = "http://localhost:" + this.port + "/superapp/objects";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userBodyRequest =
                "{\n"+
                        "\"type\": \"Group\",\n" +
                        "\"alias\": \"group\",\n" +
                        "\"active\": \"true\",\n" +
                        "\"createdBy\": {" +
                        "\"userId\": {" +
                        "\"superapp\": \"2023a.noam.levy\",\n" +
                        "\"email\": \"u1@gmail.com\",\n" + "\n}" +
                        "\n}," +
                        "\"objectDetails\": {}" +
                        "\n},";

        HttpEntity<String> objectRequest = new HttpEntity<>(userBodyRequest,headers);
        SuperAppObjectBoundary createdObject = this.restTemplate.postForObject(this.url,
                objectRequest,
                SuperAppObjectBoundary.class);

        assertThat(createdObject).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(createdObject.getCreatedBy().getUserId().getEmail().equals("u1@gmail.com"));

        this.superAppObjectRepository.save(this.superAppObjectConverter.toEntity(createdObject));
    }

    private void createMiniappCommand() {
        createMiniappUser();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // invoke MiniappCommand
        this.url = "http://localhost:" + this.port + "/superapp/miniapp/Split";
        String miniappCommandBody = "{\n" +
                "    \"command\": \"showDebt\",\n" +
                "    \"targetObject\": {\n" +
                "    \"ObjectId\": {\n" +
                "    \"superapp\": \"2023a.noam.levy,\n" +
                "    \"internalObjectId\": \"1\"\n}\n},\n" +
                "    \"invokedBy\": \"{\n" +
                "    \"userId\": {\n" +
                "    \"superapp\": \"2023a.noam.levy\",\"\n" +
                "    \"email\": \"u2@test.com\"\n" +
                "     }\n}\n" +
                "}";
        HttpEntity<String> commandRequest = new HttpEntity<>(miniappCommandBody, headers);
        MiniAppCommandBoundary invokedCommand = this.restTemplate.postForObject(this.url,
                commandRequest, MiniAppCommandBoundary.class);
        assertThat(invokedCommand).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(invokedCommand.getInvokedBy().getUserId().getEmail()).isEqualTo("u2@test.com");
        // save to DB
        this.miniAppCommandRepository.save(miniappCommandConverter.toEntity(invokedCommand));
    }
}
