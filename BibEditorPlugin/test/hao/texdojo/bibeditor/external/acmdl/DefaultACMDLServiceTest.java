package hao.texdojo.bibeditor.external.acmdl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import hao.texdojo.bibeditor.external.acmdl.DefaultACMDLService;

public class DefaultACMDLServiceTest {

	@Test
	public void testGetBib() {
		List<String> result = new DefaultACMDLService().getBib("1328488");
		assertEquals(2, result.size());
	}

	@Test
	public void testParse() {
		Pattern BIBTEX_ENTRY = Pattern.compile("<PRE id=\"\\d+\">([^<]+)</pre>");
		String data = "<PRE id=\"1328488\">\r\n@article{Fisher:2008:DSF:1328897.1328488,\n author = {Fisher, Kathleen and Walker, David and Zhu, Kenny Q. and White, Peter},\n title = {From Dirt to Shovels: Fully Automatic Tool Generation from Ad Hoc Data},\n journal = {SIGPLAN Not.},\n issue_date = {January 2008},\n volume = {43},\n number = {1},\n month = jan,\n year = {2008},\n issn = {0362-1340},\n pages = {421--434},\n numpages = {14},\n url = {http://doi.acm.org/10.1145/1328897.1328488},\n doi = {10.1145/1328897.1328488},\n acmid = {1328488},\n publisher = {ACM},\n address = {New York, NY, USA},\n keywords = {ad hoc data, data description languages, grammar induction, tool generation},\n} \r\n</pre>";
		
		assertTrue(BIBTEX_ENTRY.matcher(data).find());

	}
}
