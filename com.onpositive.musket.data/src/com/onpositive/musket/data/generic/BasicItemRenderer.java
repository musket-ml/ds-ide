package com.onpositive.musket.data.generic;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextArea;
public class BasicItemRenderer {

	
	public static void main(String[] args) {
		
		JPanel pnl=new JPanel();
		BorderLayout borderLayout = new BorderLayout();
		pnl.setLayout(borderLayout);
		
		JTextArea label=new JTextArea("hello world");
		label.setWrapStyleWord(true);
		label.setLineWrap(true);
		//label.setSize(33, 100);
		pnl.add(label, BorderLayout.EAST);
		
		JTextArea label1=new JTextArea("hello world2");
		label1.setWrapStyleWord(true);
		label1.setLineWrap(true);
		//label1.setSize(33, 100);
		pnl.add(label1, BorderLayout.WEST);
		
		pnl.doLayout();
		pnl.setLocation(0, 0);
		pnl.setSize(64, 64);
		//borderLayout.layoutContainer(pnl);
		label1.setBounds(0, 0, 32, 32);
		label.setBounds(0, 32, 32, 32);
		BufferedImage img=new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		pnl.paint(img.getGraphics());
		try {
			ImageIO.write(img, "png", new File("D:/t.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
