package com.xiaoju.products.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDPFileUtil {
	public static String read(String file) {
		StringBuffer sb = new StringBuffer();
		Configuration conf = new Configuration();
		FileSystem fs = null;
		FSDataInputStream fin = null;
		BufferedReader in = null;
		try {
			fs = FileSystem.get(conf);
			fin = fs.open(new Path(file));
			String data;
			in = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
			while ((data = in.readLine()) != null) {
				sb.append(data + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static String read4Linux(String file) {
		StringBuffer sb = new StringBuffer();
		String cmd = "hadoop fs -cat " + file;
		InputStream in = null;
		BufferedReader read = null;
		try {
			Process pro = Runtime.getRuntime().exec(cmd);
			pro.waitFor();
			in = pro.getInputStream();
			read = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = read.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private HDPFileUtil() {
	}
}
