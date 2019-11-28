package com.onpositive.dside.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AugmentCompletionsBuildHelper {

	public static void main(String[] args) {
		try {
			InputStream in = new URL("https://raw.githubusercontent.com/aleju/imgaug/master/README.md").openStream();
			Files.copy(in, Paths.get("temp.txt"), StandardCopyOption.REPLACE_EXISTING);
			BufferedReader reader = new BufferedReader(new FileReader("temp.txt"));
			PrintWriter out = new PrintWriter(new File("augmenters.txt")); 
			boolean listStarted = false;
			while (reader.ready()) {
				String line = reader.readLine().trim();
				if (!listStarted && line.startsWith("## List of Augmenters")) {
					listStarted = true;
				} else if (listStarted) {
					if (line.startsWith("|") && line.endsWith("|") && line.indexOf('(') > 0) {
						out.println(line.substring(1, line.length() - 1).trim());
					}
				}
			}
			reader.close();
			out.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
