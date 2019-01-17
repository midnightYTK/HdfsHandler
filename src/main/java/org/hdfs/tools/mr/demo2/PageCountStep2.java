package org.hdfs.tools.mr.demo2;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class PageCountStep2 {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
		
		String rootPathStr = new String("E:/Workbench/mrdata/uricount/");
		
		Configuration configuration = new Configuration();
        
		Job job = Job.getInstance(configuration);
		
		job.setJarByClass(PageCountStep2.class);
		
		job.setMapperClass(PageCountStep2Mapper.class);
		job.setReducerClass(PageCountStep2Reducer.class);
		
		job.setMapOutputKeyClass(PageCount.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path(rootPathStr+"output"));
		FileOutputFormat.setOutputPath(job, new Path(rootPathStr+"output2"));
		
		File file = new File(rootPathStr+"output2");
		if (file.exists()) {
			delAllFile(file);
		}
		
		// 这里只能配成一个！！！
		job.setNumReduceTasks(1);
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:1);
		
	}
	
	
	private static void delAllFile(File file) {
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles.length != 0) {
				for( File sonFile : listFiles ) {
					delAllFile(sonFile);
				}
			}
		}
		file.delete();
	}
	
}


class PageCountStep2Mapper extends Mapper<LongWritable, Text, PageCount, NullWritable> {
	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, PageCount, NullWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] lineSplit = line.split("\t");
		PageCount pageCount = new PageCount(lineSplit[0], new Integer(lineSplit[1]));
		context.write(pageCount, NullWritable.get());
	}
}

class PageCountStep2Reducer extends Reducer<PageCount, NullWritable, Text, IntWritable> {
	@Override
	protected void reduce(PageCount key, Iterable<NullWritable> values,
			Reducer<PageCount, NullWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		context.write(new Text(key.getUri()), new IntWritable(key.getCount()));
		
	}
}