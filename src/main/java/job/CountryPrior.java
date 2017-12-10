package job;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.FileSystem;
public class CountryPrior extends  Configured implements Tool{

    //map类
    public static class MyMap extends Mapper<Object, LongWritable, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        protected void map(Object key, LongWritable value, Context context)
                throws IOException, InterruptedException {
            InputSplit inputSplit=context.getInputSplit();
            Text dirNameRes=new Text();

            String  dirName=((FileSplit)inputSplit).getPath().getName();
            dirNameRes.set(dirName);
            context.write(dirNameRes,one);
        }
    }
    //reduce类
    public static class MyReduce extends Reducer<Text,IntWritable,Text,IntWritable>{
        private IntWritable result=new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
    //重写的inputFormat类
    public static class CountryInputFormat extends FileInputFormat<Text, LongWritable> {
        public CountryInputFormat() {
        }

        public RecordReader<Text, LongWritable> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            return new CountryPrior.CountryInputFormat.CountryRecordReader();
        }

        public static class CountryRecordReader extends RecordReader<Text, LongWritable> {
            private long length;
            private long count;
            private Text key = null;
            private LongWritable value = null;
            private String className = null;

            public CountryRecordReader() {
            }

            public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
                FileSplit split = (FileSplit) inputSplit;
                Path path = split.getPath();
                String[] str = path.toString().split("/");
                this.className = str[str.length - 1];//读取文件父目录名，也就是国家名称
                FileSystem fs = path.getFileSystem(taskAttemptContext.getConfiguration());
                FileStatus[] status = fs.listStatus(path);
                this.length = (long) status.length;
                this.count = 1L;
            }
            //重写initialize和nextKeyValue方法
            public boolean nextKeyValue() throws IOException, InterruptedException {
                if (this.count <= this.length) {
                    this.key = new Text(this.className);//让key值=国家名称，value=1。
                    this.value = new LongWritable(1L);
                    ++this.count;
                    return true;
                } else {
                    return false;
                }
            }

            public Text getCurrentKey() throws IOException, InterruptedException {
                return this.key;
            }

            public LongWritable getCurrentValue() throws IOException, InterruptedException {
                return this.value;
            }

            public float getProgress() throws IOException, InterruptedException {
                return 0.0F;
            }

            public void close() throws IOException {
            }
        }
    }

    //重写的run方法，设置hadoop运行的配置。
    public int run(String[] args)throws Exception{
        Configuration conf=new Configuration();
        conf.set("mapred.textoutputformat.separator",",");//key value 分隔符
        Job job = this.parseInputAndOutput(this, conf, args);

        job.setInputFormatClass(CountryInputFormat.class);
        // 3.3 set input path
        // FileInputFormat.addInputPath(job, new Path(args[0]));
        // 3.4 set mapper
        job.setMapperClass(MyMap.class);
        // 3.5 set map output key/value class
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(IntWritable.class);
        // 3.6 set partitioner class
        // job.setPartitionerClass(HashPartitioner.class);
        // 3.7 set reduce number
        // job.setNumReduceTasks(1);
        // 3.8 set sort comparator class
        // job.setSortComparatorClass(LongWritable.Comparator.class);
        // 3.9 set group comparator class
        // job.setGroupingComparatorClass(LongWritable.Comparator.class);
        // 3.10 set combiner class
        // job.setCombinerClass(null);
        // 3.11 set reducer class
        job.setReducerClass(MyReduce.class);
        // 3.12 set output format

        job.setOutputFormatClass(TextOutputFormat.class);
        // 3.13 job output key/value class
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 3.14 set job output path
        // FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 4 submit job
        boolean isSuccess = job.waitForCompletion(true);
        // 5 exit
        // System.exit(isSuccess ? 0 : 1);
        return isSuccess ? 0 : 1;
    }

    //设置job的输入输出路径
    public Job parseInputAndOutput(Tool tool, Configuration conf, String[] args)
            throws Exception {
        // validate
        if (args.length != 2) {
            System.err.printf("Usage:%s [genneric options]<input><output>\n",
                    tool.getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return null;
        }
        // 2 create job
        Job job = new Job(conf, tool.getClass().getSimpleName());
        // 3.1 set run jar class
        job.setJarByClass(tool.getClass());
        // 3.3 set input path
        FileInputFormat.addInputPath(job, new Path(args[0]));
        // 3.14 set job output path
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job;
    }
    //主函数
    public static void main(String[] args) throws Exception {
        // run mapreduce
        int status = ToolRunner.run(new CountryPrior(), args);
        // 5 exit

        System.exit(status);
    }


}
