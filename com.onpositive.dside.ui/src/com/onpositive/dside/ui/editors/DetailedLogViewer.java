package com.onpositive.dside.ui.editors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import com.opencsv.CSVReader;

public class DetailedLogViewer {

	static String[] headers = null;
	static ArrayList<Double[]> data = new ArrayList<>();

	public static DefaultXYDataset createDataset(String metric) {

		DefaultXYDataset dataset = new DefaultXYDataset();

		add(metric, dataset, "Train");
		add("val_" + metric, dataset, "Validation");

		return dataset;
	}

	private static void add(String metric, DefaultXYDataset dataset, String series1) {
		int index = -1;
		if (headers == null) {
			return;
		}
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].equals(metric)) {
				index = i;
			}
		}
		if (index != -1) {
			double[][] m = new double[2][data.size()];
			for (int i = 0; i < data.size(); i++) {
				Double[] doubles = data.get(i);
				m[0][i] = i;
				try {
				
				m[1][i] = doubles[index];
				}catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
			int window=100;
			double[]sumv=new double[m[1].length];
			for (int i=0;i<m[1].length;i++) {
				double sum=0;
				for (int j=Math.max(0, i-window);j<Math.min(m[1].length, i+window);j++) {
					sum=sum+m[1][j];
				}
				sumv[i]=sum/(2*window);
			}
			for (int i=0;i<m[1].length;i++) {
				m[1][i]=sumv[i];
			}
			dataset.addSeries(series1, m);
		}
	}
	static class XYLineChart_AWT extends ApplicationFrame {

		   public XYLineChart_AWT( String applicationTitle, String chartTitle,XYDataset ds) {
		      super(applicationTitle);
		      JFreeChart xylineChart = ChartFactory.createXYLineChart(
		         chartTitle ,
		         "Category" ,
		         "Score" ,
		         ds,
		         PlotOrientation.VERTICAL ,
		         true , true , false);
		         
		      ChartPanel chartPanel = new ChartPanel( xylineChart );
		      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		      
		      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
		      
		      //plot.setRenderer( renderer ); 
		      setContentPane( chartPanel ); 
		   }
		   
		   
		}

	public static void main(String[] args) {
		
		try {
			new CSVReader(new FileReader("D:\\jigsaw\\experiments\\e9\\metrics\\metrics-0.0.csvall2.csv")).forEach(l -> {
				if (headers == null) {
					headers = l;
				} else {
					Double[] res = new Double[l.length];
					for (int i = 0; i < res.length; i++) {
						try {
							String string = l[i];
							if (string.isEmpty()) {
								return;
							}
							res[i] = Double.parseDouble(string);
						} catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}
					}
					data.add(res);
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DefaultXYDataset createDataset = createDataset("loss");
		XYLineChart_AWT vvv=new XYLineChart_AWT("AA", "AA", createDataset);
		vvv.show();
		System.out.println(createDataset);
		
		//System.out.println(data);

	}

}
