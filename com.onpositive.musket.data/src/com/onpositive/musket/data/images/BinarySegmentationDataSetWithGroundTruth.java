package com.onpositive.musket.data.images;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.onpositive.musket.data.columntypes.DataSetSpec;
import com.onpositive.musket.data.core.IDataSet;
import com.onpositive.musket.data.core.IDataSetWithGroundTruth;
import com.onpositive.musket.data.core.IItem;
import com.onpositive.musket.data.core.IVisualizerProto;
import com.onpositive.musket.data.core.Parameter;
import com.onpositive.musket.data.table.IColumn;
import com.onpositive.musket.data.table.ITabularDataSet;
import com.onpositive.musket.data.table.ITabularItem;

public class BinarySegmentationDataSetWithGroundTruth extends BinarySegmentationDataSet implements IDataSetWithGroundTruth{

	private ITabularDataSet predictions;
	
	public static final String PREDICTION_ALPHA = "Prediction alpha";
	public static final String PREDICTION_COLOR = "Prediction color";
	public static final String PREDICTION_COLOR_DEFAULT = "0,255,0";


	public BinarySegmentationDataSetWithGroundTruth(DataSetSpec base, IColumn image, IColumn rle,
			 int width, int height,ITabularDataSet prediction) {
		super(base, image, rle,  width, height);
		{
			parameters.put(PREDICTION_ALPHA, MASK_ALPHA_DEFAULT);
			parameters.put(PREDICTION_COLOR, PREDICTION_COLOR_DEFAULT);
		}
		this.predictions=prediction;
	}
	
	@Override
	public Collection<BinarySegmentationItem> items() {
		Map<String, ITabularItem> itemMap = this.predictions.getItemMap();
		if (items==null) {
			items=new ArrayList<>();
		    tabularBase.items().forEach(v->{
		    	items.add(new BinarySegmentationItemWithGroundTruth(this,v,itemMap.get(v.id())));
		    });
		}
		return items;
	}

	@Override
	public IItem getPrediction(int num) {
		BinarySegmentationItemWithGroundTruth binarySegmentationItem = (BinarySegmentationItemWithGroundTruth) this.items.get(num);
		return binarySegmentationItem.getPrediction();
	}

	@Override
	public IVisualizerProto getVisualizer() {
		return new IVisualizerProto() {
			
			@Override
			public Parameter[] parameters() {
				Parameter alpha=new Parameter();
				alpha.defaultValue=parameters.get(MASK_ALPHA).toString();
				alpha.type=int.class;
				alpha.name=MASK_ALPHA;
				
				Parameter alpha1=new Parameter();
				alpha1.defaultValue=parameters.get(PREDICTION_ALPHA).toString();
				alpha1.type=int.class;
				alpha1.name=PREDICTION_ALPHA;
				
				Parameter nk=new Parameter();
				nk.defaultValue=parameters.get(MASK_COLOR).toString();
				nk.type=Color.class;
				nk.name=MASK_COLOR;
				
				Parameter nk1=new Parameter();
				nk1.defaultValue=parameters.get(PREDICTION_COLOR).toString();
				nk1.type=Color.class;
				nk1.name=PREDICTION_COLOR;
				return new Parameter[] {alpha,alpha1,nk,nk1};
			}
			
			@Override
			public String name() {
				return "Image visualizer";
			}
			
			@Override
			public String id() {
				return "Image visualizer";				
			}

			@Override
			public Supplier<Collection<String>> values(IDataSet ds) {
				return null;
			}
		};
	}
	
	@Override
	protected String getKind() {
		return "Binary Segmentation with predictions";
	}
}
