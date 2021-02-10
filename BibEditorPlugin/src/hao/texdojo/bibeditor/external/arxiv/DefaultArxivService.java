package hao.texdojo.bibeditor.external.arxiv;

import java.text.MessageFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultArxivService implements ArxivService {

	static final String ARTICLE_URL = "http://export.arxiv.org/api/query?id_list={0}";

	@Override
	public ArxivArticle getArticle(String id) {
		String url = MessageFormat.format(ARTICLE_URL, id);
		try {
			Content content = Request.Get(url).execute().returnContent();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(content.asStream());

			ArxivArticle article = new ArxivArticle();

			NodeList entries = doc.getDocumentElement().getElementsByTagName("entry");
			if (0 == entries.getLength()) {
				return null;
			}
			Element entry = (Element) entries.item(0);

			for (int i = 0; i < entry.getChildNodes().getLength(); i++) {

				Node childNode = (Node) entry.getChildNodes().item(i);
				if (childNode instanceof Element) {
					Element child = (Element) childNode;
					if ("title".equals(child.getTagName())) {
						article.setTitle(child.getTextContent().trim());
					}
					if ("author".equals(child.getTagName())) {
						article.getAuthor().add(child.getTextContent().trim());
					}
					if ("id".equals(child.getTagName())) {
						article.setId(child.getTextContent().trim());
					}
					if ("link".equals(child.getTagName())) {
						String linkTitle = child.getAttribute("title");

						if (StringUtils.isEmpty(linkTitle)) {
							article.setLink(child.getAttribute("href"));
						} else if ("doi".equals(linkTitle)) {
							article.setDoi(child.getAttribute("href"));
						}
					}
					if ("arxiv:journal_ref".equals(child.getTagName())) {
						article.setJournal(child.getTextContent().trim());
					}
					if ("updated".equals(child.getTagName())) {
						Calendar updateTime = javax.xml.bind.DatatypeConverter.parseDateTime(child.getTextContent());
						article.setYear(String.valueOf(updateTime.get(Calendar.YEAR)));
					}
					if("summary".equals(child.getTagName())) {
						article.setSummary(child.getTextContent().trim());
					}
				}
			}

			return article;

		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
