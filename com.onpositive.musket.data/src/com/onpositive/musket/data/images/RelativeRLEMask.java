package com.onpositive.musket.data.images;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class RelativeRLEMask extends RLEMask{


	public RelativeRLEMask(BitSet mask, int width, int height) {
		super(mask, width, height);
	}

	public RelativeRLEMask(BufferedImage mask) {
		super(mask);
	}

	public  RelativeRLEMask(String mask, int height, int width) {
		super(mask, height, width);
	}

	protected BitSet rleToBitSet(String str) {
		BitSet all=new BitSet(width*height);
		if (str.isEmpty()||str.trim().equals("-1")) {
			return all;
		}
		String[] split = str.split(" ");
		int currentPosition=0;
		for (int i=0;i<split.length;i=i+2) {
			int st=Integer.parseInt(split[i]);
			
			int len=Integer.parseInt(split[i+1]);
			for (int j=st;j<st+len;j++) {
				all.set(currentPosition+j);
			}
			currentPosition+=st;
			currentPosition+=len;
		}
		return all;
	}
	
	protected String bitsetToString(int size, BitSet all) {
		StringBuilder bld=new StringBuilder();
		int index=0;
		int prevBit=0;
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
			bld.append(nextSetBit-prevBit);
			bld.append(' ');
			bld.append(end-nextSetBit);
			index=end;
			prevBit=end;
		}
		String trim = bld.toString().trim();
		return trim;
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
		int currentPosition=0;
		for (int i=0;i<split.length;i=i+2) {
			int st=Integer.parseInt(split[i]);
			
			int len=Integer.parseInt(split[i+1]);
			for (int j=st;j<st+len;j++) {
				if (all.get(currentPosition+j)) {
					return false;
				}
				all.set(currentPosition+j);
			}
			currentPosition+=st;
			currentPosition+=len;
		}
		return true;

	}

}