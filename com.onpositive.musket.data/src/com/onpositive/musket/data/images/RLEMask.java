package com.onpositive.musket.data.images;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class RLEMask implements IMask{

	protected String rle;
	protected String rlePatchAdd="";
	protected String rlePatchRemove="";
	protected int width;
	protected int height;
	
	
	protected String clazz="";
	
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@Override
	public String clazz() {
		return clazz;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
		result = prime * result + height;
		result = prime * result + ((rle == null) ? 0 : rle.hashCode());
		result = prime * result + ((rlePatchAdd == null) ? 0 : rlePatchAdd.hashCode());
		result = prime * result + ((rlePatchRemove == null) ? 0 : rlePatchRemove.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RLEMask other = (RLEMask) obj;
		if (bounds == null) {
			if (other.bounds != null)
				return false;
		} else if (!bounds.equals(other.bounds))
			return false;
		if (height != other.height)
			return false;
		if (rle == null) {
			if (other.rle != null)
				return false;
		} else if (!rle.equals(other.rle))
			return false;
		if (rlePatchAdd == null) {
			if (other.rlePatchAdd != null)
				return false;
		} else if (!rlePatchAdd.equals(other.rlePatchAdd))
			return false;
		if (rlePatchRemove == null) {
			if (other.rlePatchRemove != null)
				return false;
		} else if (!rlePatchRemove.equals(other.rlePatchRemove))
			return false;
		if (width != other.width)
			return false;
		return true;
	}


	protected Rectangle bounds;
	
	public RLEMask(String mask,int height,int width) {
		this.rle=mask;
		this.width=width;
		this.height=height;
	}
	
	public RLEMask(BufferedImage mask) {		
		updateFrom(mask);
	}
	public RLEMask(BitSet mask,int width,int height) {
		int i=width*height;
		updateRLE(i, mask);
		this.width=width;
		this.height=height;
		this.bounds=null;
	}

	public void updateFrom(BufferedImage mask) {
		int i = mask.getWidth()*mask.getHeight();
		BitSet all=new BitSet(i);
		for (int x=0;x<mask.getWidth();x++) {
			for (int y=0;y<mask.getWidth();y++) {
				int rgb = mask.getRGB(x, y);
				if (rgb!=0&&rgb!=-16777216) {
					all.set(x*height+y);
				}
			}	
		}
		updateRLE(i, all);
		this.width=mask.getWidth();
		this.height=mask.getHeight();
		this.bounds=null;
	}
	
	public void updateFrom(BitSet bitset) {
		updateRLE(bitset.size(),bitset);		
	}

	private void updateRLE(int size, BitSet all) {
		String newRLE = bitsetToString(size, all);
		this.rle = newRLE;
	}
	
	private void updateRLEPatch(int size, BitSet all, boolean add) {
		
		BitSet addBitSet = rleToBitSet(this.rlePatchAdd);
		BitSet removeBitSet = rleToBitSet(this.rlePatchRemove);
		
		if(add) {
			addBitSet.or(all);
			removeBitSet.andNot(all);
		}
		else {
			addBitSet.andNot(all);
			removeBitSet.or(all);
		}
		
		this.rlePatchAdd = bitsetToString(size, addBitSet);
		this.rlePatchRemove = bitsetToString(size, removeBitSet);
	}

	protected String bitsetToString(int size, BitSet all) {
		StringBuilder bld=new StringBuilder();
		int index=0;
		while (index<size) {
			int nextSetBit = all.nextSetBit(index);
			if (nextSetBit==-1) {
				break;
			}
			int end=all.nextClearBit(nextSetBit);
			if (end==-1) {
				end=size+1;
			}
			bld.append(' ');
			bld.append(nextSetBit);
			bld.append(' ');
			bld.append(end-nextSetBit);
			index=end;
		}
		return bld.toString().trim();
	}

	@Override
	public Image getImage(int color) {
		BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		BitSet all = constructFinalBitSet();
		Rectangle bounds=new Rectangle(width, height, -1, -1);
		for (int i =0;i<all.size();i++) {
			if (all.get(i)) {
				int x = i/height;
				
				int y = i%height;
				if (x==width) {
					x=x-1;
					y=height-1;
				}
				
				bounds.add(x, y);
				image.setRGB(x, y, color);
				
			}
		}
		this.bounds=bounds;
		return image;
	}

	private BitSet constructFinalBitSet() {
		BitSet all = getBooleanMask();
		BitSet add = rleToBitSet(rlePatchAdd);
		BitSet remove = rleToBitSet(rlePatchRemove);
		all.or(add);
		all.andNot(remove);
		return all;
	}


	public BitSet getBooleanMask() {
		return rleToBitSet(this.rle);
	}

	protected BitSet rleToBitSet(String str) {
		BitSet all=new BitSet(width*height);
		if (str.isEmpty()||str.trim().equals("-1")) {
			return all;
		}
		String[] split = str.split(" ");
		

		for (int i=0;i<split.length;i=i+2) {
			int st=Integer.parseInt(split[i]);
			
			int len=Integer.parseInt(split[i+1]);
			for (int j=st;j<st+len;j++) {
				
				all.set(j);
			}
		}
		return all;
	}
	public boolean checkValid() {
		String str=this.rle;
		if (str.trim().isEmpty()) {
			return true;
		}
		if (str.trim().equals("-1")) {
			return true;
		}
		BitSet all=new BitSet(width*height);
		String[] split = str.split(" ");
		

		for (int i=0;i<split.length;i=i+2) {
			int st=Integer.parseInt(split[i]);
			
			int len=Integer.parseInt(split[i+1]);
			for (int j=st;j<st+len;j++) {
				if (all.get(j)) {
					return false;
				}
				all.set(j);
			}
		}
		return true;
	}


	@Override
	public Rectangle getBounds() {
		if (this.bounds==null) {
			this.getImage();
		}
		return this.bounds;
	}


	public String getRle() {
		return this.rle();
	}

	@Override
	public String rle() {
		return this.rle;
	}

	@Override
	public void update(Image image) {
		this.updateFrom((BufferedImage) image);
		
	}

	@Override
	public void remove(Image image) {
		BufferedImage i=(BufferedImage) image;
		BitSet all = new BitSet(width*height);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				int rgb = i.getRGB(x, y);
				if (rgb!=0) {
					all.set(x*height+y);
				}
			}	
		}
		updateRLEPatch(all.size(), all, false);
		this.width=i.getWidth();
		this.height=i.getHeight();
		this.bounds=null;
	}
	
	@Override
	public void add(Image image) {
		BufferedImage i=(BufferedImage) image;
		BitSet all = new BitSet(width*height);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				int rgb = i.getRGB(x, y);
				if (rgb!=0) {
					all.set(x*height+y);
				}
			}	
		}
		updateRLEPatch(all.size(), all, true);
		this.width=i.getWidth();
		this.height=i.getHeight();
		this.bounds=null;
	}

	@Override
	public String rawRle() {
		return this.rle;
	}

	@Override
	public String addPatchRle() {
		return this.rlePatchAdd;
	}

	@Override
	public String removePatchRle() {
		return this.rlePatchRemove;
	}
	
	public void setAddPatchRle(String str) {
		this.rlePatchAdd = str;
	}

	public void setRemovePatchRle(String str) {
		this.rlePatchRemove = str;
	}
	
	public void applyPatch() {
		BitSet bs = constructFinalBitSet();
		String str = bitsetToString(bs.size(), bs);
		this.rle = str;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}
	

}