package com.onpositive.musket.data.text;

import java.awt.Color;
import java.util.HashMap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.SWTImageDescriptor;
import com.onpositive.musket.data.text.ClassVisibilityOptions.ClassVisibilitySetting;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.richtext.StyledString.Style;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;

public class ClassColorLabelProvider implements IRichLabelProvider, IImageDescriptorProvider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getDescription(Object arg0) {
		return null;
	}

	@Override
	public String getText(IHasMeta arg0, Object arg1, Object arg2) {
		return "";
	}

	@Override
	public StyledString getRichTextLabel(Object arg0) {
		return new StyledString();
	}
	protected static HashMap<Color, Image>images=new HashMap<>();

	@Override
	public ImageDescriptor getImageDescriptor(Object arg0) {
		if (arg0 instanceof ClassVisibilitySetting) {
			ClassVisibilitySetting setting=(ClassVisibilitySetting) arg0;
			ImageDescriptor d=new SWTImageDescriptor(new org.eclipse.jface.resource.ImageDescriptor() {
				@Override
				public Image createImage() {
					if (images.containsKey(setting.rgb)) {
						return images.get(setting.rgb);
					}
					
					Image im=new Image(Display.getCurrent(), new Rectangle(0, 0, 16, 16));
					GC gc=new GC(im);
					org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(Display.getCurrent(), setting.rgb.getRed(), setting.rgb.getGreen(), setting.rgb.getBlue());
					gc.setBackground(color);
					gc.fillRectangle(0, 0, 16, 16);
					gc.dispose();
					color.dispose();
					images.put(setting.rgb, im);
					return im;
				}
			});
			return d;
		}
		return null;
	}

}
