/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package com.onpositive.dside.ui.editors.outline;

import org.aml.typesystem.AbstractType;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import com.onpositive.commons.SWTImageManager;

import de.jcup.yamleditor.YamlEditorActivator;
import de.jcup.yamleditor.YamlEditorColorConstants;
import de.jcup.yamleditor.ColorManager;
import de.jcup.yamleditor.EclipseUtil;
import de.jcup.yamleditor.outline.Item;
import de.jcup.yamleditor.outline.ItemType;

public class OutlineLabelProvider extends BaseLabelProvider implements IStyledLabelProvider, IColorProvider, IToolTipProvider {

	private static final String ICON_NODE = "public_co.png";
	private static final String ICON_LEAF = "field_public_obj.png";
	private static final String ICON_ERROR ="error_tsk.png";
	private static final String ICON_INFO ="info_tsk.png";

	private Styler outlineItemTypeStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(YamlEditorColorConstants.OUTLINE_ITEM__TYPE);
		}
	};

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element == null){
			return null;
		}
		
		if (element instanceof OutlineNode) {
			
			OutlineNode item = (OutlineNode) element;
			if (item instanceof PropertyNode) {
				PropertyNode node=(PropertyNode) item;
				if (node.property.id().equals("stages")) {
					return SWTImageManager.getImage("stages");
				}
				if (node.property.id().equals("callbacks")||node.property.id().equals("extra_callbacks")) {
					return SWTImageManager.getImage("stack");
				}
				if (node.property.id().equals("architecture")) {
					return SWTImageManager.getImage("architecture");
				}
				if (node.property.id().equals("datasets")) {
					return SWTImageManager.getImage("dataset");
				}
				if (node.property.id().equals("declarations")) {
					return SWTImageManager.getImage("validate");
				}
				if (node.property.id().equals("preprocessing")) {
					return SWTImageManager.getImage("preprocessor");
				}
			}
			AbstractType type = item.element.getType();
			//System.out.println(type.name());
			return imageFromType(type);
			
		}
		return null;
	}

	public static Image imageFromType(AbstractType type) {
		if (type.name().contains("cache")) {
			return SWTImageManager.getImage("cache");
		}
		if (type.isSubTypeOf("Stage")||type.name().equals("Stage")) {
			return SWTImageManager.getImage("stage");
		}
		if (type.isSubTypeOf("Callback")||type.name().equals("Callback")) {
			return SWTImageManager.getImage("stack");
		}
		if (type.isSubTypeOf("Layer")||type.name().equals("Layer")) {
			return SWTImageManager.getImage("layer_unk");
		}
		if (type.isSubTypeOf("PreprocessorDeclaration")||type.name().equals("PreprocessorDeclaration")) {
			return SWTImageManager.getImage("preprocessor");
		}
		if (type.isSubTypeOf("Preprocessor")||type.name().equals("Preprocessor")) {
			return SWTImageManager.getImage("preprocessor");
		}
		if (type.isSubTypeOf("NetworkBlockDeclaration")||type.name().equals("NetworkBlockDeclaration")) {
			return SWTImageManager.getImage("architecture");
		}
		if (type.isSubTypeOf("DataSetDeclaration")||type.name().equals("DataSetDeclaration")) {
			return SWTImageManager.getImage("dataset");
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		StyledString styled = new StyledString();
		
		if (element == null) {
			styled.append("null");
		} else if (element instanceof Item) {
			Item item = (Item) element;

			ItemType itemType = item.getItemType();
			if (itemType==ItemType.SPECIAL){
				
				StyledString typeString = new StyledString("special:", outlineItemTypeStyler);
				styled.append(typeString);
			}else if (itemType==ItemType.META_DEBUG){
				StyledString typeString = new StyledString(item.getOffset()+": ", outlineItemTypeStyler);
				styled.append(typeString);
			}
			String name = item.getName();
			if (name != null) {
				styled.append(name );//+" { ... }");
			}
		} else {
			return styled.append(element.toString());
		}

		return styled;
	}

	public ColorManager getColorManager() {
		YamlEditorActivator editorActivator = YamlEditorActivator.getDefault();
		if (editorActivator == null) {
			return ColorManager.getStandalone();
		}
		return editorActivator.getColorManager();
	}

	private Image getOutlineImage(String name) {
		return EclipseUtil.getImage("/icons/outline/" + name, YamlEditorActivator.PLUGIN_ID);
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof Item){
			Item item = (Item) element;
			if (ItemType.META_ERROR.equals(item.getItemType())){
				return item.getName()+"\nThe outline will only show valid parts of document!";
			}
		}
		return null;
	}

}
