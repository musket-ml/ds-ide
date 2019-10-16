package com.onpositive.yamledit.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

public class YamlIO {

	public static <T> T loadFromFile(File file, Class<T> clazz) {
		if (file.exists()) {
			try (FileReader fileReader = new FileReader(file)) {
					return new Yaml(new CustomClassLoaderConstructor(clazz.getClassLoader())).loadAs(fileReader, clazz);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> T load(Reader reader, Class<T> clazz) {
		return new Yaml(new CustomClassLoaderConstructor(clazz.getClassLoader())).loadAs(reader, clazz);	
	}
	
}
