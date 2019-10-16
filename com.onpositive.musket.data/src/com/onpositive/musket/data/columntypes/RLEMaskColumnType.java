package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Collection;

import com.onpositive.musket.data.project.DataProject;
import com.onpositive.musket.data.table.AbstractColumnType;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IQuestionAnswerer;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

@Caption("RLE Mask")
public class RLEMaskColumnType extends AbstractColumnType{

	public RLEMaskColumnType(String image, String id, String caption) {
		super(image, id, caption);
	}

	@Override
	public ColumnPreference is(IColumn c, DataProject prj, IQuestionAnswerer answerer) {
		if (like(c)) {
			return ColumnPreference.STRICT;
		}
		return ColumnPreference.NEVER;
	}

	
	public static ArrayList<String> fastSplitWS(final String text) {
	    if (text == null) {
	        throw new IllegalArgumentException("the text to split should not be null");
	    }

	    final ArrayList<String> result = new ArrayList<String>();

	    final int len = text.length();
	    int tokenStart = 0;
	    boolean prevCharIsSeparator = true;  // "preceding char is separator" flag

	    char[] chars = text.toCharArray();

	    for (int pos = 0; pos < len; ++pos) {
	        char c = chars[pos];

	        if ( c == ' ') {
	            if (!prevCharIsSeparator) {
	                result.add(text.substring(tokenStart, pos));
	                prevCharIsSeparator = true;
	            }
	            tokenStart = pos + 1;
	        } else {
	            prevCharIsSeparator = false;
	        }
	    }

	    if (tokenStart < len) {
	        result.add(text.substring(tokenStart));
	    }
	    
	    return result;
	}
	
	public static boolean like(IColumn c) {
		Collection<Object> values = c.values();
		boolean res=values.parallelStream().allMatch(o->{
			if (o==null) {
				return true;
			}
			String z=o.toString().trim();
			if (z.equals("-1")||z.isEmpty()) {
				return true;
			}
			
			ArrayList<String> split =fastSplitWS(z);
			if (split.size()%2!=0) {
				return false;
			}
			
			for (String s:split) {
				try {
					Integer.parseInt(s);
					}catch (Exception e) {
						return false;
					}
			}
			return true;
		});
		
		return res;
	}
}
