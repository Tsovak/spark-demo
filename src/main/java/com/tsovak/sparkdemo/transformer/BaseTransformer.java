package com.tsovak.sparkdemo.transformer;

import spark.ResponseTransformer;

public interface BaseTransformer extends ResponseTransformer {

    String getAcceptType();
}
