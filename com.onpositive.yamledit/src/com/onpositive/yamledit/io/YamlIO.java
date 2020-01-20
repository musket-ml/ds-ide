package com.onpositive.yamledit.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Node;

public class YamlIO {

	public static <T> T loadFromFile(File file, Class<T> clazz) {
		if (file.exists()) {
			try (FileReader fileReader = new FileReader(file)) {
				Yaml yaml = getYamlTool(clazz);
				return yaml.loadAs(fileReader, clazz);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> T loadAs(Reader reader, Class<T> clazz) {
		Yaml yaml = getYamlTool(clazz);
		return yaml.loadAs(reader, clazz);	
	}

	private static <T> Yaml getYamlTool(Class<T> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		Yaml yaml = loader != null ? new Yaml(new CustomClassLoaderConstructor(loader)) : new Yaml();
		return yaml;
	}
	
	public static <T> T loadAs(InputStream stream, Class<T> clazz) {
		Yaml yaml = getYamlTool(clazz);
		return yaml.loadAs(stream, clazz);	
	}
	
	public static <T> T loadAs(String str, Class<T> clazz) {
		return loadAs(new StringReader(str), clazz);	
	}

	public static void dump(Object data, Writer writer) {
		DumperOptions opts = new DumperOptions();
		opts.setDefaultFlowStyle(FlowStyle.BLOCK);
		new Yaml(opts).dump(data,writer);
	}
	
	public static String dump(Object data) {
		DumperOptions opts = new DumperOptions();
		opts.setDefaultFlowStyle(FlowStyle.BLOCK);
		return new Yaml().dump(data);
	}

	public static Node compose(Reader reader) {
		return new Yaml().compose(reader);
	}

	public static Object load(Reader reader) {
		return new Yaml().load(reader);
	}
	
	public static Object load(String yaml) {
		return new Yaml().load(yaml);
	}

	public static Object load(InputStream inputStream) {
		return new Yaml().load(inputStream);
	}
	
}
