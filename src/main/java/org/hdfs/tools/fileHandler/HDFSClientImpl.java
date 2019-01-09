package org.hdfs.tools.fileHandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;


public class HDFSClientImpl implements HDFSClient {
	
	FileSystem fs = null;
	
	@Before
	public void init() throws Exception {
		
		Properties properties = new Properties();
		properties.load(HDFSClientImpl.class.getClassLoader().getResourceAsStream("conf.properties"));
		String HDFS_ADDRESS = properties.getProperty("HDFS_ADDRESS");
		String USERNAME = properties.getProperty("USERNAME");
		Configuration conf = new Configuration();
		conf.set("dfs.replication","2");
		conf.set("dfs.blocksize", "64m");
		fs = FileSystem.get(new URI(HDFS_ADDRESS), conf, USERNAME);
		
	}
	
	
	@Test
	public void copyFileFromLocal() throws Exception {
		fs.copyFromLocalFile(new Path("E:/Workbench/new2.txt"), new Path("/wordcount/input/"));
		fs.close();
	}
	
	
	@Test
	public void getFileToLocal() throws Exception {
		fs.copyToLocalFile(new Path("/wordcount/output/res.dat"), new Path("E:/Workbench/res.dat"));
		fs.close();
	}
	
	
	@Test
	public void renameFileInHDFS() throws Exception {
		fs.rename(new Path("/new2.txt"), new Path("/newtxt.txt"));
		fs.close();
	}
	
	
	@Test
	public void mkdirInHDFS() throws Exception {
		// 创建目录时可以递归创建
//		fs.mkdirs(new Path("/new1/test"));
		fs.mkdirs(new Path("/wordcount/input/"));
		fs.close();
	}
	
	
	@Test
	public void removeFileInHDFS() throws Exception {
//		fs.delete(new Path("/new"));
		// boolean是否递归删除（如果设置否，则如果该目录下有文件/文件夹就无法删除）
		fs.delete(new Path("/new"), false);
//		fs.delete(new Path("/new"), true);
		fs.close();
	}
	
	/**
	 * 查询所有文件，包括所有的子目录下的所有文件
	 * @throws Exception
	 */
	@Test
	public void listFile() throws Exception {
		// 只查询文件的信息,不返回文件夹的信息
		RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
		while(listFiles.hasNext()) {
			LocatedFileStatus status = listFiles.next();
			System.out.println("文件权限:" + status.getPermission());
			System.out.println("文件拥有者:" + status.getOwner());
			System.out.println("用户组:" + status.getGroup());
			System.out.println("文件大小 :" + status.getLen());
			System.out.println("文件块大小:" + status.getBlockSize());
			System.out.println("文件副本数:" + status.getReplication());
			System.out.println("文件路径:" + status.getPath());
//			System.out.println("未知1?:" + status.getSymlink());
			
			System.out.println("-----------------");
		}
		fs.close();
	}
	
	
	/**
	 * 查询hdfs指定目录下的文件/文件夹，无子目录
	 * @throws Exception
	 */
	@Test
	public void listAllFiles() throws Exception {
		FileStatus[] fileStatus = fs.listStatus(new Path("/"));
		for( FileStatus fStatus : fileStatus ) {
			System.out.println("文件路径:" + fStatus.getPath());
			System.out.println(fStatus.isDirectory()?"该文件是目录":"该文件是文件");
			System.out.println("文件块大小:" + fStatus.getBlockSize());
			System.out.println("文件副本数:" + fStatus.getReplication());
			System.out.println("文件大小:" + fStatus.getLen());
			
			System.out.println("---------------------");
		}
		fs.close();
	}
	
	
	/**
	 * 读取文件内容
	 * @throws Exception
	 */
	@Test
	public void catFileContent() throws Exception {
		FSDataInputStream fSDIS = fs.open(new Path("/new.txt"));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fSDIS, "UTF-8"));
		String line = null;
		while( (line = bufferedReader.readLine())!= null ) {
			System.out.println(line);
		}
		bufferedReader.close();
		fSDIS.close();
		fs.close();
	}
	
	
	/**
	 * 随机文件读取文件内容
	 * @throws Exception
	 */
	@Test
	public void randomReadFile() throws Exception {
		
		FSDataInputStream fsDataInputStream = fs.open(new Path("/new.txt"));
		// 设定读取起始位置
		fsDataInputStream.seek(3);
		// 读取多少字节
		byte[] buf = new byte[6];
		fsDataInputStream.read(buf);
		System.out.println(new String(buf));
		fsDataInputStream.close();
		fs.close();
	}
	
	
	/**
	 * FileSystem.create 是往hdfs文件中写数据（可以写新的，也可以覆盖写）
	 * FileSystem.append 是往hdfs的文件后面追加数据
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	@Test
	public void writeFile() throws Exception {
		
//		FSDataOutputStream outputStream = fs.create(new Path("/new.txt"), true);
		FSDataOutputStream outputStream = fs.append(new Path("/new.txt"));
		FileInputStream inputStream = new FileInputStream("E:/Workbench/new.txt");
		
		byte[] buf = new byte[1024];
		int read = 0;
		while( (read = inputStream.read(buf)) != -1 ) {
			// 错误写法，原因：
			// 这里，因为每次读取buf中的全部数据，在前面不会出现异常 ，但是在最后一次读取文件时，可能读取的是buf中的一部分，
			// 此时，如果继续读取全部，则会出现读取出来的文件有问题，无法识别等情形。
			// outputStream.write(buf);
			outputStream.write(buf, 0, read);
		}
		inputStream.close();
		outputStream.close();
		fs.close();
	}

	
	
}
