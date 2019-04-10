package com.onpositive.dside.ui;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StringContent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.PaletteShelfRenderer;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.onpositive.dside.tasks.analize.IAnalizeResults;
import com.onpositive.dside.ui.VisualizerViewer.It;
import com.onpositive.musket_core.IDataSet;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.richtext.StyledString.Style;
import com.onpositive.viewer.extension.coloring.IItemPaintParticipant;
import com.onpositive.viewer.extension.coloring.OwnerDrawSupport;

public class VirtualTable extends VisualizerViewer<Control> {

	private LinkedHashMap<It, StyledString> texts = new LinkedHashMap<>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected IdentityHashMap<IDataSet, Table> tables = new IdentityHashMap<>();

	@Override
	protected Control createControl(Composite conComposite) {
		final PShelf CTabFolder = new PShelf(conComposite, SWT.NONE);
		RedmondShelfRenderer renderer = new RedmondShelfRenderer();
		// renderer.setShadeColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		CTabFolder.setRenderer(renderer);
		IAnalizeResults input2 = this.getInput();
		int size = input2.size();
		for (int i = 0; i < size; i++) {
			IDataSet iDataSet = input2.get(i);
			String name = iDataSet.name();
			int count = iDataSet.len();
			PShelfItem pitem = new PShelfItem(CTabFolder, SWT.NONE);
			pitem.setText(name + "(" + count + ")");
			Table virtualTable = new Table(pitem.getBody(), SWT.VIRTUAL);
			tables.put(iDataSet, virtualTable);
			// virtualTable.setHeaderVisible(true);
			TableColumn column = new TableColumn(virtualTable, SWT.RIGHT);
			column.setText("My Virtual Table");
			column.setWidth(480);
			virtualTable.addListener(SWT.SetData, new Listener() {

				@Override
				public void handleEvent(Event event) {
					TableItem it = (TableItem) event.item;
					int tableIndex = virtualTable.indexOf(it);
					it.setData(tableIndex);
				}
			});
			virtualTable.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					column.setWidth(virtualTable.getSize().x - 20);
					super.controlResized(e);
				}
			});
			TableViewer tv = new TableViewer(virtualTable);
			new OwnerDrawSupport(virtualTable, tv) {

				@Override
				protected void refreshItem(Item item) {

				}

				@Override
				public IItemPaintParticipant getPaintParticipantLabel(Item item, int index) {
					return null;
				}

				@Override
				public StyledString getColoredLabel(Item item, int index) {
					Integer data = (Integer) item.getData();
					if (data == null) {
						data = virtualTable.indexOf((TableItem) item);
					}
					It key = new It(data, iDataSet);
					if (texts.containsKey(key)) {
						return texts.get(key);
					}
					loadImage(key);
					return new StyledString("...");
				}
			};
			virtualTable.setItemCount(count);
			pitem.getBody().setLayout(new FillLayout());
		}
		FillLayout layout = new FillLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		conComposite.setLayout(layout);
		return CTabFolder;
	}

	protected void loadImage(It key) {

		tasks.offerLast(() -> {
			try {
				if (texts.containsKey(key)) {
					return;
				}
				Object item = null;
				try {
					item = key.ds.item(key.data);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String s = item != null ? item.toString() : null;
				texts.put(key, parseString(s));

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						tables.get(key.ds).redraw();
						;
					}
				});
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
	}

	private StyledString parseString(String s) {
		// s="<p><span style='color:red'>deded</span> hello</p>";
		StyledString styledString = new StyledString();
		if (this.html) {
			try {
				Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(s)));
				fill(parse.getDocumentElement(), styledString, new HashMap<>());
			} catch (SAXException | IOException | ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			s=s.replace('\n', ' ');
			s=s.replace('\r', ' ');
			s=s.replace('\t', ' ');
			int lastIndexOf = s.lastIndexOf("- [");
			String end="";
			if (lastIndexOf!=-1) {
				end=s.substring(lastIndexOf+1);
				s=s.substring(0, lastIndexOf);
			}
			StringTokenizer stringTokenizer = new StringTokenizer(s, "[],", true);
			while (stringTokenizer.hasMoreTokens()) {
				String nextToken = stringTokenizer.nextToken();
				if (nextToken.charAt(0)=='['||nextToken.charAt(0)==']') {
					styledString.append(nextToken,new Style("dark green", null));
				}
				else if (nextToken.charAt(0)==',') {
					styledString.append(nextToken,new Style("dark green", null));
				}
				else {
					styledString.append(nextToken,new Style("dark blue", null));
				}
			}
			if (end.length()>0) {
				styledString.append(end,new Style("dark red", null));
			}
		}

		return styledString;
	}

	private void fill(org.w3c.dom.Element documentElement, StyledString styledString, HashMap<String, String> styles) {
		NodeList childNodes = documentElement.getChildNodes();
		HashMap<String, String> newStyles = new HashMap<>(styles);
		styles = newStyles;
		String attribute = documentElement.getAttribute("style");
		if (attribute != null && attribute.length() > 0) {
			String[] split = attribute.split(";");
			for (String s : split) {
				int indexOf = s.indexOf(':');
				if (indexOf != -1) {
					String at = s.substring(0, indexOf).trim();
					String value = s.substring(indexOf + 1);
					styles.put(at, value);
				}
			}
		}
		if (documentElement.getNodeName().equals("b")) {
			styles.put("bold", "true");
		}
		if (documentElement.getNodeName().equals("i")) {
			styles.put("italic", "true");
		}
		if (documentElement.getNodeName().equals("s")) {
			styles.put("strike", "true");
		}
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof Text) {
				styledString.append(item.getTextContent(), createStyle(styles));
			}
			if (item instanceof org.w3c.dom.Element) {
				fill((org.w3c.dom.Element) item, styledString, styles);
			}
		}
	}

	private Style createStyle(HashMap<String, String> styles) {
		String color = null;
		String backgroundColor = null;
		if (styles.containsKey("color")) {
			color = styles.get("color");
		}
		if (styles.containsKey("background-color")) {
			backgroundColor = styles.get("background-color");
		}
		Style st = new Style(color, backgroundColor);
		if (styles.containsKey("bold")) {
			st.setFontStyle(st.getFontStyle() | SWT.BOLD);
		}
		if (styles.containsKey("italic")) {
			st.setFontStyle(st.getFontStyle() | SWT.ITALIC);
		}
		if (styles.containsKey("strike")) {
			st.setStrikethrough(true);
		}
		return st;
	}
}
