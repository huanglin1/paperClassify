//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Entity;

public class PreditionResultEntity {
    private String fileName;
    private Double probability1;
    private Double probability2;
    private String country;
    private Double probability3;
    public PreditionResultEntity(String fileName, Double probability1, Double probability2,Double probabality3, String country) {
        this.fileName = fileName;
        this.probability1 = probability1;
        this.probability2 = probability2;
        this.country = country;
        this.probability3=probabality3;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getProbability1() {
        return this.probability1;
    }

    public void setProbability1(Double probability1) {
        this.probability1 = probability1;
    }

    public Double getProbability2() {
        return this.probability2;
    }

    public void setProbability2(Double probability2) {
        this.probability2 = probability2;
    }

    public Double getProbability3() {
        return probability3;
    }

    public void setProbability3(Double probability3) {
        this.probability3 = probability3;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
