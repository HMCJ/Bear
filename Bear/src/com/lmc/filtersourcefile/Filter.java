package com.lmc.filtersourcefile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Filter {
	public static void main(String[] args)throws Exception{
		Configuration conf=new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf,args).getRemainingArgs();
		if(otherArgs.length!=2){
			System.err.println("Usage:");
			System.exit(2);
		}
		Job job=new Job(conf,"filter");
		job.setJarByClass(Filter.class);
		job.setMapperClass(FilterMapper.class);
		job.setReducerClass(FilterReduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job,new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true)?0:1);
	}
}

