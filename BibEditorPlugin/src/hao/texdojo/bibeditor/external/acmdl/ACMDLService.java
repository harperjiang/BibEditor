package hao.texdojo.bibeditor.external.acmdl;

import java.util.List;

public interface ACMDLService {

	/**
	 * Retrieve BibTex records for given article on ACM DL
	 * 
	 * @param id
	 * @return
	 */
	public List<String> getBib(String id);
}
