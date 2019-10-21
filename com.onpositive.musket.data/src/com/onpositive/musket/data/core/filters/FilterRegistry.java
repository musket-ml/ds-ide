package com.onpositive.musket.data.core.filters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.columntypes.IDColumnType;
import com.onpositive.musket.data.columntypes.NumberColumn;
import com.onpositive.musket.data.columntypes.TextColumnType;
import com.onpositive.musket.data.columntypes.ClassColumnType;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IAnalizerProto;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IFilterProto;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.generic.GenericItem;
import com.onpositive.musket.data.images.IMulticlassClassificationItem;
import com.onpositive.musket.data.images.MultiClassClassificationItem;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.IColumnType;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;
import com.onpositive.musket.data.text.ITextItem;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;

public class FilterRegistry {

	static protected HashMap<String, FilterFactory> factories = new HashMap<String, FilterRegistry.FilterFactory>();

	static class FilterFactory implements IFilterProto, IAnalizerProto {
		Class<?> clazz;

		public FilterFactory(Class<?> clazz) {
			super();
			this.clazz = clazz;
		}

		@Override
		public String name() {
			return this.clazz.getAnnotation(Caption.class).value();
		}

		@Override
		public Parameter[] parameters() {
			Constructor<?>[] constructors = clazz.getConstructors();
			for (Constructor<?> c : constructors) {
				if (Modifier.isPublic(c.getModifiers())) {
					java.lang.reflect.Parameter[] parameters = c.getParameters();

					Parameter[] pm = new Parameter[parameters.length];
					int a = 0;
					for (java.lang.reflect.Parameter p : parameters) {
						pm[a] = convertParameter(p);
					}
					return pm;
				}
			}
			return new Parameter[0];
		}

		private Parameter convertParameter(java.lang.reflect.Parameter p) {
			Parameter res = new Parameter();
			res.name = p.getName();
			res.type = p.getType();
			res.required = true;
			res.defaultValue = "";
			return res;
		}

		public boolean canApply(IDataSet d) {
			Type[] interfaces = clazz.getGenericInterfaces();
			for (Type i : interfaces) {
				if (i instanceof ParameterizedType) {
					Type t = ((ParameterizedType) i).getActualTypeArguments()[0];
					if (t instanceof Class) {
						if (!((Class<?>) t).isInstance(d)) {
							return false;
						}
					}
				}
			}
			return true;
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			return (Predicate<IItem>) createItem((HashMap<String, Object>) parameters);
		}

		public boolean isAnalizer() {
			return IAnalizer.class.isAssignableFrom(clazz);
		}

		@Override
		public String id() {
			return clazz.getName();
		}

		@Override
		public IAnalizeResults perform(HashMap<String, Object> analzierArgs, IDataSet dataset) {
			IAnalizer<IDataSet> an = (IAnalizer<IDataSet>) createItem(analzierArgs);
			return an.analize(dataset);
		}

