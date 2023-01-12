package superapp;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import superapp.boundaries.user.UserBoundary;

import static org.assertj.core.api.Assertions.assertThat;

public class SuperAppObjectsControllerTests extends ApplicationTests{

    @Override
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    //createObjectTest - Post
    @Test
    public void testCreateObject() throws Exception {
        this.url = "http://localhost:" + this.port + "/superapp/objects";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userBodyRequest =
                        "{\n"+
                        "\"type\": \"parent\",\n" +
                        "\"alias\": \"parent\",\n" +
                        "\"active\": \"true\",\n" +

                        "\"createdBy\": {" +
                        "\"userId\": {" +
                        "\"superapp\": \"2023a.noam.levy\",\n" +
                        "\"email\": \"temp@gmail.com\",\n" + "\n}" +
                        "\n}," +
                        "\"objectDetails\": {}" +
                        "\n},";

        HttpEntity<String> objectRequest = new HttpEntity<>(userBodyRequest,headers);
        UserBoundary createObject = this.restTemplate.postForObject(this.url,
                objectRequest,
                UserBoundary.class);

        assertThat(createObject).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(createObject.getUserId().getEmail().equals("temp@gmail.com"));
    }

    //updateObjectTest - Put

    //retrieveObjectTest - Get

    //getAllObjectsTest - Get

    //bindExistingObjectsTest - put

    //getAllChildrenTest - Get

    //getAllParentsTest -Get

    //SearchObjectsByTypeTest - Get

    //SearchObjectsByExactAliasTest - Get

    //SearchObjectsByAliasContainingTest - Get

}
