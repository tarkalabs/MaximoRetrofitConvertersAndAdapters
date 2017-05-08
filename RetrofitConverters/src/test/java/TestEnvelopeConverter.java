import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tarkalabs.retrofitconverters.EnvelopeConverter;
import com.tarkalabs.retrofitconverters.ResponseEnvelope;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by kalpesh on 08/05/17.
 */

public class TestEnvelopeConverter {
  @Rule public final MockWebServer server = new MockWebServer();

  private Service service;

  @Before public void setUp() {
    Gson gson = new GsonBuilder().create();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(server.url("/"))
        .addConverterFactory(EnvelopeConverter.create(gson))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    service = retrofit.create(Service.class);
  }

  @Test public void testEnvelopeConversion() {
    server.enqueue(new MockResponse().setBody(
        "{\"ResponseStructure\":{\"ResponseSet\":{\"User\":{\"name\":\"Kalpesh\"}}}}"));
    try {
      Call<User> call = service.login();
      Response<User> response = call.execute();
      User body = response.body();
      Assert.assertNotNull(body);
      Assert.assertEquals(body.name, "Kalpesh");
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test public void testNullConversionIfMismatch() {
    server.enqueue(new MockResponse().setBody(
        "{\"ResponseStructure\":{\"Set\":{\"User\":{\"name\":\"Kalpesh\"}}}}"));
    try {
      Call<User> call = service.login();
      Response<User> response = call.execute();
      User body = response.body();
      Assert.assertNull(body);
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  public interface Service {
    @GET("/")
    @ResponseEnvelope(responseName = "ResponseStructure", setName = "ResponseSet", objectsName = "User")
    Call<User> login();
  }
}
