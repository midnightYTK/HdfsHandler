package org.hdfs.tools.mr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JobSubmitterFromWindowsToYarn {
	
	public static void main (String []args) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
		
		Properties properties = new Properties();
		properties.load(JobSubmitter.class.getClassLoader().getResourceAsStream("conf.properties"));
		
		// 在代码中配置JVM参数，用于给job对象来获取访问HDFS的用户身份
		System.setProperty("HADOOP_USER_NAME", properties.getProperty("HADOOP_USER_NAME"));
		
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", properties.getProperty("HDFS_ADDRESS"));
		// 默认为local
		conf.set("mapreduce.framework.name", properties.getProperty("mapreduce.framework.name"));
		conf.set("yarn.resoursemanager", properties.getProperty("yarn.resoursemanager"));
		// 配置跨平台提交参数
		conf.set("mapreduce.app_submission.cross_platform", properties.getProperty("mapreduce.app_submission.cross_platform"));
		
		
		Job job = Job.getInstance(conf);
		
		// 1. 封装参数，jar所在的位置
		job.setJar(properties.getProperty("JobJarPath"));
		
		// 2. 配置mapper reducer
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		// 3. 配置输出的k v参数
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		Path mrOutputPath = new Path(properties.getProperty("MROUTPUT_PATH"));
		Path mrInputPath = new Path(properties.getProperty("MRINPUT_PATH"));
		FileSystem fs = FileSystem.get(
				new URI(properties.getProperty("HDFS_ADDRESS")), conf, properties.getProperty("USERNAME"));
		if( fs.exists(mrOutputPath) ) {
			fs.delete(mrOutputPath, true);
		}
		
		// 4. 配置job要处理的数据的输入输出数据集所在路径
		FileInputFormat.setInputPaths(job, mrInputPath);
		FileOutputFormat.setOutputPath(job, mrOutputPath);
		
		// 5. 根据实际需求配置需要的reduce task 数目（默认一个）
		job.setNumReduceTasks(2);
		
		// 6. 将job提交给yarn，并等待程序完成，boolean参数是配置是否print the progress to the user
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:-1);
		
	}
	
}
