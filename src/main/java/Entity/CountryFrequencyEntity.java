package Entity;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public class CountryFrequencyEntity {
    private String country;
    private int count;
    private double probability;

    public CountryFrequencyEntity() {
    }

    public CountryFrequencyEntity(String country, int count) {
        this.country = country;
        this.count = count;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
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

