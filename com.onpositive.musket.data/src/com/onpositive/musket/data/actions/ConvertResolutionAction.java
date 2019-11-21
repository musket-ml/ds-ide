package com.onpositive.musket.data.actions;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.onpositive.musket.data.core.IProgressMonitor;

public class ConvertResolutionAction {

	static class ImageAndName{
		protected BufferedImage image;
		protected String name;
		protected String image_base;
		
		public ImageAndName(BufferedImage image, String name,String base) {
			super();
			this.image = image;
			this.name = name;
			this.image_base=base;
		}		
	}
	
	
	
	public void convertResolution(ArrayList<File>files,int width,int height,int hints,File targetDir,IProgressMonitor monitor) {
		monitor.onBegin("Starting conversion", files.size());
		files.parallelStream().map(x->{
			try {
				BufferedImage read = ImageIO.read(x);
				Image scaledInstance = read.getScaledInstance(width, height, hints);
				BufferedImage resized = new BufferedImage(width, height, read.getType());
		        Graphics2D g2d = resized.createGraphics();
		        g2d.drawImage(scaledInstance, 0, 0, null);
		        g2d.dispose();
				return new ImageAndName(resized, x.getName(),x.getParentFile().getName());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}).forEach(i->{			
			try {
				ImageIO.write(i.image, i.name.substring(i.name.lastIndexOf('.')+1), new File(targetDir,i.image_base+"/"+i.name));
				monitor.onProgress(i.name, 1);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		});
		monitor.onDone("", files.size());
	}
}
