package com.tarkalabs.retrofitconverters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by kalpesh on 02/05/17.
 */

public class EnvelopeConverter extends Converter.Factory {
  private final Gson gson;

  private EnvelopeConverter(Gson gson) {
    if (gson == null) throw new NullPointerException("gson == null");
    this.gson = gson;
  }

  public static EnvelopeConverter create() {
    return create(new Gson());
  }

  public static EnvelopeConverter create(Gson gson) {
    return new EnvelopeConverter(gson);
  }

  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations,
      Retrofit retrofit) {
    for (final Annotation annotation : annotations) {
      if (annotation instanceof ResponseEnvelope) {
        return new Converter<ResponseBody, Object>() {
          @Override public Object convert(ResponseBody value) throws IOException {
            ResponseEnvelope responseEnvelope = (ResponseEnvelope) annotation;
            JsonObject jsonObject = new JsonParser().parse(value.charStream()).getAsJsonObject();
            if (jsonObject.has(responseEnvelope.responseName())) {
              JsonObject responseObject =
                  jsonObject.getAsJsonObject(responseEnvelope.responseName());
              if (responseObject.has(responseEnvelope.setName())) {
                JsonObject setObject = responseObject.getAsJsonObject(responseEnvelope.setName());
                if (setObject.has(responseEnvelope.objectsName())) {
                  return gson.fromJson(setObject.get(responseEnvelope.objectsName()), type);
                }
              }
            }
            return null;
          }
        };
      }
    }
    return super.responseBodyConverter(type, annotations, retrofit);
  }
}
