package com.tsovak.sparkdemo.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonTransformer implements BaseTransformer {
    private Gson gson = new GsonBuilder().create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    @Override
    public String getAcceptType() {
        return "application/json";
    }
}
