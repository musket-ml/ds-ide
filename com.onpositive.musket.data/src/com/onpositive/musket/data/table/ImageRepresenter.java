package com.onpositive.musket.data.table;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.onpositive.musket.data.actions.ConvertResolutionAction;
import com.onpositive.musket.data.core.IProgressMonitor;

public class ImageRepresenter implements Iterable<String> {

	protected ArrayList<String> folders = new ArrayList<String>();
	protected String root;

	protected HashMap<String, File> id2Path = new HashMap<String, File>();

	protected ArrayList<ImageRepresenter> children = new ArrayList<ImageRepresenter>();

	public ImageRepresenter(String root) {
		this.root = root;
	}

	public int size() {
		return id2Path.size();
	}

	public File getFixedResolutionFolder(int width, int height) {
		File file = new File(root, width + "x" + height);
		return file;
	}

	public boolean looksLikeResolutionFolder(String name) {
		int indexOf = name.indexOf('x');
		if (indexOf != -1) {
			try {
				int width = Integer.parseInt(name.substring(0, indexOf));
				int height = Integer.parseInt(name.substring(indexOf + 1));
				return true;
			} catch (Exception e) {

			}
		}
		return false;
	}

	public void convertToResolution(IProgressMonitor monitor, int width, int height) {
		File fixedResolutionFolder = getFixedResolutionFolder(width, height);
		if (!fixedResolutionFolder.exists()) {
			fixedResolutionFolder.mkdir();
		}
		ArrayList<File> allFilesToConvert = new ArrayList<>();
		this.children.forEach(v -> {
			String string = v.folders.get(0);
			if (looksLikeResolutionFolder(string)) {
				return;
			}
			File[] listFiles = new File(root, string).listFiles();
			allFilesToConvert.addAll(Arrays.asList(listFiles));
			new File(fixedResolutionFolder, string).mkdir();
		});
		new ConvertResolutionAction().convertResolution(allFilesToConvert, width, height,
				BufferedImage.SCALE_AREA_AVERAGING, fixedResolutionFolder, monitor);
	}

	public boolean like(IColumn cln) {
		if (cln.caption().toLowerCase().contains("class")) {
			return false;
		}
		boolean like = true;
		for (Object o : cln.values()) {
			if (o != null) {
				if (!id2Path.containsKey(o.toString())) {
					int lastIndexOf = o.toString().lastIndexOf(".");

					if (lastIndexOf != -1) {
						String substring = o.toString().substring(lastIndexOf);
						if (substring.contains("_")) {
							return false;
						}
						if (!id2Path.containsKey(o.toString().subSequence(0, lastIndexOf))) {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		return like;
	}

	public void addFolder(String path) {
		try {
			DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(root, path));
			newDirectoryStream.forEach(v -> {
				Path name = v.getName(v.getNameCount() - 1);
				String name2 = name.toFile().getName();
				int lastIndexOf = name2.lastIndexOf(".");
				String extension = name2.substring(lastIndexOf);
				String withoutExtension = name2.substring(0, lastIndexOf);
				if (extension.equals(".jpg") || extension.equals(".gif") || extension.equals(".png")
						|| extension.equals(".bmp")) {
					id2Path.put(withoutExtension, v.toFile());
				}
			});
			folders.add(path);
			newDirectoryStream.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean looksLikeImageFolder(String path) {
		boolean checks = true;
		try {
			DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(root, path));
			boolean found = false;
			for (Path v : newDirectoryStream) {

				Path name = v.getName(v.getNameCount() - 1);
				String name2 = name.toFile().getName();
				int lastIndexOf = name2.lastIndexOf(".");
				if (lastIndexOf == -1) {
					checks = false;
				} else {
					String withExtension = name2.substring(lastIndexOf);

					if (withExtension.equals(".DS_Store")) {
						continue;
					}
					if (!withExtension.equals(".png") && !withExtension.equals(".jpg")) {
						checks = false;
					}
					found = true;
				}

			}

			newDirectoryStream.close();
			if (!found) {
				return false;
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return checks;
	}

	public BufferedImage get(String id) {
		try {
			File file = getFile(id);
			BufferedImage read = ImageIO.read(file);
			return read;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected void gather(File f,ArrayList<ImageRepresenter>rs) {
		String absolutePath = f.getAbsolutePath();
		if (f.getName().contains("mask")) {
			return ;
		}
		String substring = absolutePath.substring(this.root.length());
		if (looksLikeImageFolder(substring)) {
			ImageRepresenter r1 = new ImageRepresenter(root);
			r1.addFolder(substring);
			rs.add(r1);
		}
		for (File c:f.listFiles()) {
			if (c.isDirectory()) {
				gather(c, rs);
			}
		}
	}

	public void configure() {
		File file = new File(this.root);
		File[] listFiles = file.listFiles();
		ArrayList<ImageRepresenter> rs = new ArrayList<ImageRepresenter>();
		for (File f : listFiles) {
			boolean directory = f.isDirectory();
			if (directory)
				if (looksLikeImageFolder(f.getName())) {
					ImageRepresenter r1 = new ImageRepresenter(root);
					r1.addFolder(f.getName());
					rs.add(r1);
				} else {
					File[] listFiles2 = f.listFiles();
					for (File fa : listFiles2) {
						if (fa.isDirectory()) {
							gather(fa, rs);							
						}
					}
				}
		}
		HashMap<HashSet<String>, ArrayList<ImageRepresenter>> maps = new HashMap<HashSet<String>, ArrayList<ImageRepresenter>>();
		for (ImageRepresenter r : rs) {
			HashSet<String> hashSet = new HashSet<String>(r.id2Path.keySet());
			ArrayList<ImageRepresenter> arrayList = maps.get(hashSet);
			if (arrayList == null) {
				arrayList = new ArrayList<ImageRepresenter>();
				maps.put(hashSet, arrayList);
			}
			arrayList.add(r);
		}
		children.addAll(rs);
		children.forEach(v -> {
			this.id2Path.putAll(v.id2Path);
		});
		if (this.children.isEmpty()) {
			if (looksLikeImageFolder(".")) {
				addFolder(".");
				// ImageRepresenter imageRepresenter = new ImageRepresenter(root);
				// this.children.add(imageRepresenter);
			} else {

			}
		}
	}

	@Override
	public Iterator<String> iterator() {
		return id2Path.keySet().iterator();
	}

	public String getImageDirsString() {
		ArrayList<String> result = new ArrayList<>();
		this.children.forEach(v -> {
			String string = v.folders.get(0);
			result.add('"' + string + '"');
		});
		return "[" + result.stream().collect(Collectors.joining(",")) + "]";
	}

	protected HashMap<String, Point> dims = new HashMap<>();

	public Point getDimensions(String valueAsString) {
		if (dims.containsKey(valueAsString)) {
			return dims.get(valueAsString);
		}
		File file = getFile(valueAsString);

		try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					Point point = new Point(reader.getWidth(0), reader.getHeight(0));
					synchronized (dims) {
						dims.put(valueAsString, point);
					}
					return point;
				} finally {
					reader.dispose();
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException();
		}
		return null;
	}

	protected File getFile(String valueAsString) {
		File file = id2Path.get(valueAsString);
		if (file == null) {
			int lastIndexOf = valueAsString.lastIndexOf('.');
			if (lastIndexOf != -1) {
				file = id2Path.get(valueAsString.substring(0, lastIndexOf));
			}
		}
		return file;
	}

	public boolean isEmpty() {
		return id2Path.isEmpty();
	}
}