package tests;

import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;

@Story("Image Favorite API tests")

public class ImageFavoriteTests extends BaseTest{
    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    static String encodedFile;
    String imageId;
    String deleteHashImage;
    Response response;

    @BeforeEach
    void setUpUp() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        response = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.jsonPath().getString("data.id");
        deleteHashImage = response.jsonPath().getString("data.deletehash");
    }

    @DisplayName("Файл в избранные")
    @Test
    void addFavoriteFileTest() {
            given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    @AfterEach
    void tearDown() {
        if (deleteHashImage != null)
        {
            given()
                    .headers("Authorization", token)
                    .when()
                    .delete("https://api.imgur.com/3/image/{deleteHash}", deleteHashImage)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
    }
}
