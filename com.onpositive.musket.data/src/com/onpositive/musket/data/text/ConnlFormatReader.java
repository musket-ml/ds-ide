package com.onpositive.musket.data.text;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConnlFormatReader {

	public TextSequenceDataSet read(BufferedReader rs) {
		ArrayList<Document> docs = new ArrayList<>();
		Document curDoc = null;
		Sentence curSeq = null;
		TextSequenceDataSet ds = new TextSequenceDataSet();
		int num = 0;
		int tco = -1;
		while (true) {
			try {
				String readLine = rs.readLine();
				if (readLine == null) {
					break;
				}
				String[] split = readLine.split("\t");
				if (split.length == 1) {
					split = readLine.split(" ");
				}
				if (split[0].equals("-DOCSTART-")) {
					if (curDoc != null && !curDoc.isEmpty()) {
						docs.add(curDoc);
					}
					curDoc = new Document(ds, num);
					num++;
				}
				if (split.length > 1 && tco == -1) {
					tco = split.length;
				}
				if (readLine.trim().isEmpty() || (split.length == 1 && tco > 1)) {
					if (curSeq != null && !curSeq.isEmpty()) {
						if (curDoc == null) {
							curDoc = new Document(ds, num);
							num++;
						}
						curDoc.add(curSeq);
					}
					curSeq = new Sentence(curDoc);
					continue;
				}
				if (curSeq == null) {
					if (curDoc == null) {
						curDoc = new Document(ds, num);
						num++;
					}
					curSeq = new Sentence(curDoc);
				}
				curSeq.add(new Token(curSeq, split));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (curSeq != null && !curSeq.isEmpty()) {
			if (curDoc == null) {
				curDoc = new Document(ds, num);
				num++;
			}
			curDoc.add(curSeq);
		}
		if (docs.isEmpty()) {
			if (curDoc.contents.size() > 1) {
				for (Sentence s:curDoc.contents) {
					Document document = new Document(ds, num);
					s.document=document;
					document.add(s);
					docs.add(document);
					num++;
				}
			}
		} else {
			if (curDoc != null && !curDoc.isEmpty()) {
				docs.add(curDoc);
			}
		}

		ds.docs.addAll(docs);
		ds.init();
		return ds;
	}
	
}