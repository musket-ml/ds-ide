package com.onpositive.musket.data.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MusketFileReader {
	
	public static String readStringFile(File file) {
		if(!file.exists()) {
			throw new RuntimeException("File does not exist: " + file.getAbsolutePath());
		}
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		String content = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			byte[] buf = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l;
			while((l=bis.read(buf))>=0) {
				baos.write(buf, 0, l);
			}
			content = new String(baos.toByteArray(),"utf-8");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}

}
