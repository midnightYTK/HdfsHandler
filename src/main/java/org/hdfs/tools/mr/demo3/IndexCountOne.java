package org.hdfs.tools.mr.demo3;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.hdfs.tools.mr.tools.DeleteFile;


/**
 * 文件格式：
 *  a.txt:hello world, This is a txt hello txt. 
 *  b.txt:hello world, This is b txt nohello txt. 
 *  c.txt:hello world, This isn't c txt.
 * 最终结果：
 * 	txt a.txt-->2 b.txt-->2 c.txt-->1
 * 	hello a.txt-->2	b.txt-->1 c.txt-->1
 * 	world a.txt-->1 b.txt-->1 c.txt-->1
 *  ...
 * @author TheDK
 * 
 */
public class IndexCountOne {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		String rootPathStr = new String("E:/Workbench/mrdata/indexcount/");
		
		Configuration configuration = new Configuration();
		Job job = Job.getInstance(configuration);
		
		job.setJarByClass(IndexCountOne.class);
		
		job.setMapperClass(IndexCountOneMapper.class);
		job.setReducerClass(IndexCountOneReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(rootPathStr+"input"));
		FileOutputFormat.setOutputPath(job, new Path(rootPathStr+"output1"));
		
		File file = new File(rootPathStr+"output1");
		if (file.exists()) {
			DeleteFile.delAllFile(file);
		}
		
		job.setNumReduceTasks(3);
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:1);
	}
	
}


class IndexCountOneMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	@Override
	protected void map(LongWritable key, Text value, 
			Mapper<LongWritable, Text, Text, IntWritable>.Context context)
					throws IOException, InterruptedException {
		FileSplit inputSplit = (FileSplit) context.getInputSplit();
		String filename = inputSplit.getPath().getName();
		String[] splits = value.toString().split(" ");
		for(String split : splits) {
			context.write(new Text(split + " " + filename), new IntWritable(1));
		}
	}
}

class IndexCountOneReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int count = 0;
		for (IntWritable intWritable : values) {
			count += intWritable.get();
		}
		context.write(key, new IntWritable(count));
	}
}