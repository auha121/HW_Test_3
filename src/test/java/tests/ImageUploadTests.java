package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@Story("Image Upload API tests")

public class ImageUploadTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    static String encodedFile;
    String deleteHashImage;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

    @DisplayName("Загрузка файла в формате base64")
    @Test
    void uploadFileTest() {
        deleteHashImage = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @DisplayName("Загрузка файла в формате jpg")
    @Test
    void uploadFileImageTest() {
        deleteHashImage = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @DisplayName("Загрузка файла без файла")
    @Test
    void uploadFileWithoutImageTest() {
        deleteHashImage = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @DisplayName("Загрузка файла без авторизации")
    @Test
    void uploadFileWithoutAuthTest() {
        deleteHashImage = given()
                .multiPart("image", encodedFile)
                .expect()
                .statusCode(401)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
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