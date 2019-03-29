package com.onpositive.dside.dto;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.musket_core.Utils;
import com.onpositive.semantic.model.api.property.java.annotations.Image;

public class PythonError {

	@Image("icons/methodresult_obj.png")
	public static class StackElement {
		String file;
		int line = -1;
		String method;
		String code = "";

		@Override
		public String toString() {
			return this.file + ", line " + this.line + "  in  " + this.method;
		}
	}

	protected ArrayList<StackElement> elements = new ArrayList<>();

	public ArrayList<StackElement> getElements() {
		return elements;
	}

	public void setElements(ArrayList<StackElement> elements) {
		this.elements = elements;
	}

	public PythonError(List<String> errorLines) {
		for (String s : errorLines) {
			if (s.trim().startsWith("File")) {
				int fq = s.indexOf("\"");
				if (fq != -1) {
					int lq = s.indexOf("\"", fq + 1);
					String file = s.substring(fq + 1, lq);
					int indexOf = s.indexOf("line ");
					StackElement el = new StackElement();
					el.file = file;
					if (indexOf != -1) {
						String substring = s.substring(indexOf + 5);
						int indexOf2 = substring.indexOf(",");
						if (indexOf2 != -1) {
							el.line = Integer.parseInt(substring.substring(0, indexOf2));
							String substring2 = substring.substring(indexOf2);
							int indexOf3 = substring2.indexOf("in ");
							if (indexOf3 != -1) {
								String substring3 = substring2.substring(indexOf3);
								el.method = substring3;
							}
						} else {
							el.line = Integer.parseInt(substring);
						}
					}
					this.elements.add(el);
				}

			} else {
				if (this.elements.size() > 0) {
					StackElement stackElement = this.elements.get(this.elements.size() - 1);
					stackElement.code = stackElement.code + s + "\r\n";
				}
				System.out.println(s);
			}
			System.out.println(s);
		}
	}

	public void open() {
		if (this.elements.size() > 0) {
			StackElement stackElement = this.elements.get(this.elements.size() - 1);
			String file = stackElement.file;
			Utils.openWithDefault(stackElement, file,stackElement.line);
		}
	}

	
}
