package job;

import java.io.IOException;
import java.util.StringTokenizer;

import Entity.CountryWordWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class CountryWordConditional {
    //map类
    public static class MyMapper extends Mapper<Object, Text, CountryWordWritable, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private Text dirNameRes = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());//读取每一行的单词

                InputSplit inputSplit = context.getInputSplit();
                String dirName = ((FileSplit) inputSplit).getPath().getName();
                dirNameRes.set(dirName);
                //value-国家名称,单词  value-1
                CountryWordWritable countryWordWritable = new CountryWordWritable(dirNameRes, word);

                context.write(countryWordWritable, one);
            }
        }
    }

    //reduce类
    public static class MyReducer
            extends Reducer<CountryWordWritable,IntWritable,CountryWordWritable,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(CountryWordWritable key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);//对同一国家下相同的单词进行求和
            context.write(key, result);
        }
    }
    //主函数--重写的inputFormat类是CountryWordInputFormat类
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: wordcount <in> <out>");
            System.exit(2);
        }
        Job job = new Job(conf, "CountryWordConditional");
        job.setInputFormatClass(CountryWordInputFormat.class);
        job.setJarByClass(CountryWordConditional.class);
        job.setMapperClass(MyMapper.class);
        job.setCombinerClass(MyReducer.class);
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(CountryWordWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
