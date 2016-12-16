package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashSet;

public class Task4 {


    ///////////

  public static class TokenizerMapper extends
        Mapper<Object, Text, Text, IntWritable> {
    private Text word1 = new Text();
    private Text word2 = new Text();
    private final static IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer itr = new StringTokenizer(line,"\t");
        if(itr.hasMoreElements()){
         word1.set(itr.nextToken().toLowerCase());

        }
        if(itr.hasMoreElements()){
            word2.set(itr.nextToken().toLowerCase());

        }
        context.write(word1, one);
        context.write(word2, one);
      }	  		  
	  }
  
  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

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


  public static class TokenizerMapper2 extends
        Mapper<Object, Text, Text, IntWritable> {
    
    private Text degree = new Text();
    private final static IntWritable node = new IntWritable(1);

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer itr = new StringTokenizer(line,"\t");
        String[] row = new String[itr.countTokens()];
        int i=0;
        while (i<2) 
        {
           row[i]=itr.nextToken();  
           i=i+1; 
        } 
        degree.set( row[1] ) ;
            
        context.write(degree, node);  
      }         
    }
  
  public static class IntSumReducer2
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

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

/* TODO: Needs to be implemented */
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Task4");
	  job.setJarByClass(Task4.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path("wasbs://ysong346@ysong346.blob.core.windows.net/temp234"));
    job.waitForCompletion(true);
/////////////
    Configuration conf2 = new Configuration();
    Job job2 = Job.getInstance(conf2, "Task4");
    job2.setJarByClass(Task4.class);
    job2.setMapperClass(TokenizerMapper2.class);
    job2.setCombinerClass(IntSumReducer2.class);
    job2.setReducerClass(IntSumReducer2.class);
    job2.setOutputKeyClass(Text.class);
    job2.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job2, new Path("wasbs://ysong346@ysong346.blob.core.windows.net/temp234"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
    System.exit(job2.waitForCompletion(true) ? 0 : 1);

  }
}
