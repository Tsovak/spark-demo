package com.tsovak.sparkdemo.transformer;

public class TextTransformer implements BaseTransformer {

    @Override
    public String render(Object model) {
        return model.toString();
    }

    @Override
    public String getAcceptType() {
        return "content-type: text/html";
    }
}
