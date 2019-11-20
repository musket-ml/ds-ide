package com.onpositive.datasets.visualisation.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.FormDialog;
import com.onpositive.datasets.visualisation.ui.views.AnalistsEditor.AnalizerOrVisualizerUI;
import com.onpositive.musket.data.columntypes.ColumnLayout.ColumnInfo;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.generic.Columns;
import com.onpositive.musket.data.generic.GenericDataSet;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.text.ClassVisibilityOptions;
import com.onpositive.musket.data.text.IHasClassGroups;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IHasPropertyProvider;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.java.MapPropertyProvider;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.CheckboxElement;
import com.onpositive.semantic.model.ui.property.editors.ColorSelectorWrapper;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;
import com.onpositive.semantic.model.ui.property.editors.SectionEditor;
import com.onpositive.semantic.model.ui.property.editors.SeparatorElement;
import com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.roles.IWidgetProvider;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.semantic.ui.core.Alignment;
import com.onpositive.semantic.ui.core.Rectangle;

public class DynamicUI {

	protected LinkedHashMap<String, Object> args = new MapWithProvider();
	protected InstrospectedFeature feature;
	
	public DynamicUI() {
		// TODO Auto-generated constructor stub
	}

	public class MapWithProvider extends LinkedHashMap<String, Object> implements IHasPropertyProvider {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public IPropertyProvider getPropertyProvider() {
			return new MapPropertyProvider() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public IProperty getProperty(Object obj, String name) {

					if (name != null) {
						final String s = name;
						MapProperty mapProperty = new MapProperty(s) {
							@Override
							protected void doSet(Object target, Object object)
									throws IllegalAccessException {
								IntrospectedParameter parameter = feature.getParameter(s);
								if (parameter!=null) {
									String type2 = parameter.getType();
								}
								Map targetMap=(Map) target;
								targetMap.put(id, object);
							}
						};
						BaseMeta m = (BaseMeta) mapProperty.getMeta();
						IntrospectedParameter parameter = feature.getParameter(s);
						if (parameter != null) {
							m.putMeta(DefaultMetaKeys.CAPTION_KEY, HumanCaption.getHumanCaption(parameter.getName()));
							m.putMeta(DefaultMetaKeys.DEFAULT_VALUE,
									HumanCaption.getHumanCaption(parameter.getDefaultValue()));
							m.putMeta(DefaultMetaKeys.REQUIRED_KEY, true);
							String type = parameter.getType();
							if (type != null) {
								if (type.equals("int")) {
									m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, int.class);
								}
								if (type.equals("float")) {
									m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, double.class);
								}
								if (type.equals("bool")) {
									m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, boolean.class);
								}
								if (type.equals("str")) {
									m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, String.class);
								}
								if (type.equals("Color")) {
									m.putMeta(DefaultMetaKeys.SUBJECT_CLASS_KEY, String.class);
								}
							}
						}
						return mapProperty;
					}
					return null;
				}
				
				
			};
		}

	}

	public DynamicUI(InstrospectedFeature feature) {
		super();
		this.feature = feature;
	}

