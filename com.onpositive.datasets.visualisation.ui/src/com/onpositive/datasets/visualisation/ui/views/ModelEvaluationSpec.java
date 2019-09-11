package com.onpositive.datasets.visualisation.ui.views;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;


@Display("dlf/eval.dlf")
public class ModelEvaluationSpec {

//	@Caption("Please select seed selection strategy")
//	@RealmProvider(EnumRealmProvider.class)
//	@Required
//	FoldSelectionStrategy seeds=FoldSelectionStrategy.ALL;
//	
//	@Caption("Please select fold selection strategy")
//	@RealmProvider(EnumRealmProvider.class)
//	@Required
//	FoldSelectionStrategy folds=FoldSelectionStrategy.ALL;
//	
//	@Caption("Please select stage selection strategy")
//	@RealmProvider(EnumRealmProvider.class)
//	@Required
//	StageSelectionStrategy stages=StageSelectionStrategy.LAST_STAGE;
	
	boolean hasSeeds;	
	boolean hasStages;
	boolean hasFolds;
	
	public ModelEvaluationSpec(boolean hasSeeds, boolean hasStages, boolean hasFolds) {
		super();
		this.hasSeeds = hasSeeds;
		this.hasStages = hasStages;
		this.hasFolds = hasFolds;
	}
	
	ArrayList<Integer>seed_numbers=new ArrayList<>();
	public boolean isHasSeeds() {
		return hasSeeds;
	}
	public void setHasSeeds(boolean hasSeeds) {
		this.hasSeeds = hasSeeds;
	}
	public boolean isHasStages() {
		return hasStages;
	}
	public void setHasStages(boolean hasStages) {
		this.hasStages = hasStages;
	}
	public boolean isHasFolds() {
		return hasFolds;
	}
	public void setHasFolds(boolean hasFolds) {
		this.hasFolds = hasFolds;
	}
	public ArrayList<Integer> getSeed_numbers() {
		return seed_numbers;
	}
	public void setSeed_numbers(ArrayList<Integer> seed_numbers) {
		this.seed_numbers = seed_numbers;
	}
	public ArrayList<Integer> getFolds_numbers() {
		return folds_numbers;
	}
	public void setFolds_numbers(ArrayList<Integer> folds_numbers) {
		this.folds_numbers = folds_numbers;
	}
	public ArrayList<Integer> getStages_numbers() {
		return stages_numbers;
	}
	public void setStages_numbers(ArrayList<Integer> stages_numbers) {
		this.stages_numbers = stages_numbers;
	}

	ArrayList<Integer>folds_numbers=new ArrayList<>();
	ArrayList<Integer>stages_numbers=new ArrayList<>();
	
//	public FoldSelectionStrategy getSeeds() {
//		return seeds;
//	}
//	public void setSeeds(FoldSelectionStrategy seeds) {
//		this.seeds = seeds;
//	}
//	public FoldSelectionStrategy getFolds() {
//		return folds;
//	}
//	public void setFolds(FoldSelectionStrategy folds) {
//		this.folds = folds;
//	}
//	public StageSelectionStrategy getStages() {
//		return stages;
//	}
//	public void setStages(StageSelectionStrategy stages) {
//		this.stages = stages;
//	}
//
//	
//	public boolean getShowSeeds() {
//		return seeds==FoldSelectionStrategy.MANUAL;
//	}
//	public boolean getShowFolds() {
//		return folds==FoldSelectionStrategy.MANUAL;
//	}
//	public boolean getShowStages() {
//		return stages==StageSelectionStrategy.MANUAL;
//	}
}
