package com.onpositive.datasets.visualisation.ui.views;

import java.util.List;

import com.onpositive.musket.data.actions.BasicDataSetActions;
import com.onpositive.musket.data.actions.BasicDataSetActions.ConversionAction;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Image;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;

@Display("dlf/actionList.dlf")
@Image("generic_task")
class ActionSelection{

	@Validator(validatorClass = ActionSelection.TargetFieldValidator.class)
	@Caption("Target file name")
	private String target;
	
	private List<ConversionAction> items;
	
	@Required("Please select converter")
	@Caption("Converter")
	
	private ConversionAction selection;

	public ActionSelection(List<ConversionAction> conversions) {
		this.items=conversions;
	}
	
	public String targetFile() {
		return target;
	}
	
	public ConversionAction getSelectedAction() {
		return selection;
	}
	
	public boolean targetEnabled() {
		return (selection == null) || !isInstanceSementation();
	}

	private boolean isInstanceSementation() {
		return selection instanceof BasicDataSetActions.ConvertToInstanceSegmentation;
	}
	
	public static class TargetFieldValidator implements IValidator<String>{

		private static final long serialVersionUID = 6942046158838193092L;

		@Override
		public CodeAndMessage isValid(IValidationContext arg0, String arg1) {
			if(arg1 != null && !arg1.isEmpty()) {
				return CodeAndMessage.OK_MESSAGE;				
			}
			Object parentValue = arg0.getParent().getValue();
			if(!(parentValue instanceof ActionSelection)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			ActionSelection as = (ActionSelection)parentValue;
			if(as.isInstanceSementation()) {
				return CodeAndMessage.OK_MESSAGE;
			}
			return new CodeAndMessage(CodeAndMessage.ERROR,"Target required");
		}
		
	}
	
	
}