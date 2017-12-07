package edu.uchicago.cs.hao.texdojo.bibeditor.external.acmdl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DefaultACMDLService implements ACMDLService {

	static final String BIBTEX_URL = "https://dl.acm.org/exportformats.cfm?id= {0}1328488&expformat=bibtex";

	@Override
	public List<String> getBib(String id) {
		String url = MessageFormat.format(BIBTEX_URL, id);
		try {
			Content content = Request.Get(url).execute().returnContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(content.asStream());

			NodeList bibtexs = doc.getElementsByTagName("pre");

			List<String> result = new ArrayList<>();
			for (int i = 0; i < bibtexs.getLength(); i++) {
				result.add(bibtexs.item(i).getTextContent());
			}

			return result;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
