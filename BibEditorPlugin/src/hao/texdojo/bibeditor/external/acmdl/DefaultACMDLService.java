package hao.texdojo.bibeditor.external.acmdl;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

public class DefaultACMDLService implements ACMDLService {

	static final String BIBTEX_URL = "https://dl.acm.org/exportformats.cfm?id={0}&expformat=bibtex";

	static final Pattern BIBTEX_ENTRY = Pattern.compile("<PRE id=\"\\d+\">([^<]+)</pre>");
	
	@Override
	public List<String> getBib(String id) {
		String url = MessageFormat.format(BIBTEX_URL, id);
		try {
			Request request = Request.Get(url);
			request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
			request.setHeader("Accept-Encoding","gzip, deflate, br");
			request.setHeader("Accept-Language","en-US,en;q=0.9,zh-TW;q=0.8,zh;q=0.7");
			request.setHeader("Connection","keep-alive");
			request.setHeader("DNT","1");
			request.setHeader("Host","dl.acm.org");
			request.setHeader("Upgrade-Insecure-Requests","1");
			Content content = request.execute().returnContent();
			
			String data = content.asString(StandardCharsets.UTF_8);

			Matcher matcher = BIBTEX_ENTRY.matcher(data);
			
			List<String> result = new ArrayList<>();
			
			while(matcher.find()) {
				result.add(matcher.group(1));
			}

			return result;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
