package com.tarkalabs.retrofitconverters;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by kalpesh on 02/05/17.
 */

public class ListIndexConverter extends Converter.Factory {
  @Override public Converter<ResponseBody, ?> responseBodyConverter(final Type type,
      final Annotation[] annotations, final Retrofit retrofit) {
    for (Annotation annotation : annotations) {
      if (annotation instanceof ListIndexMapper) {
        final ListIndexMapper indexMapper = (ListIndexMapper) annotation;
        return new Converter<ResponseBody, Object>() {
          @Override public Object convert(ResponseBody value) throws IOException {
            Type arrayType = Array.newInstance((Class<?>) type, 0).getClass();
            Converter nextConverter =
                retrofit.nextResponseBodyConverter(ListIndexConverter.this, arrayType, annotations);
            Object[] objects = (Object[]) nextConverter.convert(value);
            if (indexMapper.index() >= objects.length && indexMapper.safeIndexCheck()) {
              return null;
            }
            return objects[indexMapper.index()];
          }
        };
      }
    }
    return super.responseBodyConverter(type, annotations, retrofit);
  }
}