//	public Map<String, Object> open(Experiment ex) {
//		FormEditor editor = new FormEditor(this.args);
//		Container cm = new Container();
//		cm.getLayoutHints().setGrabVertical(true);
//		cm.setLayoutManager(new OneElementOnLineLayouter());
//		editor.setCaption(HumanCaption.getHumanCaption(feature.getName()));
//		ArrayList<IntrospectedParameter> parameters = getParameters();
//		Binding bnd = new Binding(args);
//		for (IntrospectedParameter p : parameters) {
//			createParameterUI(ex, cm, bnd, p);
//		}
//		editor.add(cm);
//		SeparatorElement element = new SeparatorElement();
//		element.setVertical(false);
//		editor.add(element);
//		OneLineTextElement<Integer> workers = new OneLineTextElement<>();
//		workers.setBinding(bnd.binding("workers"));
//		this.args.put("workers", 1);
//		workers.setCaption("Workers count");
//		editor.add(element);
//		CheckboxElement f = new CheckboxElement();
//		f.setCaption("Run in debug mode?");
//		f.setBinding(bnd.binding("debug"));
//		editor.add(workers);
//		editor.add(f);
//		bnd.refresh(true);
//		int open = new FormDialog(bnd, editor).open();
//		if (open == Dialog.OK) {
//			return new LinkedHashMap<>(args);
//		}
//		return null;
//	}

	protected ArrayList<IntrospectedParameter> getParameters() {
		return feature.getParameters();
	}
	public Container populateParameters(Map<String,Object>values,Object ex) {
		Container cm = new Container();
		cm.setMargin(new Rectangle(0, 2, 0, 0));
		cm.getLayoutHints().setGrabHorizontal(true);
		//cm.getLayoutHints().setGrabHorizontal(true);
		cm.setLayoutManager(new OneElementOnLineLayouter());
		Binding bnd = new Binding(values);
		ArrayList<IntrospectedParameter> parameters = getParameters();
		for (IntrospectedParameter p : parameters) {
			createParameterUI( cm, bnd, p);
		}		
		return cm;
	}

	private void createParameterUI(Container cm, Binding bnd, IntrospectedParameter p) {
		String type = p.getType();
		if (type==null) {
			type="any";
		}
//		if (type.equals("Model") || type.equals("ConnectedModel")) {
//			ModelEvaluationSpec ma = ex.createModelSpec();
//			args.put(p.getName(), ma);
//			IWidgetProvider widgetObject = WidgetRegistry.getInstance().getWidgetObject(ma, null, null);
//			IUIElement<?> createWidget = widgetObject.createWidget(bnd.binding(p.getName()));
//			SectionEditor ed = new SectionEditor();
//			ed.setCaption(HumanCaption.getHumanCaption(p.getName()));
//			ed.add((AbstractUIElement<?>) createWidget);
//			cm.add(ed);
//			return;
//		}
		if (p.getDefaultValue() != null) {
			Object defaultValue = p.getDefaultValue();
			if (type!=null) {
				try {
				if (type.equals("bool")) {
					defaultValue=Boolean.parseBoolean(defaultValue.toString());
				}
				if (type.equals("int")) {
					defaultValue=Integer.parseInt(defaultValue.toString());
				}
				if (type.equals("float")) {
					defaultValue=Double.parseDouble(defaultValue.toString());
				}
				if (type.equals("color")) {
					int indexOf = defaultValue.toString().indexOf("{");
					if (indexOf!=-1) {
						defaultValue=defaultValue.toString().substring(indexOf+1,defaultValue.toString().length()-1);
					}
					String[] splt = defaultValue.toString().split(",");
					defaultValue=new RGB(Integer.parseInt(splt[0].trim()),Integer.parseInt(splt[1].trim()),Integer.parseInt(splt[2].trim()));
				}
				}catch (Exception e) {
				}
			}
			args.put(p.getName(), defaultValue);
		}
		Binding binding = (Binding) bnd.getBinding(p.getName());
		binding.setRequired(true);
		if (type.equals("DataSet")) {
			args.put(p.getName(), "validation");
			ComboEnumeratedValueSelector<String> element = new ComboEnumeratedValueSelector<String>();
			//element.setRealm(new Realm<>(ex.getDataSets()));
			element.setBinding(binding);

			element.setCaption(HumanCaption.getHumanCaption(p.getName()));
			cm.add(element);
		} else if (type.equals("bool")) {
			CheckboxElement element = new CheckboxElement();
			element.setBinding(binding);
			element.setCaption(HumanCaption.getHumanCaption(p.getName()));
			cm.add(element);
		} 
		else if (type.equals("color")) {
			ColorSelectorWrapper element = new ColorSelectorWrapper();
			element.setBinding(binding);
			element.setCaption(HumanCaption.getHumanCaption(p.getName()));
			cm.add(element);
		}else if (type.equals("ClassVisibilityOptions")) {
			ButtonSelector bs=new ButtonSelector();
			bs.addListener(SWT.Selection, new SWTEventListener<Button>() {
				
				@Override
				public void handleEvent(AbstractUIElement<Button> arg0, Event arg1) {
					AnalizerOrVisualizerUI b=(AnalizerOrVisualizerUI) (DynamicUI.this);					
					Object value = binding.getValue();
					if (value==null) {
						value="";
					}
					ClassVisibilityOptions options=new ClassVisibilityOptions(value.toString(),(IHasClassGroups) b.getDataSet());
					boolean createObject = WidgetRegistry.createObject(options);
					
					if (createObject) {
						binding.setValue(options.toString());
						onUpdate();
					}
					
				}
			});
			bs.getLayoutHints().setGrabHorizontal(true);
			bs.getLayoutHints().setAlignmentHorizontal(Alignment.FILL);
			bs.setCaption("Visible Classes...");
			cm.add(bs);
		}
		else if (type.equals("columns")) {
			ButtonSelector bs=new ButtonSelector();
			bs.addListener(SWT.Selection, new SWTEventListener<Button>() {
				
				@Override
				public void handleEvent(AbstractUIElement<Button> arg0, Event arg1) {
					AnalizerOrVisualizerUI b=(AnalizerOrVisualizerUI) (DynamicUI.this);
					GenericDataSet dataSet = (GenericDataSet) b.getDataSet();
					List<ColumnInfo> columns = (List<ColumnInfo>) new ArrayList<>(dataSet.getSpec().layout.infos());
					Object value = binding.getValue();
					ListSelectionDialog ls=new ListSelectionDialog(Display.getCurrent().getActiveShell(), 
							columns.toArray(), new ArrayContentProvider(), 
							new LabelProvider() {
						@Override
						public String getText(Object element) {
							return element.toString();
						}
					}, "Please select visible columns");
					String ss=value==null?"":value.toString();
					if (!ss.isEmpty()) {
						String[] split = ss.split(",");
						HashSet<String> hashSet = new HashSet<>(Arrays.asList(split));
						ArrayList<Object>selected=new ArrayList<>();
						for (ColumnInfo i:columns) {
							if (hashSet.contains(i.getColumn().caption())) {
								selected.add(i);
							}
						}
						ls.setInitialSelections((Object[])selected.toArray());
					}
					else {
						ls.setInitialSelections((Object[])columns.toArray());
					}
					int open = ls.open();
					if (open==Dialog.OK) {
						Object[] result = ls.getResult();
						String collect = Arrays.asList(result).stream().map(x->{
							return ((ColumnInfo)x).getColumn().caption();
						}).collect(Collectors.joining(","));
						binding.setValue(collect);
						onUpdate();
					}
					
				}
			});
			bs.getLayoutHints().setGrabHorizontal(true);
			bs.getLayoutHints().setAlignmentHorizontal(Alignment.FILL);
			bs.setCaption("Visible Columns...");
			cm.add(bs);
		}
		else {
			OneLineTextElement<Object> element = new OneLineTextElement<Object>(binding);
			element.setCaption(HumanCaption.getHumanCaption(p.getName()));
			cm.add(element);
		}
		bnd.setSubjectClass(Integer.class);
	}

	public LinkedHashMap<String, Object> getArgs() {
		return args;
	}

	
	public void onUpdate() {
		
	}
}
