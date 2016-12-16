package edu.gatech.cse6242;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Task1 {

  public static class graphMapper
       extends Mapper<Object, Text, Text, IntWritable> {
  

	private IntWritable weight = new IntWritable();
	private Text node = new Text();
        
				
	public void map(Object key, Text value, Context context) 
		throws IOException, InterruptedException{

	  StringTokenizer itr = new StringTokenizer(value.toString(),"\t");
	  String[] row = new String[itr.countTokens()];
		  
	  int i=0;
	  while (i<3) 
	  {
	     row[i]=itr.nextToken();  
	     i=i+1; 
	  }	
	  node.set( row[1] ) ;
	  weight.set( Integer.parseInt( row[2] ) );  
		  
	  context.write(node, weight);  
		  		  
	}
  }

  public static class graphReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> 
  {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException 
	{
      int weight_value = 0;
      for (IntWritable val : values) 
	  {
      if (weight_value <= val.get()){
        weight_value = val.get();}
      }
      
	  if (weight_value!=0)
	  {
		result.set(weight_value);
		context.write(key, result);  
	  }
	
	}
  }

   public static void main(String[] args) throws Exception 
  {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Task1");

	job.setJarByClass(Task1.class);
	
    job.setMapperClass(graphMapper.class);
    job.setCombinerClass(graphReducer.class);
    job.setReducerClass(graphReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
