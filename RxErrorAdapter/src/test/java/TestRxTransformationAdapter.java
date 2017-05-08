import com.tarkalabs.retrofitadapters.RxTransformationAdapterFactory;
import com.tarkalabs.retrofitadapters.RxTransformer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Completable;
import rx.Observable;
import rx.Single;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by kalpesh on 08/05/17.
 */

public class TestRxTransformationAdapter {
  @Rule public final MockWebServer server = new MockWebServer();

  private Service service;
  @Mock RxTransformer rxTransformer;

  @Before public void setUp() {
    rxTransformer = Mockito.mock(RxTransformer.class);

    Retrofit retrofit = new Retrofit.Builder().baseUrl(server.url("/"))
        .addConverterFactory(new StringConverterFactory())
        .addCallAdapterFactory(RxTransformationAdapterFactory.create(rxTransformer))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();
    service = retrofit.create(Service.class);
  }

  @Test public void testObservableTransformation() {
    server.enqueue(new MockResponse().setBody("Hi.."));
    when(rxTransformer.observableTransformer()).thenReturn(
        new Observable.Transformer<Object, Object>() {
          @Override public Observable<Object> call(Observable<Object> objectObservable) {
            return objectObservable;
          }
        });
    service.observable();
    verify(rxTransformer).observableTransformer();
    verify(rxTransformer, never()).singleTransformer();
    verify(rxTransformer, never()).completableTransformer();
  }

  @Test public void testSingleTransformation() {
    server.enqueue(new MockResponse().setBody("Hi.."));
    when(rxTransformer.singleTransformer()).thenReturn(new Single.Transformer<Object, Object>() {
      @Override public Single<Object> call(Single<Object> objectSingle) {
        return objectSingle;
      }
    });

    service.single();
    verify(rxTransformer).singleTransformer();
    verify(rxTransformer, never()).observableTransformer();
    verify(rxTransformer, never()).completableTransformer();
  }

  @Test public void testCompletableTransformation() {
    server.enqueue(new MockResponse().setBody("Hi.."));
    when(rxTransformer.completableTransformer()).thenReturn(new Completable.Transformer() {
      @Override public Completable call(Completable completable) {
        return completable;
      }
    });
    service.completable();
    verify(rxTransformer).completableTransformer();
    verify(rxTransformer, never()).observableTransformer();
    verify(rxTransformer, never()).singleTransformer();
  }

  public interface Service {
    @GET("/") Observable<String> observable();

    @GET("/") Single<String> single();

    @POST("/") Completable completable();
  }
}
