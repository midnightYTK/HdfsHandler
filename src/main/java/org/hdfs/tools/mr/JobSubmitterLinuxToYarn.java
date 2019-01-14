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
 * 如果封装到jar包中，使用 hadoop -jar 执行xxxx.jar
 * ，则会自动加载本地 HADOOP_HOME 下所有的jar包和配置文件到执行的xxxx.jar的xxxx类的classpath中
 * @author TheDK
 *
 */
public class JobSubmitterLinuxToYarn {
	
	public static void main (String []args) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
		
		Properties properties = new Properties();
		properties.load(JobSubmitterLinuxToYarn.class.getClassLoader().getResourceAsStream("conf.properties"));
		
		// 由于在Linux中运行，无需配置跨平台参数、yarn地址等
		Configuration conf = new Configuration();
		
		// 获取参数
		String defaultFS = properties.getProperty("HDFS_ADDRESS");
		String USERNAME = properties.getProperty("USERNAME");

		
		Job job = Job.getInstance(conf);
		// 1. 封装参数，jar所在的位置
		job.setJarByClass(JobSubmitterLinuxToYarn.class);
		
		// 2. 配置mapper reducer
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		// 3. 配置输出的k v参数
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		Path mrOutputPath = new Path(properties.getProperty("MROUTPUT_PATH"));
		Path mrInputPath = new Path(properties.getProperty("MRINPUT_PATH"));
		FileSystem fs = FileSystem.get(
				new URI(defaultFS), conf, USERNAME);
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
