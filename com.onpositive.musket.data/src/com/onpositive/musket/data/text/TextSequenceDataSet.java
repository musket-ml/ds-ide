package com.onpositive.musket.data.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.DescriptionEntry;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetDelta;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.labels.LabelsSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.text.SequenceLabelingFactories.SequenceLayout;

public class TextSequenceDataSet  implements IDataSet,ITextDataSet,IHasClassGroups{

	private SequenceLayout extension;

	public TextSequenceDataSet(ArrayList<Document> docs2) {
		this.docs.addAll(docs2);
		settings.put(GenericDataSet.FONT_SIZE, "13");
		settings.put(GenericDataSet.MAX_CHARS_IN_TEXT, "700");
	}

	public TextSequenceDataSet() {
		settings.put(GenericDataSet.FONT_SIZE, "13");
		settings.put(GenericDataSet.MAX_CHARS_IN_TEXT, "700");
	}

	public TextSequenceDataSet(DataSetSpec spec, Map<String, Object> options) {
		ITabularDataSet tb=spec.tb;
		ArrayList<IColumn> classColumns=new ArrayList<>();
		Object object = options.get(CLAZZ_COLUMNS);
		if (object!=null) {
			String[] split = object.toString().split(",");
			for (String s:split) {
				classColumns.add(tb.getColumn(s.trim()));
			}
		}
		SequenceLayout ll=new SequenceLayout(tb.getColumn("sentence_id"),tb.getColumn("doc_id"), tb.getColumn(WORD_COLUMN), classColumns);
		doRead(ll, tb);
		this.extension=ll;
		this.settings.putAll(options);
	}
	public final static String WORD_COLUMN="WORD_COLUMN";

	public TextSequenceDataSet(DataSetSpec spec, SequenceLayout extension) {
		ITabularDataSet tb=spec.tb;
		doRead(extension, tb);
		this.extension=extension;
		settings.put(GenericDataSet.FONT_SIZE, "13");
		settings.put(GenericDataSet.MAX_CHARS_IN_TEXT, "700");
		
		settings.put(CLAZZ_COLUMNS, extension.classColumns.stream().map(x->x.id()).collect(Collectors.joining(",")));
		settings.put(WORD_COLUMN, extension.wordColumn.id());
	}
	
	int num;

	protected void doRead(SequenceLayout extension, ITabularDataSet tb) {
		HashMap<String, Sentence>st=new HashMap<>();
		LinkedHashMap<String, Document>dt=new LinkedHashMap<>();
		
		if (extension.docColumn!=null) {
			
			tb.items().forEach(v->{
				String id = extension.sentenceColumn.getValueAsString(v).trim();
				String did = extension.docColumn!=null?extension.docColumn.getValueAsString(v).trim():id;
				Document dm=dt.get(did);
				if (dm==null) {
					dm=new Document(this, num);
					num++;
					dt.put(did, dm);
				}
				Sentence sentence = st.get(id);
				if (sentence==null) {
					sentence=new Sentence(dm);
					st.put(id, sentence);
					dm.contents.add(sentence);
				}
				String[]vals=new String[1+extension.classColumns.size()];
				vals[0]=extension.wordColumn.getValueAsString(v).trim();
				int cm=1;
				for (IColumn c:extension.classColumns) {
					vals[cm++]=c.getValueAsString(v).trim();
				}
				sentence.tokens.add(new Token(sentence, vals));
			});
		}
		this.docs.addAll(dt.values());
	}

	protected ArrayList<Document>docs=new ArrayList<>();
	private int text;
	
	private boolean outputsUniq=false;
	private int outputCount;
	
	public int getOutputCount() {
		return outputCount;
	}

	public boolean isOutputsUniq() {
		return outputsUniq;
	}

	
	
	protected ArrayList<String>classNames=new ArrayList<>();

	protected void init() {
		this.text=0;
		classGroups = this.classes();
		this.outputCount=classGroups.size();
		LinkedHashSet<String>all=new LinkedHashSet<>();
		int sum=0;
		for (LinkedHashSet<String>sm:classGroups) {
			all.addAll(sm);
			sum=sum+sm.size();
		}
		if (sum==all.size()) {
			outputsUniq=true;
		}
		else {
			
		}
	}

	public ArrayList<LinkedHashSet<String>>classes(){
		ArrayList<LinkedHashSet<String>>classes=new ArrayList<>();
		this.docs.forEach(v->{
			ArrayList<LinkedHashSet<String>> tclasses = v.classes();
			for (int i=0;i<tclasses.size();i++) {
				LinkedHashSet<String> string = tclasses.get(i);
				if (classes.size()<=i) {
					classes.add(new LinkedHashSet<>());
				}
				classes.get(i).addAll(string);
			}
		});
		return classes;
	}
	
	public int textPosition() {
		return text;
	}
	
	protected LabelsSet labels;
	
	protected static String TEXT_COLUMN="TEXT_COLUMN";
	
	protected static String CLAZZ_COLUMNS="CLAZZ_COLUMNS";

	protected IColumn textColumn;
	protected ArrayList<IItem> items ;
	protected Map<String, Object> settings = new LinkedHashMap<String, Object>();
	private String name = "";
	private ArrayList<LinkedHashSet<String>> classGroups;
	private ClassVisibilityOptions classVisibility;
	
	
	

	@Override
	public int length() {
		return items().size();
	}

	@Override
	public List<? extends IItem> items() {
		if (this.items == null) {
			this.items = this.createItems();
		}
		return this.items;
	}

	protected ArrayList<IItem> createItems(){
		ArrayList<IItem> arrayList = new ArrayList<>();
		arrayList.addAll(this.docs);
		return arrayList;
	}

	@Override
	public IDataSetDelta compare(IDataSet d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IItem item(int num) {
		return items().get(num);
	}
	
	@Override
	public Map<String, Object> getSettings() {
		return settings;
	}

	@Override
	public String name() {
		return this.name;
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
				
				return new Parameter[] {parameter,parameter1,parameter2};
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

	@Override
	public void setSettings(IVisualizerProto proto, Map<String, Object> parameters) {
		this.settings.putAll(parameters);
		this.classVisibility=null;
	}
	
	public ClassVisibilityOptions getVisibility() {
		if (classVisibility==null) {
			Object object = this.settings.get(GenericDataSet.CLASS_VISIBILITY_OPTIONS);
			classVisibility=new ClassVisibilityOptions(object!=null?object.toString():"", this);
		}
		return classVisibility;
	}

	@Override
	public IDataSet subDataSet(String string, List<? extends IItem> arrayList) {
		TextSequenceDataSet textSequenceDataSet = new TextSequenceDataSet((ArrayList<Document>) arrayList);
		textSequenceDataSet.name=string;
		return textSequenceDataSet;
	}

	@Override
	public IDataSet withPredictions(IDataSet t2) {
		TextSequenceDataSet preds=new TextSequenceDataSet(new DataSetSpec((ITabularDataSet) t2, null), extension);
		
		return new TextSequenceDataSetWithPredictions(this,preds);
	}

	@Override
	public List<DescriptionEntry> description() {
		return new ArrayList<>();
	}

	@Override
	public List<ConversionAction> conversions() {
		return new ArrayList<>();
	}

	@Override
	public ArrayList<LinkedHashSet<String>> classGroups() {
		if (this.classGroups==null) {
			this.init();
		}
		return this.classGroups;
	}
}
