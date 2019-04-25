package org.hdfs.tools.fileHandler;

/**
 * 本接口实现了对HDFS中的文件读 写 修改 重命名，目录的增 删 改
 * @author TheDK
 *
 */
public interface HDFSClient {
	
	/**
	 * 从本地文件上传到HDFS
	 */
	public void copyFileFromLocal() throws Exception;
	
	/**
	 * 从HDFS向本地下载文件
	 */
	public void getFileToLocal() throws Exception;
	
	/**
	 * 重命名HDFS中的文件
	 */
	public void renameFileInHDFS() throws Exception;
	
	/**
	 * 在HDFS中新建目录
	 */
	public void mkdirInHDFS() throws Exception;
	
	/**
	 * 在HDFS中移动文件
	 */
	public void removeFileInHDFS() throws Exception;
	
	/**
	 * 列出HDFS中的文件
	 */
	public void listFile() throws Exception;
	
	/**
	 * 列出HDFS中的全部文件
	 */
	public void listAllFiles() throws Exception;
	
	/**
	 * 顺序读取HDFS中的文件
	 */
	public void catFileContent() throws Exception;
	
	/**
	 * 随机读取HDFS中的文件
	 */
	public void randomReadFile() throws Exception;
	
	/**
	 * 向HDFS中写文件
	 */
	public void writeFile() throws Exception;
	
}
