package edu.uchicago.cs.hao.texdojo.latexeditor.editors.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.ArgNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.BeginNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.CommandNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.CommentNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.EndNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXConstant;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.OptionNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.TextNode;

public class LaTeXDocModel {

	private LaTeXModel model = new LaTeXModel();

	/**
	 * Init the model with an given document and partition
	 * 
	 * @param doc
	 * @param tokens
	 * @throws BadLocationException
	 */
	public void init(IDocument doc) throws BadLocationException {
		ITypedRegion[] partitions = doc.computePartitioning(0, doc.getLength());
		model.setNodes(parseTokens(doc, partitions));
		model.organize();
	}

	/**
	 * @param tokens
	 */
	public void update(IDocument doc, ITypedRegion[] tokens) throws BadLocationException {
		if (tokens.length == 0)
			return;
		// Parse Tokens
		List<LaTeXNode> newnodes = parseTokens(doc, tokens);

		int offset = tokens[0].getOffset();
		int insertIndex = 0;

		List<LaTeXNode> nodes = model.getNodes();

		if (nodes.size() == 0 || nodes.get(0).getOffset() > offset)
			insertIndex = 0;
		else if (nodes.get(nodes.size() - 1).getEnd() <= offset)
			insertIndex = nodes.size();
		else {
			for (int i = 0; i < nodes.size() - 1; i++) {
				if (nodes.get(i).getOffset() < offset && nodes.get(i + 1).getOffset() > offset) {
					insertIndex = i + 1;
					break;
				}
			}
		}
		nodes.addAll(insertIndex, newnodes);

		model.organize();

		fireModelChanged();
	}

	protected List<LaTeXNode> parseTokens(IDocument doc, ITypedRegion[] tokens) throws BadLocationException {
		List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();
		for (ITypedRegion token : tokens) {
			String data = doc.get(token.getOffset(), token.getLength());
			if (PartitionScanner.LATEX_COMMAND.equals(token.getType())) {
				if (LaTeXConstant.BEGIN.equals(data)) {
					newnodes.add(new BeginNode(null, token.getOffset(), token.getLength()));
				} else if (LaTeXConstant.END.equals(data)) {
					newnodes.add(new EndNode(null, token.getOffset(), token.getLength()));
				} else {
					newnodes.add(new CommandNode(undecorate(data), token.getOffset(), token.getLength()));
				}
			} else if (PartitionScanner.LATEX_ARG.equals(token.getType())) {
				newnodes.add(new ArgNode(undecorate(data), token.getOffset(), token.getLength()));
			} else if (PartitionScanner.LATEX_OPTION.equals(token.getType())) {
				newnodes.add(new OptionNode(undecorate(data), token.getOffset(), token.getLength()));
			} else if (PartitionScanner.LATEX_COMMENT.equals(token.getType())) {
				newnodes.add(new CommentNode(data, token.getOffset(), token.getLength()));
			} else {
				newnodes.add(new TextNode(data, token.getOffset(), token.getLength()));
			}
		}
		return newnodes;
	}

	/**
	 * @param offset
	 * @param length
	 */
	public void clear(int offset, int length) {
		List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();
		List<LaTeXNode> nodes = model.getNodes();
		for (LaTeXNode node : nodes) {
			if (node.overlap(offset, length)) {
				newnodes.addAll(node.decompose());
			} else {
				newnodes.add(node);
			}
		}
		nodes.clear();
		for (LaTeXNode node : newnodes) {
			if (!node.overlap(offset, length)) {
				nodes.add(node);
			}
		}
		fireModelChanged();
	}

	private String undecorate(String input) {
		if (input.startsWith("\\"))
			return input.substring(1);
		if (input.startsWith("{") && input.endsWith("}")) {
			return input.substring(1, input.length() - 1);
		}
		if (input.startsWith("[") && input.endsWith("]")) {
			return input.substring(1, input.length() - 1);
		}
		return input;
	}

	public List<LaTeXNode> nodes() {
		return model.getNodes();
	}

	private ListenerList<LaTeXDocModelListener> listeners = new ListenerList<>();

	public void addModelListener(LaTeXDocModelListener listener) {
		listeners.add(listener);
	}

	public void removeModelListener(LaTeXDocModelListener listener) {
		listeners.remove(listener);
	}

	protected void fireModelChanged() {
		LaTeXDocModelEvent event = new LaTeXDocModelEvent(this);
		for (LaTeXDocModelListener listener : listeners) {
			listener.modelChanged(event);
		}
	}

}
