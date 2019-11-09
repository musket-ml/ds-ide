package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.generic.GenericDataSet;

public class TextSequenceDataSetWithPredictions extends TextSequenceDataSet{

	public TextSequenceDataSetWithPredictions(TextSequenceDataSet textSequenceDataSet, TextSequenceDataSet preds) {
		Map<String, IItem> itemMap = preds.itemMap();
		textSequenceDataSet.items().forEach(v->{
			Document rs=(Document) v;
			Document iItem = (Document) itemMap.get(v.id());
			this.docs.add(new DocumentWithPredictions(this, rs,iItem));
		});
		this.settings.put(FOCUS_ON_DIFFERENCES, true);
		this.settings.putAll(textSequenceDataSet.settings);
	}
	public static final String FOCUS_ON_DIFFERENCES="Only highlight differences";

	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		ArrayList<DocumentWithPredictions>d=(ArrayList<DocumentWithPredictions>) arrayList;
		ArrayList<Document>gt=new ArrayList<>();
		ArrayList<Document>pt=new ArrayList<>();
		d.forEach(v->{
			gt.add(v);
			pt.add(v.getPredictions());
		});
		TextSequenceDataSetWithPredictions textSequenceDataSet = new TextSequenceDataSetWithPredictions(new TextSequenceDataSet(gt),new TextSequenceDataSet(pt));
		textSequenceDataSet.name=string;
		return textSequenceDataSet;
	}
	
	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {

			@Override
			public Parameter[] parameters() {
			
				Parameter parameter = new Parameter();
				parameter.name=GenericDataSet.FONT_SIZE;
				Map<String, Object> settings2 = getSettings();
				parameter.defaultValue=settings2.get(GenericDataSet.FONT_SIZE).toString();
				parameter.type=Integer.class;
				
				Parameter parameter1 = new Parameter();
				parameter1.name=GenericDataSet.MAX_CHARS_IN_TEXT;
				parameter1.defaultValue=settings2.get(GenericDataSet.MAX_CHARS_IN_TEXT).toString();
				parameter1.type=Integer.class;
				
				Parameter parameter2 = new Parameter();
				parameter2.name=GenericDataSet.CLASS_VISIBILITY_OPTIONS;
				Object object = settings2.get(GenericDataSet.CLASS_VISIBILITY_OPTIONS);
				if (object==null) {
					object="";
				}
				parameter2.defaultValue=object.toString();
				parameter2.type=ClassVisibilityOptions.class;
				
				Parameter parameter3 = new Parameter();
				parameter3.name=FOCUS_ON_DIFFERENCES;
				object = settings2.get(FOCUS_ON_DIFFERENCES);
				if (object==null) {
					object="";
				}
				parameter3.defaultValue=object.toString();
				parameter3.type=boolean.class;
				
				return new Parameter[] {parameter,parameter1,parameter2,parameter3};
			}

			@Override
			public String name() {
				return "Card visualizer";
			}

			@Override
			public String id() {
				return "Card visualizer";
			}

			@Override
			public Supplier<Collection<String>> values(IDataSet ds) {
				return null;
			}
		};
	}
}
