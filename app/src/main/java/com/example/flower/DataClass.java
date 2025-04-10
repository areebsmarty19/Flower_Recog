package com.example.flower;

public class DataClass {
    public String name1;
    private String dataTitle;
    private int dataDesc;
    private String dataLang;
    private int dataImage;

    public String getDataTitle() {
        return dataTitle;
    }

    public int getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public int getDataImage() {
        return dataImage;
    }

    public DataClass(String dataTitle, int dataDesc, String dataLang, int dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }

    public static class FlowerData {
        private String name;
        private float[] features;

        public FlowerData(String name, float[] features) {
            this.name = name;
            this.features = features;
        }

        public String getName() {
            return name;
        }

        public float[] getFeatures() {
            return features;
        }
    }
}