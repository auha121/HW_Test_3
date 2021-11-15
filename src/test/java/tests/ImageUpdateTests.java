package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;

@Story("Image Update API tests")

public class ImageUpdateTests extends BaseTest{
    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    static String encodedFile;
    String updateImageId;
    String uploadedImageId;

    @BeforeEach
    void setUpUp() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        updateImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @DisplayName("Изменение title")
    @Test
    void updateFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .param("title", "Heart")
                .expect()
                .statusCode(200)
                .when()
                .put("https://api.imgur.com/3/account/{username}/image/{imageHash}", "testprogmath", updateImageId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }
}
