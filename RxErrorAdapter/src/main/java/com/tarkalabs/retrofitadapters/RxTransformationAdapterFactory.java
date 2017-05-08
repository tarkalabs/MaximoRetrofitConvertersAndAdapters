package com.tarkalabs.retrofitadapters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RxTransformationAdapterFactory extends CallAdapter.Factory {

  private RxTransformer transformer;

  private RxTransformationAdapterFactory(RxTransformer rxTransformer) {
    if (rxTransformer == null) throw new NullPointerException("rxTransformer == null");
    this.transformer = rxTransformer;
  }

  public static RxTransformationAdapterFactory create(RxTransformer rxTransformer) {
    return new RxTransformationAdapterFactory(rxTransformer);
  }

  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    Class<?> rawType = getRawType(returnType);
    final boolean isSingle = Single.class == rawType;
    final boolean isCompletable = Completable.class == rawType;

    if (rawType != Observable.class && !isSingle && !isCompletable) {
      // Return type not a Rx type.
      return null;
    }

    final CallAdapter delegate = retrofit.nextCallAdapter(this, returnType, annotations);
    return new CallAdapter<Object, Object>() {
      @Override public Type responseType() {
        return delegate.responseType();
      }

      @Override public Object adapt(Call<Object> call) {
        Object object = delegate.adapt(call);
        if (isSingle) {
          return ((Single<?>) object).compose(transformer.singleTransformer());
        } else if (isCompletable) {
          return ((Completable) object).compose(transformer.completableTransformer());
        } else {
          return ((Observable<?>) object).compose(transformer.observableTransformer());
        }
      }
    };
  }
}
