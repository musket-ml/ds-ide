package com.onpositive.musket.data.core.filters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IAnalizer;
import com.onpositive.musket.data.core.IAnalizerProto;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IFilterProto;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.Parameter;
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

		private Object createItem(HashMap<String, Object> analzierArgs) {
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
	}

	public static void register(Class<?> clazz) {
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
		return result;
	}

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
		return result;
	}
}