import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RandomSampling {

    public static class RandomSampleMapper extends Mapper<Object, Text, NullWritable, Text> {
        private Random randomNumber = new Random();
        private Double sample_amt;

        @Override
        public void map(Object key, Text value, Mapper.Context context) throws IOException, InterruptedException {
            String percentage = context.getConfiguration().get("sample_vol");
            sample_amt = Double.parseDouble(percentage) / 100.0;
            if (randomNumber.nextDouble() < sample_amt) {
                context.write(NullWritable.get(), value);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File(args[1]));
        if (args.length != 2) {
            System.err.println("Please specify the input and output path");
            System.exit(-1);
        }
        Configuration conf = new Configuration();
        conf.set("sample_vol","10");
        Job job = Job.getInstance(conf);
        job.setJarByClass(RandomSampling.class);
        job.setJobName("Random Sampling");
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setMapperClass(RandomSampleMapper.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
