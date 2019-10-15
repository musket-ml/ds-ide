package com.onpositive.musket.data.columntypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.IColumnType.ColumnPreference;

public class ColumnLayoutMask {

	public static class ColumnTypeMask {
		
		public ColumnTypeMask(Class<? extends IColumnType> clazz, int minCardinality2, int maxCardinality2) {
			this.columnType=clazz;
			this.minCardinality=minCardinality2;
			this.maxCardinality=maxCardinality2;
		}
		protected Class<?>columnType;
		
		protected int minCardinality=1;
		protected int maxCardinality=1;
		
		public int match(ColumnLayout layout,HashSet<ColumnInfo>toIgnore) {
			Collection<ColumnInfo> infos = layout.infos();
			int strictMatchCount=0;
			int weakMatchCount=0;
			for (ColumnInfo i:infos) {
				if (toIgnore.contains(i)) {
					continue;
				}
				ColumnPreference columnPreference = i.prefs.get(columnType);
				if (columnPreference==ColumnPreference.STRICT) {
					toIgnore.add(i);
					strictMatchCount++;
					weakMatchCount++;
				}
				if (columnPreference==ColumnPreference.MAYBE) {
					toIgnore.add(i);
					weakMatchCount++;
				}
			}
			if (strictMatchCount>=minCardinality&&strictMatchCount<=maxCardinality) {
				return 2;
			}
			if (weakMatchCount>=minCardinality&&weakMatchCount<=maxCardinality) {
				return 1;
			}
			return 0;
		}
	}
	@SafeVarargs
	public ColumnLayoutMask(Class<? extends IColumnType>...classez) {
		for (Class<? extends IColumnType> cl: classez) {
			addRequirement(cl, 1, 1);
		}
	}
	
	protected ArrayList<ColumnTypeMask>requirements=new ArrayList<>();
	
	public void addRequirement(Class<? extends IColumnType>clazz,int minCardinality,int maxCardinality) {
		this.requirements.add(new ColumnTypeMask(clazz,minCardinality,maxCardinality));
	}
	
	public double check(ColumnLayout layout) {
		int score=0;
		HashSet<ColumnInfo>toIgnore=new HashSet<>();
		for (ColumnTypeMask m:this.requirements) {
			int match = m.match(layout,toIgnore);
			if (match<=0) {
				return -1;
			}
			score=score+match;
		}
		LinkedHashSet<ColumnInfo> linkedHashSet = new LinkedHashSet<>(layout.infos());
		linkedHashSet.removeAll(toIgnore);
		if (!linkedHashSet.isEmpty()) {
			return score/10;
		}
		return score;		
	}
}