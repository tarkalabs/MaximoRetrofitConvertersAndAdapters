package com.tarkalabs.retrofitadapters;

import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by kalpesh on 02/05/17.
 */

public interface RxTransformer {
  Observable.Transformer observableTransformer();

  Single.Transformer singleTransformer();

  Completable.Transformer completableTransformer();
}
