package com.onpositive.musket.data.images;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class MultiClassSegmentationItem implements ISegmentationItem,IBinarySegmentationItem,IMultiClassSegmentationItem{
	
	protected MultiClassSegmentationDataSet base;
	protected ArrayList<ITabularItem> items;
	private ArrayList<IMask> rleMasks;
	private String id;
	
	public MultiClassSegmentationItem(String id,MultiClassSegmentationDataSet binarySegmentationDataSet, ArrayList<ITabularItem> items) {
		this.base=binarySegmentationDataSet;
		this.items=items;
		this.id=id;
	}

	@Override
	public Image getImage() {
		BufferedImage bufferedImage = base.representer.get(id);
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		getMask().drawOn(image,0x777777);
		
		Object object = base.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_ALPHA);
		drawMasks(image,object);
		
		base.drawOverlays(this.id(),image);
		return image;
	}

	protected void drawMasks(BufferedImage image,Object object) {
		
		int[] rgbs=new int[base.classes.size()];
		int num=0;
		int a=0;
		for (Object o:base.classes) {
			String classMaskKey = base.getClassMaskKey(o);
			Object object2 = base.getSettings().get(classMaskKey);
			if (classMaskKey==null||object2==null) {
				rgbs[num++]=new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)).getRGB();
			}
			else {
			rgbs[num++]=AbstractRLEImageDataSet.parse(object.toString(),object2.toString());
			}
		}
		
		for (IMask m:getMasks()) {
			m.drawOn(image, rgbs[a]);
			a++;
		}
	}

	@Override
	public String id() {
		return id;
	}

	
	
	@Override
	public IDataSet getDataSet() {
		return base;
	}

	@Override
	public ArrayList<IMask> getMasks() {
		if (this.rleMasks==null) {
			ArrayList<IMask>mssk=new ArrayList<IMask>();
			this.items.forEach(v->{
				RLEMask createMask = base.createMask(base.rleColumn.getValueAsString(v), base.height, base.width,this);
				createMask.setClazz(base.clazzColumn.getValue(v).toString());
				mssk.add( createMask);
			});
			this.rleMasks=mssk;
		}
		return this.rleMasks;
	}

	@Override
	public IMask getMask() {
		IMask r=null;
		for (IMask m:getMasks()) {
			if (r==null) {
				r=m;
			}
			else {
				r=r.union(m);
			}
		}
		if (r==null) {
			return base.createMask("-1", base.height, base.width,this);
		}
		return r;
	}

	@Override
	public boolean isPositive() {
		if (this.items.isEmpty()) {
			return false;
		}
		if (this.getMask().isEmpty()) {
			return false;
		}
		return true;
	}

	public List<? extends ISegmentationItem> items() {
		ArrayList<BinarySegmentationItem>result=new ArrayList<BinarySegmentationItem>();
		this.items.forEach(v->{
			BinarySegmentationItem bi=new BinarySegmentationItem(this.base, v);
			if (bi.isPositive()) {
				result.add(bi);
			}
		});
		return result;
	}

	@Override
	public void drawOverlay(Image image, int color) {
		getMask().drawOn(image,color);
	}

	@Override
	public ArrayList<String> classes() {
		ArrayList<String>result=new ArrayList<>();
		for (IMask m:this.getMasks()) {
			if(m.isEmpty()) {
				continue;
			}
			result.add(m.clazz());
		}
		if (result.isEmpty()) {
			result.add("Empty");
		}
		return result;
	}

	@Override
	public BinarySegmentationItem getItem(String clazz) {
		for (ITabularItem v:this.items) {
			String string = base.clazzColumn.getValue(v).toString();
			if (string.equals(clazz)) {
				BinarySegmentationItem bi=new BinarySegmentationItem(this.base, v);
				return bi;
			}
			
		}
		return new BinarySegmentationItem(this.base, new RLEMask("-1", this.base.width, this.base.height));
	}

	@Override
	public Point getImageDimensions() {
		return base.representer.getDimensions(id());
	}

	public boolean hasSameClass() {
		HashSet<String>classes=new HashSet<>();
		for (ITabularItem i:this.items) {
			String string = base.clazzColumn.getValue(i).toString();
			if (!classes.add(string)) {
				return true;
			}
		}		
		return false;
	}

}