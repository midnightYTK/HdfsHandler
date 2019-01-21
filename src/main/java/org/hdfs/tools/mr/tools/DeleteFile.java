package org.hdfs.tools.mr.tools;

import java.io.File;

public class DeleteFile {
	
	/**
	 * delete file/path itself and it's all son file/path.
	 * @param file
	 */
	public static void delAllFile(File file) {
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