		protected Object createItem(HashMap<String, Object> analzierArgs) {
			Constructor<?>[] constructors = clazz.getConstructors();
			for (Constructor<?> c : constructors) {
				if (Modifier.isPublic(c.getModifiers())) {
					java.lang.reflect.Parameter[] parameters = c.getParameters();
					Object[] values = new Object[parameters.length];
					int a = 0;
					if (values.length > 0) {
						if (analzierArgs.size() == 1) {
							values[0] = analzierArgs.values().iterator().next();
						} else {
							for (java.lang.reflect.Parameter p : parameters) {
								values[a] = analzierArgs.get(p.getName());
								a++;
							}
						}
					}
					try {
						Object newInstance = c.newInstance(values);
						return newInstance;

					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				}
			}
			throw new IllegalStateException();
		}

		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					return Collections.emptyList();
				}
			};
		}

		@Override
		public int score() {
			if (clazz==BasicAnalizer.class) {
				return -1;
			}
			if (AbstractAnalizer.class.isAssignableFrom(clazz)) {
				return 0;
			}
			return 1;
		}
	}

	public static class FilterPredicateFilterFactory extends FilterFactory {

		private boolean mode;

		public FilterPredicateFilterFactory(Class<?> clazz, boolean b) {
			super(clazz);
			this.mode = b;
		}

		@Override
		public String name() {
			ProvidesFilter annotation = clazz.getAnnotation(ProvidesFilter.class);
			if (mode) {
				return annotation.value() + " is more then";
			} else {
				return annotation.value() + " is less then";
			}
		}

		@Override
		public boolean isAnalizer() {
			return false;
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			Object vs = parameters.values().iterator().next();
			Double vvv = Double.parseDouble(vs.toString());
			AbstractAnalizer createItem = (AbstractAnalizer) createItem(new HashMap<>());
			return (Predicate<IItem>) v -> {
				Number group = (Number) createItem.group(v);
				if (mode) {
					boolean b = group.doubleValue() >= vvv.doubleValue();
					return b;
				} else {
					return group.doubleValue() < vvv.doubleValue();
				}
			};
		}

	}

	public static void register(Class<?> clazz) {
		ProvidesFilter annotation = clazz.getAnnotation(ProvidesFilter.class);
		if (annotation != null) {
			factories.put(annotation.value() + " is less then", new FilterPredicateFilterFactory(clazz, false));
			factories.put(annotation.value() + " is more then", new FilterPredicateFilterFactory(clazz, true));
		}
		factories.put(clazz.getName(), new FilterFactory(clazz));
	}

	static {
		register(PositiveNegativeAnalizer.class);
		register(InstanceCountAnalizer.class);
		register(BinaryConfusionMatrix.class);
		register(IOUAnalizer.class);
		register(IdFilter.class);
		register(CorrectlyClassifierFilter.class);
		register(ISPositiveFilter.class);
		register(HasClassAnalizer.class);
		register(HasClassesAnalizer.class);
		register(ClassConfusionMatrix.class);
		register(HasTextFilter.class);
		register(HasClassFilter.class);
		register(WordCountAnalizer.class);
		register(TextLengthAnalizer.class);
		register(SentenceCountAnalizer.class);
		register(F1Analizer.class);
		register(RecallAnalizer.class);
		register(PrecisionAnalizer.class);
		register(BasicAnalizer.class);
	}

	public ArrayList<IFilterProto> getFilters(IDataSet ds) {
		Collection<FilterFactory> values = factories.values();
		ArrayList<IFilterProto> result = new ArrayList<IFilterProto>();
		values.forEach(v -> {
			if (!v.isAnalizer()) {
				if (v.canApply(ds)) {
					result.add(v);
				}
			}
		});
		if (ds instanceof GenericDataSet) {
			DataSetSpec spec = ((GenericDataSet) ds).getSpec();
			processGeneric((ArrayList) result, spec, false);
		}
		return result;
	}

	public static class BasicAnalizerProto implements IAnalizerProto {

		protected String name;
		protected IAnalizer<IDataSet> analizer;
		private int score;

		public BasicAnalizerProto(String name, IAnalizer<IDataSet> analizer,int score) {
			super();
			this.name = name;
			this.analizer = analizer;
			this.score=score;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public Parameter[] parameters() {
			return new Parameter[0];
		}

		@Override
		public String id() {
			return name;
		}

		@Override
		public IAnalizeResults perform(HashMap<String, Object> analzierArgs, IDataSet dataset) {
			return this.analizer.analize(dataset);
		}
		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					return Collections.emptyList();					
				}
			};
		}

		@Override
		public int score() {
			return score;
		}
	}

	public static class BasicColumnFilterProto implements IFilterProto {

		protected AbstractAnalizer analizer;
		private IColumn column;
		private boolean mode;

		public BasicColumnFilterProto(boolean mode, IColumn c, AbstractAnalizer analizer) {
			super();
			this.column = c;
			this.analizer = analizer;
			this.mode = mode;
		}

		@Override
		public String name() {
			ProvidesFilter annotation = analizer.getClass().getAnnotation(ProvidesFilter.class);
			if (mode) {
				return column.caption() + " " + annotation.value() + " is more then";
			} else {
				return column.caption() + " " + annotation.value() + " is less then";
			}
		}

		@Override
		public Parameter[] parameters() {
			return new Parameter[0];
		}

		@Override
		public String id() {
			return name();
		}
		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					return column.uniqueValues().stream().map(x->x.toString()).collect(Collectors.toList());					
				}
			};
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			Object vs = parameters.values().iterator().next();
			Double vvv = Double.parseDouble(vs.toString());
			return (Predicate<IItem>) v -> {
				Number group = (Number) analizer.group(analizer.converter.apply(v));
				if (mode) {
					boolean b = group.doubleValue() >= vvv.doubleValue();
					return b;
				} else {
					return group.doubleValue() < vvv.doubleValue();
				}
			};
		}
	}

	public static class ObjectColumnFilterProto implements IFilterProto {

		protected AbstractAnalizer analizer;
		private IColumn column;
		private Class<? extends IColumnType> type;

		public ObjectColumnFilterProto(IColumn c, Class<? extends IColumnType> class1) {
			this.column = c;
			this.type=class1;
		}

		@Override
		public String name() {
			return column.caption() + "  has value";

		}

		@Override
		public Parameter[] parameters() {
			return new Parameter[0];
		}

		@Override
		public String id() {
			return name();
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			Object vs = parameters.values().iterator().next();
			String val = vs == null ? "" : vs.toString();
			boolean islc = val.toLowerCase().equals(val);
			return (Predicate<IItem>) v -> {
				GenericItem va = (GenericItem) v;
				String valueAsString = column.getValueAsString(va.base());
				if (!islc) {
					return valueAsString.contains(val);
				}
				return valueAsString.toLowerCase().contains(val);
			};
		}
		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					if (type==TextColumnType.class) {
						return words();
					}
					List<String> collect = column.uniqueValues().stream().map(x->x.toString()).collect(Collectors.toList());
					return collect;					
				}
				ArrayList<String>_words=null;

				private Collection<String> words() {
					if (_words==null) {
						HashSet<String>st=new HashSet<>();
						for (Object x:column.uniqueValues()) {
							String[] split = new BreakIteratorTokenizer().split(x.toString());
							for (String s:split) {
								st.add(s);
							}
						}
						_words=new ArrayList<>(st);
						Collections.sort(_words);						
					}
					return _words;
				}
			};
		}
	}
	public static class ObjectColumnFilterNumber implements IFilterProto {

		protected AbstractAnalizer analizer;
		private IColumn column;

		public ObjectColumnFilterNumber(IColumn c) {
			this.column = c;
		}

		@Override
		public String name() {
			return column.caption() + "  has value";

		}

		@Override
		public Parameter[] parameters() {
			return new Parameter[0];
		}

		@Override
		public String id() {
			return name();
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			Object vs = parameters.values().iterator().next();
			String val = vs == null ? "" : vs.toString();
			boolean islc = val.equals("None");
			
			double vlNum=!islc?Double.parseDouble(val):0;
			return (Predicate<IItem>) v -> {
				GenericItem va = (GenericItem) v;
				String valueAsString = column.getValueAsString(va.base());
				
				try {
					return vlNum==Double.parseDouble(valueAsString);
				}catch (NumberFormatException e) {
					return islc;
				}
			};
		}

		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					return column.uniqueValues().stream().map(x->x.toString()).collect(Collectors.toList());					
				}
			};
		}
	}
	public static class ObjectColumnFilterNumberRange implements IFilterProto {

		protected AbstractAnalizer analizer;
		private IColumn column;
		private boolean mode;

		public ObjectColumnFilterNumberRange(IColumn c,boolean mode) {
			this.column = c;
			this.mode=mode;
		}

		@Override
		public String name() {
			if (mode) {
				return column.caption() + " is greater then";
			}
			else return column.caption() + " is less then";

		}

		@Override
		public Parameter[] parameters() {
			return new Parameter[0];
		}

		@Override
		public String id() {
			return name();
		}

		@Override
		public Predicate<IItem> apply(IDataSet original, Map<String, Object> parameters) {
			Object vs = parameters.values().iterator().next();
			String val = vs == null ? "" : vs.toString();
			boolean isOk=false;
			try {
				Double.parseDouble(val);
				isOk=true;
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			boolean islc = val.equals("None");
			
			
			double vlNum=isOk?Double.parseDouble(val):0;
			
			return (Predicate<IItem>) v -> {
				GenericItem va = (GenericItem) v;
				String valueAsString = column.getValueAsString(va.base());
				
				try {
					if (mode) {
						return vlNum<=Double.parseDouble(valueAsString);
					}
					else {
						double parseDouble = Double.parseDouble(valueAsString);
						return vlNum>parseDouble;
					}
				}catch (NumberFormatException e) {
					return false;
				}
			};
		}

		@Override
		public Supplier<Collection<String>> values() {
			return new Supplier<Collection<String>>() {

				@Override
				public Collection<String> get() {
					return column.uniqueValues().stream().map(x->x.toString()).collect(Collectors.toList());					
				}
			};
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<IAnalizerProto> getAnalizers(IDataSet ds) {
		Collection<FilterFactory> values = factories.values();
		ArrayList<IAnalizerProto> result = new ArrayList<IAnalizerProto>();
		values.forEach(v -> {
			if (v.isAnalizer()) {
				if (v.canApply(ds)) {
					result.add(v);
				}
			}
		});
		if (ds instanceof GenericDataSet) {
			DataSetSpec spec = ((GenericDataSet) ds).getSpec();
			processGeneric((ArrayList) result, spec, true);
		}
		Collections.sort(result);
		return result;
	}

	protected void processGeneric(ArrayList<IProto> result, DataSetSpec spec, boolean analizer) {
		for (ColumnInfo i : spec.layout.infos()) {
			if (i.preferredType() == TextColumnType.class) {
				IColumn column = i.getColumn();
				Function<IItem, IItem> converter = textAdapter(column);
				Class[] cc = new Class[] { WordCountAnalizer.class, TextLengthAnalizer.class,
						SentenceCountAnalizer.class };
				for (Class c : cc) {
					AbstractAnalizer wordCountAnalizer;
					try {
						wordCountAnalizer = (AbstractAnalizer) c.newInstance();
						wordCountAnalizer.setConverter(converter);
						if (analizer) {
							result.add(new BasicAnalizerProto(
									((IColumnDependentAnalizer) wordCountAnalizer).getName(column),
									(IAnalizer) wordCountAnalizer,1));

						} else {
							result.add(new BasicColumnFilterProto(true, column, (AbstractAnalizer) wordCountAnalizer));
							result.add(new BasicColumnFilterProto(false, column, (AbstractAnalizer) wordCountAnalizer));
							result.add(new ObjectColumnFilterProto(column,i.preferredType()));
						}
					} catch (InstantiationException | IllegalAccessException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			if (i.preferredType() == ClassColumnType.class) {
				IColumn column = i.getColumn();
				Function<IItem, IItem> converter = clazzAdapter(column);
				Class[] cc = new Class[] { HasClassesAnalizer.class };
				for (Class c : cc) {
					AbstractAnalizer wordCountAnalizer;
					try {
						wordCountAnalizer = (AbstractAnalizer) c.newInstance();
						wordCountAnalizer.setConverter(converter);
						if (analizer) {
							result.add(new BasicAnalizerProto(
									((IColumnDependentAnalizer) wordCountAnalizer).getName(column),
									(IAnalizer) wordCountAnalizer,5000-column.uniqueValues().size()));

						} else {
							result.add(new ObjectColumnFilterProto(column, i.preferredType()));
						}
					} catch (InstantiationException | IllegalAccessException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			if (i.preferredType() == IDColumnType.class) {
				IColumn column = i.getColumn();
				Function<IItem, IItem> converter = clazzAdapter(column);
				if (!analizer) {
					result.add(new ObjectColumnFilterProto(column,i.preferredType()));
				}
			}
			if (i.preferredType() == NumberColumn.class) {
				IColumn column = i.getColumn();
				Function<IItem, IItem> converter = clazzAdapter(column);

				NumberColumnAnalizer wordCountAnalizer = new NumberColumnAnalizer(column);
				if (analizer) {
					result.add(new BasicAnalizerProto(((IColumnDependentAnalizer) wordCountAnalizer).getName(column),
							(IAnalizer) wordCountAnalizer,2));
				} else {
					result.add(new ObjectColumnFilterNumber(column));
					result.add(new ObjectColumnFilterNumberRange(column,true));
					result.add(new ObjectColumnFilterNumberRange(column,false));
				}
			}
		}

	}

	protected Function<IItem, IItem> textAdapter(IColumn column) {
		Function<IItem, IItem> converter = x -> {
			return new ITextItem() {

				@Override
				public String id() {
					return x.id();
				}

				@Override
				public IDataSet getDataSet() {
					return x.getDataSet();
				}

				@Override
				public String getText() {
					GenericItem ti = (GenericItem) x;
					return column.getValueAsString((ITabularItem) ti.base());
				}
			};
		};
		return converter;
	}

	protected Function<IItem, IItem> clazzAdapter(IColumn column) {
		Function<IItem, IItem> converter = x -> {
			return new IMulticlassClassificationItem() {

				@Override
				public String id() {
					return x.id();
				}

				@Override
				public IDataSet getDataSet() {
					return x.getDataSet();
				}

				@Override
				public ArrayList<String> classes() {
					GenericItem ti = (GenericItem) x;
					String vl = column.getValueAsString((ITabularItem) ti.base());
					return MultiClassClassificationItem.splitByClass(vl);
				}
			};
		};
		return converter;
	}
}