package com.onpositive.musket.data.images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.onpositive.musket.data.core.AbstractItem;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.text.ClassVisibilityOptions;
import com.onpositive.musket.data.text.ClassVisibilityOptions2;
import com.onpositive.musket.data.text.IHasClassGroups;

public class MultiClassSegmentationItem extends AbstractItem<MultiClassSegmentationDataSet> implements ISegmentationItem,IBinarySegmentationItem,IMultiClassSegmentationItem{
	
	
	protected ArrayList<ITabularItem> items;
	private ArrayList<IMask> rleMasks;
	private String id;
	
	public MultiClassSegmentationItem(String id,MultiClassSegmentationDataSet binarySegmentationDataSet, ArrayList<ITabularItem> items) {
		super(binarySegmentationDataSet);
		this.owner=binarySegmentationDataSet;
		this.items=items;
		this.id=id;
	}

	@Override
	public Image getImage() {
		BufferedImage bufferedImage = owner.representer.get(id);
		BufferedImage image=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		getMask().drawOn(image,0x777777);
		
		Object object = owner.getSettings().get(BinaryInstanceSegmentationDataSet.MASK_ALPHA);
		drawMasks(image,object);
		
		owner.drawOverlays(this.id(),image);
		return image;
	}

	protected void drawMasks(BufferedImage image,Object object) {
		
		Set<Object> ownerClasses = new LinkedHashSet<Object>(owner.classes);
		int[] rgbs=new int[ownerClasses.size()];
		int num=0;
		int a=0;
		for (Object o:ownerClasses) {
			String classMaskKey = owner.getClassMaskKey(o);
			Object object2 = owner.getSettings().get(classMaskKey);
			if (classMaskKey==null||object2==null) {
				rgbs[num++]=new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)).getRGB();
			}
			else {
			rgbs[num++]=AbstractRLEImageDataSet.parse(object.toString(),object2.toString());
			}
		}
		Object alphaObj = owner.getSettings().get(AbstractRLEImageDataSet.MASK_ALPHA);		
		float alpha = 1.0f;
		if(alphaObj instanceof Float) {
			alpha = (Float) alphaObj;			
		}
		else if(alphaObj instanceof Integer) {
			alpha = (0.0f + (Integer)alphaObj)/255.0f;
		}
		else if(alphaObj instanceof String) {
			try {
				float fVal = Float.parseFloat((String) alphaObj);
				alpha = fVal/255.0f;
				
			}
			catch(Exception e) {}
		}
		Object coloursStr = owner.getSettings().get(MultiClassInstanceSegmentationDataSet.CLASSES_COLOURS);
		ClassVisibilityOptions2 colorOpts = new ClassVisibilityOptions2(coloursStr.toString(),(IHasClassGroups) this.owner.getRoot());
		for (IMask m:getMasks()) {
			if(fits(m, ownerClasses)) {
				if(colorOpts.isVisible(m.clazz())) {
					Color color = colorOpts.getColor(m.clazz());
					int colorInt = color.getRGB();
					m.drawOn(image, colorInt, alpha);
					a++;
				}
			}
		}
	}
	
	protected boolean fits(IMask m, Set<Object> ownerClasses) {
        return true;
	}

	@Override
	public String id() {
		return id;
	}

	
	
	
	@Override
	public ArrayList<IMask> getMasks() {
		if (this.rleMasks==null) {
			ArrayList<IMask>mssk=new ArrayList<IMask>();
			this.items.forEach(v->{
				RLEMask createMask = owner.createMask(owner.rleColumn.getValueAsString(v), owner.height, owner.width,this);
				Object value = owner.clazzColumn.getValue(v);
				if (value!=null) {
				createMask.setClazz(value.toString());
				}
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
			return owner.createMask("-1", owner.height, owner.width,this);
		}
		return r;
	}

	@Override
	public boolean isPositive() {
		if (this.items.isEmpty()) {
			return false;
		}
		for (IMask m:getMasks()) {
			if (!m.isEmpty()) {
				return true;
			}
		}
		return true;
	}

	public List<? extends ISegmentationItem> items() {
		ArrayList<BinarySegmentationItem>result=new ArrayList<BinarySegmentationItem>();
		this.items.forEach(v->{
			BinarySegmentationItem bi=new BinarySegmentationItem(this.owner, v);
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
			String clazz = m.clazz();
			if (this.owner.labels!=null) {
				result.add(this.owner.labels.map(clazz));
				continue;
			}
			result.add(clazz);
		}
		if (result.isEmpty()) {
			result.add("Empty");
		}
		return result;
	}
	public ArrayList<String>originalclasses(){
		ArrayList<String>result=new ArrayList<>();
		for (IMask m:this.getMasks()) {
			if(m.isEmpty()) {
				continue;
			}
			String clazz = m.clazz();
			result.add(clazz);
		}		
		return result;
	}

	@Override
	public BinarySegmentationItem getItem(String clazz) {
		for (ITabularItem v:this.items) {
			String string = owner.clazzColumn.getValue(v).toString();
			if (string.equals(clazz)) {
				BinarySegmentationItem bi=new BinarySegmentationItem(this.owner, v);
				return bi;
			}
			
		}
		return new BinarySegmentationItem(this.owner, new RLEMask("-1", this.owner.width, this.owner.height));
	}

	@Override
	public Point getImageDimensions() {
		return owner.representer.getDimensions(id());
	}

	public boolean hasSameClass() {
		HashSet<String>classes=new HashSet<>();
		for (ITabularItem i:this.items) {
			Object value = owner.clazzColumn.getValue(i);
			if (value==null) {
				return false;
			}
			String string = value.toString();
			if (!classes.add(string)) {
				return true;
			}
		}		
		return false;
	}

}