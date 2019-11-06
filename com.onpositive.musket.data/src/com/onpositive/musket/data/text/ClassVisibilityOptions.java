package com.onpositive.musket.data.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.java.annotations.Display;

@Display("dlf/classSettings.dlf")
public class ClassVisibilityOptions {

	public static class ClassVisibilitySetting {

		public String name;

		public Color rgb;

		public boolean show;

		public int group;

		public String getRGB() {
			return rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue();
		}

		public void setRGB(String s) {
			try {
				String[] split = s.split(",");
				int r = Integer.parseInt(split[0]);
				int g = Integer.parseInt(split[1]);
				int b = Integer.parseInt(split[2]);
				this.rgb = new Color(r, g, b);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	ArrayList<ClassVisibilitySetting> settings = new ArrayList<>();

	public ClassVisibilityOptions() {

	}

	protected ArrayList<ClassVisibilitySetting> selection = new ArrayList<>();

	public void showAll() {
		this.settings.forEach(v -> {
			v.show = true;
			ObjectChangeManager.markChanged(v);
		});
	}

	public void hideAll() {
		this.settings.forEach(v -> {
			v.show = false;
			ObjectChangeManager.markChanged(v);
		});
	}

	public void showSelected() {
		if (selection != null) {
			selection.forEach(v -> {
				ClassVisibilitySetting v1 = (ClassVisibilitySetting) v;
				v1.show = true;
				ObjectChangeManager.markChanged(v1);
			});
		}
	}
	
	boolean showInText=true;
	private HashSet<String> visibleClasses;

	public void hideSelected() {
		if (selection != null) {
			selection.forEach(v -> {
				ClassVisibilitySetting v1 = (ClassVisibilitySetting) v;
				v1.show = false;
				ObjectChangeManager.markChanged(v1);
			});
		}
	}

	static Color[] classesC = new Color[] { Color.CYAN, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW,
			Color.DARK_GRAY, Color.PINK, Color.ORANGE, Color.MAGENTA, Color.BLACK };

	public ClassVisibilityOptions(String settings, IHasClassGroups groups) {
		if (settings.isEmpty()) {
			int num = 0;
			ArrayList<LinkedHashSet<String>> classGroups = groups.classGroups();
			for (int i = 0; i < classGroups.size(); i++) {
				LinkedHashSet<String> linkedHashSet = classGroups.get(i);
				ArrayList<String> sm = new ArrayList<>(linkedHashSet);
				Collections.sort(sm);
				for (String sa : sm) {
					ClassVisibilitySetting c = new ClassVisibilitySetting();
					c.group = i;
					c.name = sa;
					c.show = true;
					Color color = classesC[num % classesC.length];
					c.rgb = color;
					num++;
					this.settings.add(c);
				}
			}
			return;
		}
		int ga=settings.indexOf('^');
		if (ga!=-1) {
			this.showInText=Boolean.parseBoolean(settings.substring(0,ga));
			settings=settings.substring(ga+1);
		}
		String[] split = settings.split(",");
		for (String s : split) {
			String[] split2 = s.split(";");
			ClassVisibilitySetting set = new ClassVisibilitySetting();
			set.name = unescape(split2[0]);
			String[] split3 = split2[1].split(":");
			int r = Integer.parseInt(split3[0]);
			int g = Integer.parseInt(split3[1]);
			int b = Integer.parseInt(split3[2]);
			set.rgb = new Color(r, g, b);
			set.show = Boolean.parseBoolean(split2[2]);
			set.group = Integer.parseInt(split2[3]);
			this.settings.add(set);
		}
	}

	public String toString() {
		return this.showInText+"^"+this.settings.stream().map(x -> escapeName(x) + ";" + toString(x) + ";" + x.show + ";" + x.group)
				.collect(Collectors.joining(","));
	}

	protected String toString(ClassVisibilitySetting x) {
		return x.rgb.getRed() + ":" + x.rgb.getGreen() + ":" + x.rgb.getBlue();
	}

	protected String escapeName(ClassVisibilitySetting x) {
		String replace = x.name.replace((CharSequence) ",", "@comma");
		replace = replace.replace((CharSequence) ";", "@sep");
		replace = replace.replace((CharSequence) ":", "@colon");
		return replace;
	}

	protected String unescape(String v) {
		String replace = v.replace("@comma", ",");
		replace = replace.replace((CharSequence) "@sep", ";");
		replace = replace.replace((CharSequence) "@colon", ":");
		return replace;
	}

	public List<String> filter(List<String> classes) {
		if (this.visibleClasses==null) {
			this.visibleClasses=new HashSet<>();
			this.settings.forEach(v->{
				if (v.show) {
					this.visibleClasses.add(v.group+v.name);
				}
			});
		}
		int size = classes.size();
		ArrayList<String>results=new ArrayList<>();
		for (int i=0;i<size;i++) {
			String key=i+classes.get(i);
			if (visibleClasses.contains(key)) {
				results.add(classes.get(i));
			}
		}
		return results;
	}

	public Color getColor(String string) {
		for (ClassVisibilitySetting s:this.settings) {
			if (s.name.equals(string)) {
				return s.rgb;
			}
		}
		return null;		
	}
}
