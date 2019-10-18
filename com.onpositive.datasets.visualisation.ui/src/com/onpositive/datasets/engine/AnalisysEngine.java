package com.onpositive.datasets.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.onpositive.datasets.visualisation.ui.views.DataSetAnalisysRequest;
import com.onpositive.datasets.visualisation.ui.views.DataSetFilter;
import com.onpositive.datasets.visualisation.ui.views.IAnalisysEngine;
import com.onpositive.datasets.visualisation.ui.views.InstrospectedFeature;
import com.onpositive.datasets.visualisation.ui.views.IntrospectedParameter;
import com.onpositive.datasets.visualisation.ui.views.PossibleAnalisisSpec;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IAnalizerProto;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IFilterProto;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IProto;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;

public class AnalisysEngine implements IAnalisysEngine {

	protected IDataSet dataset;

	public AnalisysEngine(IDataSet dataset) {
		super();
		this.dataset = dataset;
	}

	@Override
	public void perform(DataSetAnalisysRequest data, Consumer<IAnalizeResults> func, Consumer<Throwable> error) {
		String analizer = data.getAnalizer();
		Job j = new Job("Performing analisys") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ArrayList<DataSetFilter> filters = data.getFilters();

				Predicate<IItem> finalPredicate = null;
				for (DataSetFilter fa : filters) {
					for (IFilterProto p : dataset.filters()) {
						if (p.name().equals(fa.getFilterKind())) {
							String filterArgs = fa.getFilterArgs();
							HashMap<String, Object> realArgs = new HashMap<String, Object>();
							realArgs.put("expression", filterArgs);
							Predicate<IItem> apply = p.apply(dataset, realArgs);
							if (fa.getMode().equals("inverse")) {
								apply = apply.negate();
							}
							if (finalPredicate == null) {
								finalPredicate = apply;
							} else {
								finalPredicate = finalPredicate.and(apply);
							}
						}
					}
				}
				HashMap<String, Object> visualizerArgs = data.getVisualizerArgs();

				List<? extends IItem> collect = finalPredicate == null ? (List<? extends IItem>) dataset.items()
						: dataset.items().parallelStream().filter(finalPredicate).collect(Collectors.toList());
				for (IAnalizerProto p : dataset.analizers()) {
					if (p.name().equals(analizer)) {
						IDataSet subDataSet = dataset.subDataSet(dataset.name(), (List<IItem>) collect);
						if (visualizerArgs != null && !visualizerArgs.isEmpty()) {
							dataset.setSettings(null, visualizerArgs);
						}
						IAnalizeResults perform = p.perform(data.getAnalzierArgs(), subDataSet);
						Display.getDefault().asyncExec(new Runnable() {
							
							@Override
							public void run() {
								func.accept(perform);		
							}
						});
						
					}
				}
				return Status.OK_STATUS;
			}
		};
		j.setUser(true);
		j.schedule();		

	}

	@Override
	public void terminate() {

	}

	@Override
	public PossibleAnalisisSpec getSpec() {
		PossibleAnalisisSpec result = new PossibleAnalisisSpec();
		for (IFilterProto p : dataset.filters()) {
			result.getDatasetFilters().add(protoToFeature(p));
		}
		for (IAnalizerProto p : dataset.analizers()) {
			result.getAnalizers().add(protoToFeature(p));
		}
		for (IAnalizerProto p : dataset.analizers()) {
			result.getData_analizers().add(protoToFeature(p));
		}

		InstrospectedFeature instrospectedFeature = new InstrospectedFeature();
		instrospectedFeature.setName("default");
		dataset.getSettings();
		IVisualizerProto visualizer = dataset.getVisualizer();
		if (visualizer != null) {
			result.getVisualizers().add(protoToFeature(visualizer));
		} else {
			result.getVisualizers().add(instrospectedFeature);
		}
		// result.getDatasetStages()
		return result;
	}

	private InstrospectedFeature protoToFeature(IProto p) {
		InstrospectedFeature instrospectedFeature = new InstrospectedFeature();
		instrospectedFeature.setName(p.name());
		instrospectedFeature.setKind(p.id());
		instrospectedFeature.setValues(p.values());
		for (Parameter pa : p.parameters()) {
			IntrospectedParameter introspectedParameter = new IntrospectedParameter();
			introspectedParameter.setName(pa.name);
			instrospectedFeature.setKind(p.id());
			String simpleName = pa.type.getSimpleName();
			if (simpleName.equals("boolean")) {
				simpleName = "bool";
			}
			if (simpleName.equals("Color")) {
				simpleName = "color";
			}
			introspectedParameter.setType(simpleName);
			introspectedParameter.setDefaultValue(pa.defaultValue);
			instrospectedFeature.getParameters().add(introspectedParameter);
		}

		return instrospectedFeature;
	}

}
