package com.onpositive.datasets.visualisation.ui.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.onpositive.musket.data.core.ChartData;
import com.onpositive.musket.data.core.ChartData.BasicChartData;
import com.onpositive.musket.data.core.IAnalizeResults;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.VisualizationSpec;

public class ChartUtils {

	static Dataset createDataset(IAnalizeResults r, VisualizationSpec loadAs) {
		if (loadAs != null) {
			if (loadAs instanceof Map) {

				Map mm = (Map<String, Object>) loadAs;
				Object object = mm.get("values");
				Object total = mm.get("total");
				if (mm.get("type").equals("bar")) {
					ArrayList<Object> vls = (ArrayList<Object>) object;
					Map<String, Number> blds = (Map<String, Number>) vls.get(0);
					DefaultCategoryDataset cs = new DefaultCategoryDataset();
					for (String s : blds.keySet()) {
						Number number = blds.get(s);
						cs.addValue(number, s, "");
					}
					return cs;

				}
				if (object instanceof ArrayList) {
					ArrayList arrayList = (ArrayList) object;
					if (arrayList.get(0) instanceof Number) {
						DefaultCategoryDataset dataset = new DefaultCategoryDataset();
						int size = arrayList.size();
						for (int i = 0; i < size; i++) {
							dataset.addValue((Number) arrayList.get(i), "" + i, "");
						}
						return dataset;
					}
				}
				DefaultXYDataset d = createXY(object, total);

				return d;
			}
			System.out.println(loadAs);
		}
		if (loadAs.type == VisualizationSpec.ChartType.BAR) {

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			if (loadAs.chart != null) {
				ChartData.BasicChartData bd = (BasicChartData) loadAs.chart;
				bd.values.keySet().forEach(k -> {
					dataset.addValue(bd.values.get(k), k, "");
				});
			} else {
				int size = r.size();
				ArrayList<String> names = new ArrayList<>();
				ArrayList<Double> counts = new ArrayList<>();
				double sum = 0;
				for (int i = 0; i < size; i++) {
					IDataSet iDataSet = r.get(i);
					int len = iDataSet.length();
					String name = iDataSet.name();
					names.add(name);
					counts.add((double) len);
					sum = sum + len;

				}
				for (int i = 0; i < size; i++) {
					String string = names.get(i) + "(" + counts.get(i).intValue() + " "
							+ NumberFormat.getPercentInstance().format(counts.get(i) / sum) + ")";
					dataset.addValue(counts.get(i), string, "");
				}
			}
			return dataset;
		}
		DefaultPieDataset dataset = new DefaultPieDataset();
		int size = r.size();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Double> counts = new ArrayList<>();
		double sum = 0;
		for (int i = 0; i < size; i++) {
			IDataSet iDataSet = r.get(i);
			int len = iDataSet.length();
			String name = iDataSet.name();
			names.add(name);
			counts.add((double) len);
			sum = sum + len;

		}
		for (int i = 0; i < size; i++) {
			dataset.setValue(names.get(i) + "(" + counts.get(i).intValue() + " "
					+ NumberFormat.getPercentInstance().format(counts.get(i) / sum) + ")", counts.get(i));
		}
		return dataset;
	}

	public static DefaultXYDataset createXY(Object object, Object total) {
		DefaultXYDataset d = new DefaultXYDataset();
		String[] labels = new String[] { "All", "Positive", "Negative" };
		if (object instanceof ArrayList) {
			ArrayList x = (ArrayList) object;
			int num = 0;
			if (x.get(0) instanceof Double) {

			}
			for (Object z : x) {

				Map m = (Map) z;

				double[][] data = new double[2][m.size()];
				int i = 0;
				int sum = 0;
				for (Object o : m.keySet()) {
					data[0][i] = ((Integer) o).doubleValue();
					double doubleValue = ((Number) m.get(o)).doubleValue();
					data[1][i] = doubleValue;
					i = i + 1;
					sum += doubleValue;
				}
				if (total != null) {
					i = 0;
					for (Object o : m.keySet()) {
						data[1][i] = 1 - data[1][i] / ((Number) total).doubleValue();
						i = i + 1;
					}
				} else {
					i = 0;
					double sm = 0;
					for (Object o : m.keySet()) {
						sm = sm + data[1][i];
						data[1][i] = sm / sum;
						i = i + 1;
					}
				}
				d.addSeries(labels[num], data);
				num = num + 1;
			}
		}
		return d;
	}

	private static HistogramDataset createHistDataset(IAnalizeResults r) {
		HistogramDataset dataset = new HistogramDataset();
		int size = r.size();
		ArrayList<String> names = new ArrayList<>();
		double[] counts = new double[size];
		double sum = 0;
		for (int i = 0; i < size; i++) {
			IDataSet iDataSet = r.get(i);
			int len = iDataSet.length();
			String name = iDataSet.name();

			counts[i] = len;
			sum = sum + len;

		}
		dataset.addSeries("", counts, counts.length);
		return dataset;
	}

	public static JFreeChart createChart(Dataset dataset, VisualizationSpec loadAs) {
		if (dataset instanceof DefaultCategoryDataset) {
			String y_axis = loadAs.yName;
			String x_axis = loadAs.xName;
			if (loadAs instanceof Map) {
				Map mp = (Map) loadAs;
				if (mp.containsKey("x_axis")) {
					x_axis = mp.get("x_axis").toString();
				}
				if (mp.containsKey("y_axis")) {
					y_axis = mp.get("y_axis").toString();
				}
			}
			JFreeChart chart = ChartFactory.createBarChart("", // Chart Title
					x_axis, // Category axis
					y_axis, // Value axis
					(CategoryDataset) dataset, PlotOrientation.VERTICAL, true, true, false);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			BarRenderer renderer = (BarRenderer) plot.getRenderer(0);
			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{1}",
					NumberFormat.getInstance());
//	        renderer.setItemLabelGenerator(generator);
//	        renderer.setItemLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//	        renderer.setItemLabelsVisible(true);
//	        renderer.setPositiveItemLabelPosition(new ItemLabelPosition(
//	                ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 
//	                - Math.PI / 2));
			return chart;
		}
		if (dataset instanceof PieDataset) {
			JFreeChart chart = ChartFactory.createPieChart("", // chart title
					(PieDataset) dataset, // data
					true, // include legend
					true, false);

			return chart;
		}
		if (dataset instanceof HistogramDataset) {
			JFreeChart chart = ChartFactory.createHistogram("", "", "", (HistogramDataset) dataset,
					PlotOrientation.VERTICAL, false, false, false);

			return chart;
		}
		if (dataset instanceof XYDataset) {
			String y_axis = "Fraction of samples";
			String x_axis = "Length";
			if (loadAs instanceof Map) {
				Map mp = (Map) loadAs;
				if (mp.containsKey("x_axis")) {
					x_axis = mp.get("x_axis").toString();
				}
				if (mp.containsKey("y_axis")) {
					y_axis = mp.get("y_axis").toString();
				}
			}
			JFreeChart chart = ChartFactory.createXYLineChart("", x_axis, y_axis, (XYDataset) dataset);

			return chart;
		}

		throw new IllegalStateException("Unknown dataset type");
	}
}
