package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;

@Story("Image Delete API tests")

public class ImageDeleteTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    static String encodedFile;
    String uploadedImageId;

    @BeforeEach
    void setUpUp() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        uploadedImageId = given()
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

    @DisplayName("Удаление файла с авторизацией")
    @Test
    void deleteFileImageTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "testprogmath", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @DisplayName("Удаление файла без авторизации")
    @Test
    void deleteFileUnAuthorizeTest() {
        given()
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(401);
    }

    @DisplayName("Удаление файла без файла")
    @Test
    void deleteFileWithoutImageTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "testprogmath", "")
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
