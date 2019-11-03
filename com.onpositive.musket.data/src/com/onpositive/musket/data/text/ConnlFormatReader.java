package com.onpositive.musket.data.text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConnlFormatReader {

	public TextSequenceDataSet read(BufferedReader rs) {
		ArrayList<Document>docs=new ArrayList<>();
		Document curDoc=null;
		Sentence curSeq=null;
		TextSequenceDataSet ds=new TextSequenceDataSet();
		int num=0;
		while (true) {
			try {
				String readLine = rs.readLine();
				if (readLine==null) {
					break;
				}
				String[] split = readLine.split(" ");
				if (split[0].equals("-DOCSTART-")) {
					if (curDoc!=null&&!curDoc.isEmpty()) {
						docs.add(curDoc);
					}
					curDoc=new Document(ds,num);
					num++;
				}
				if (readLine.trim().isEmpty()) {
					if (curSeq!=null&&!curSeq.isEmpty()) {
						if (curDoc==null) {
							curDoc=new Document(ds,num);
							num++;
						}					
						curDoc.add(curSeq);
					}
					curSeq=new Sentence(curDoc);
					continue;
				}
				if (curSeq==null) {
					curSeq=new Sentence(curDoc);
				}
				curSeq.add(new Token(curSeq,split));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (curSeq!=null&&!curSeq.isEmpty()) {
			if (curDoc==null) {
				curDoc=new Document(ds,num);
				num++;
			}
			curDoc.add(curSeq);
		}
		if (curDoc!=null&&!curDoc.isEmpty()) {
			docs.add(curDoc);
		}
		ds.docs.addAll(docs);
		return ds;		
	}
	
	public static void main(String[] args) {
		try {
			new ConnlFormatReader().read(new BufferedReader(new FileReader("D:/tstw4/xxx/data/train.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
