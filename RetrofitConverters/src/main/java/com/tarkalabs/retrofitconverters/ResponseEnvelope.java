package com.tarkalabs.retrofitconverters;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by kalpesh on 02/05/17.
 */

@Target(METHOD) @Retention(RUNTIME) public @interface ResponseEnvelope {
  String responseName();

  String setName();

  String objectsName();
}
