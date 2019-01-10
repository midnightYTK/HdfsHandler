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


/**
 * 用于提交mapreduce job的客户端程序
 * 功能：
 *   1、封装本次job运行时所需要的必要参数
 *   2、跟yarn进行交互，将mapreduce程序成功的启动、运行
 * @author TheDK
 *
 */
public class JobSubmitter {
	
	public static void main (String []args) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
		
		Properties properties = new Properties();
		properties.load(JobSubmitter.class.getClassLoader().getResourceAsStream("conf.properties"));
		
		// 在代码中配置JVM参数，用于给job对象来获取访问HDFS的用户身份
		System.setProperty("HADOOP_USER_NAME",properties.getProperty("HADOOP_USER_NAME"));
		
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", properties.getProperty("HDFS_ADDRESS"));
		conf.set("mapreduce.framework.name", properties.getProperty("mapreduce.framework.name"));
		conf.set("yarn.resoursemanager", properties.getProperty("yarn.resoursemanager"));
		conf.set("mapreduce.app_submission.cross_platform", properties.getProperty("mapreduce.app_submission.cross_platform"));
		
		
		Job job = Job.getInstance(conf);
		// 封装参数，jar所在的位置
		job.setJar(properties.getProperty("JobJarPath"));
		// 配置mapper reducer
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		// 配置输出的k v参数
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		Path outputPath = new Path(properties.getProperty("OUTPUT_PATH"));
		Path inputPath = new Path(properties.getProperty("INPUT_PATH"));
		FileSystem fs = FileSystem.get(
				new URI(properties.getProperty("HDFS_ADDRESS")), conf, properties.getProperty("USERNAME"));
		if( fs.exists(outputPath) ) {
			fs.delete(outputPath, true);
		}
		
		// 配置job要处理的数据的输入输出数据集所在路径
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		// 根据实际需求配置需要的reduce task 数目
		job.setNumReduceTasks(2);
		
		// 将job提交给yarn
		boolean waitForCompletion = job.waitForCompletion(true);
		
		System.exit(waitForCompletion?0:-1);
		
	}
	
}
