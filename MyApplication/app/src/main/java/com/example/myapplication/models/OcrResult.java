package com.example.myapplication.models;

import java.util.List;

public class OcrResult {
    private String image;
    private List<String> texts;



    public OcrResult(String image) {
        this.image = image;
    }
    public OcrResult(List<String> texts) {
        this.texts = texts;
    }


    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }


    public List<String> getOcrText() {
        return texts;
    }
    public void setOcrText(List<String> texts) {
        this.texts = texts;
    }
}
