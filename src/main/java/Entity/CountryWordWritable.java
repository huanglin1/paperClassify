package Entity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class CountryWordWritable implements WritableComparable<CountryWordWritable> {
    private Text country;
    private Text word;

    public CountryWordWritable() {
        this.set(new Text(), new Text());
    }

    public CountryWordWritable(Text country, Text word) {
        this.set(country, word);
    }

    public void set(Text country, Text word) {
        this.country = country;
        this.word = word;
    }

    public Text getCountry() {
        return this.country;
    }

    public Text getWord() {
        return this.word;
    }

    public void write(DataOutput dataOutput) throws IOException {
        this.country.write(dataOutput);
        this.word.write(dataOutput);
    }

    public void readFields(DataInput dataInput) throws IOException {
        this.country.readFields(dataInput);
        this.word.readFields(dataInput);
    }

    public int compareTo(CountryWordWritable countryWordWritable) {
        int cmp = this.country.compareTo(countryWordWritable.country);
        return cmp != 0 ? cmp : this.word.compareTo(countryWordWritable.word);
    }

    public int hashCode() {
        return this.country.hashCode() * 163 + this.word.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof CountryWordWritable) {
            CountryWordWritable countryWordWritable = (CountryWordWritable)o;
            return this.country.equals(countryWordWritable.country) && this.word.equals(countryWordWritable.word);
        } else {
            return false;
        }
    }

    public String toString() {
        return this.country + "\t" + this.word;
    }
}
