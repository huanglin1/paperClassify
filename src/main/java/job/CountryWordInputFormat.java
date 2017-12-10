package job;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

public class CountryWordInputFormat extends FileInputFormat<Text, Text> {
    public CountryWordInputFormat() {
    }

    public RecordReader<Text, Text> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new CountryWordInputFormat.CountryWordRecordReader();
    }

    public static class CountryWordRecordReader extends RecordReader<Text, Text> {
        private String className = null;
        private LineReader in;
        private Text value = null;
        private Text key = null;
        private int length;
        private int count;
        private FileStatus[] status;
        private FileSystem fs;

        public CountryWordRecordReader() {
        }

        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            FileSplit split = (FileSplit)inputSplit;
            Path path = split.getPath();
            String[] str = path.toString().split("/");//读取父目录-国家名称
            this.className = str[str.length - 1];
            this.fs = path.getFileSystem(taskAttemptContext.getConfiguration());
            this.status = this.fs.listStatus(path);
            this.length = this.status.length;//文档数量
            this.count = 0;
        }

        public boolean nextKeyValue() throws IOException, InterruptedException {//重写nextKeyValue
            if (this.count < this.length) {
                if (this.in == null) {
                    FSDataInputStream fileIn = this.fs.open(this.status[this.count].getPath());
                    this.in = new LineReader(fileIn);
                }
                if (this.key == null) {
                    this.key = new Text();
                }
                this.key.set(this.className);
                if (this.value == null) {
                    this.value = new Text();
                }
                int size = this.in.readLine(this.value);//将单词传递给value
                if (size > 0) {
                    return true;
                } else if ((size <= 0) && (this.count != this.length - 1)) {
                    ++this.count;//读取下一个文件
                    FSDataInputStream fileIn = this.fs.open(this.status[this.count].getPath());
                    this.in = new LineReader(fileIn);
                    this.in.readLine(this.value);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public Text getCurrentKey() throws IOException, InterruptedException {
            return this.key;
        }

        public Text getCurrentValue() throws IOException, InterruptedException {
            return this.value;
        }

        public float getProgress() throws IOException, InterruptedException {
            return 0.0F;
        }

        public void close() throws IOException {
        }
    }
}
