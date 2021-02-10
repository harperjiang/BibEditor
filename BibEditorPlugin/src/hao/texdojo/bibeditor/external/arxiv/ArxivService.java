package hao.texdojo.bibeditor.external.arxiv;

/**
 * Simple Interface to provide Arxiv API query
 * @author harper
 *
 */
public interface ArxivService {

	public ArxivArticle getArticle(String id);
	
}
