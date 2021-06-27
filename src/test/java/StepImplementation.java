import com.sun.org.glassfish.gmbal.Description;
import com.thoughtworks.gauge.Step;
import static io.restassured.RestAssured.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.Test;
import java.util.List;

public class StepImplementation {

    private static String URL = "https://api.trello.com/1/";
    private static String board_id="";
  
    @Test
    @Description("Status'un 200(Success) gelme kontrolü")
    
    public void testDoRequestForStatusCode(){
        requestSpecification.
                when().get(baseURI).then().statusCode(HttpStatus.SC_OK);
    }
    
    public JSONObject getRequest(){
        JSONObject request = new JSONObject();
        request.put("key", "625b8aa70bcd71e5d33d77bc412591d5");
        request.put("token", "1a661e80d50192b5b67383ca4f4f1c2e5c34caec58a4a3af54fd5d1135dcf5f6");
        return request;
    }
    
    public void deleteACart(String cardId,JSONObject request){

        given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                delete(URL + "cards/" + cardId).
                then().
                statusCode(200);

    }

    private  String getRandomCardId() {

        JSONObject request =getRequest();

        Response response = given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                get(URL + "boards/" + board_id + "/cards").
                then().statusCode(200).
                extract().
                response();

        List<String> jsonResponse = response.jsonPath().getList("$");
        int randomCardNum = (int) (Math.random() * jsonResponse.size());

        return response.jsonPath().getString("id[" + randomCardNum + "]");

    }

    private  String getListsOnABoard() {

        JSONObject request =getRequest();

        Response response = given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                get(URL + "boards/" + board_id + "/lists").
                then().
                statusCode(200).
                extract().
                response();

        return response.jsonPath().getString("id[0]");
    }

    @Step("<boardName> adinda yeni Board olusturulur")
    public void createABoard(String boardName) {

        try {

        JSONObject request =getRequest();
        request.put("name", boardName);

        Response response = given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                post(URL + "boards").
                then().
                statusCode(200).
                extract().
                response();

        String id = response.jsonPath().getString("id");
        board_id=id;

        }catch (Exception ex){
            System.out.println("HATA! " + ex.getMessage());

        }
    }

    @Step("<cartName> adinda Kart olusturulur")
    public void createACard(String cardName) {

        try {
        JSONObject request =getRequest();
        request.put("idList", getListsOnABoard());
        request.put("name", cardName);

        given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                post(URL + "cards").
                then().
                statusCode(200);

        }catch (Exception ex){
            System.out.println("HATA! " + ex.getMessage());

        }
    }
    
    @Step("Random bir Karti <updatedCardName> guncelle")
    public void updateACard(String updatedCardName) {

        try {

        JSONObject request =getRequest();
        request.put("name", updatedCardName);

        given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                put(URL + "cards/" + getRandomCardId()).
                then().
                statusCode(200);

        }catch (Exception ex){
            System.out.println("HATA! " + ex.getMessage());

        }
    }

    @Step("Kartlari tek tek sil")
    public void deleteAllCardList() {

        try {

        JSONObject request =getRequest();

        Response response = given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                get(URL + "boards/" + board_id + "/cards").
                then().statusCode(200).
                extract().
                response();

        List<String> jsonResponse = response.jsonPath().getList("$");
        for (int i = 0; i < jsonResponse.size(); i++) {

            String cardId = response.jsonPath().getString("id[" + i + "]");

            deleteACart(cardId,request);

        }
        }catch (Exception ex){
            System.out.println("HATA! " + ex.getMessage());

        }
    }

    @Step("<5> saniye bekle")
    public void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Board sil")
    public void deleteABoard() {

        try {

        JSONObject request =getRequest();

        given().
                header("Accept-Encoding", "gzip, deflate").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                body(request.toJSONString()).
                when().
                delete(URL + "boards/" + board_id).
                then().
                statusCode(200);

        }catch (Exception ex){
            System.out.println("HATA! " + ex.getMessage());

        }
    }

}