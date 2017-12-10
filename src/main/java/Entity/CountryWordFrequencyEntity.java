package Entity;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


public class CountryWordFrequencyEntity {
    private String country;
    private String word;
    private int count;
    private double probability;

    public CountryWordFrequencyEntity() {
    }

    public CountryWordFrequencyEntity(String country, String word, int count) {
        this.country = country;
        this.word = word;
        this.count = count;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getProbability() {
        return this.probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}

