package edu.uchicago.cs.hao.texdojo.latexeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LaTeXParser {

	private List<LaTeXNode> newnodes = new ArrayList<LaTeXNode>();

	private Stack<LaTeXNode> stack = new Stack<LaTeXNode>();

	public List<LaTeXNode> parse(List<LaTeXNode> nodes) {
		newnodes.clear();
		stack.clear();

		for (LaTeXNode node : nodes) {
			if (stack.isEmpty()) {
				stack.push(node);
				continue;
			}
			if (node instanceof BeginNode || node instanceof EndNode || node instanceof CommandNode
					|| node instanceof TextNode) {
				LaTeXNode stacktop = stack.pop();
				if (stacktop instanceof EndNode) {
					EndNode en = (EndNode) stacktop;
					if (en.getContent() != null) {
						matchBegin(en);
						stack.push(node);
					} else {
						stack.push(stacktop);
						stack.push(node);
					}
				} else {
					stack.push(stacktop);
					stack.push(node);
				}
			}
			if (node instanceof ArgNode) {
				LaTeXNode stackTop = stack.pop();
				if (stackTop instanceof CommandNode) {
					InvokeNode invoke = new InvokeNode((CommandNode) stackTop);
					invoke.attach(node);
					stack.push(invoke);
				} else if (stackTop instanceof InvokeNode) {
					((InvokeNode) stackTop).attach(node);
					stack.push(stackTop);
				} else if (stackTop instanceof BeginNode) {
					BeginNode bn = (BeginNode) stackTop;
					if (bn.getContent() != null) {
						stack.push(bn);
						stack.push(node);
					} else {
						bn.append((ArgNode) node);
						stack.push(stackTop);
					}
				} else if (stackTop instanceof EndNode) {
					EndNode en = (EndNode) stackTop;
					if (en.getContent() != null) {
						matchBegin(en);
						stack.push(node);
					} else {
						en.append((ArgNode) node);
						stack.push(stackTop);
					}
				} else {
					stack.push(node);
				}
			}
			if (node instanceof OptionNode) {
				LaTeXNode stacktop = stack.pop();
				if (stacktop instanceof CommandNode) {
					InvokeNode in = new InvokeNode((CommandNode) stacktop);
					in.attach(node);
					stack.push(in);
				} else if (stacktop instanceof InvokeNode) {
					((InvokeNode) stacktop).attach(node);
					stack.push(stacktop);
				} else if (stacktop instanceof EndNode) {
					EndNode en = (EndNode) stacktop;
					if (en.getContent() != null) {
						matchBegin(en);
						stack.push(node);
					} else {
						stack.push(stacktop);
						stack.push(node);
					}
				} else {
					stack.push(stacktop);
					stack.push(node);
				}
			}
		}

		// Final processing, EOF node
		LaTeXNode stacktop = stack.pop();
		if (stacktop instanceof EndNode && ((EndNode) stacktop).getContent() != null) {
			matchBegin((EndNode) stacktop);
		}

		return stack;
	}

	private void matchBegin(EndNode en) {
		// find recent begin
		BeginNode bn = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof BeginNode && stack.get(i).getContent() != null) {
				bn = (BeginNode) stack.get(i);
				break;
			}
		}
		if (bn != null && en.getContent().equals(bn.getContent())) {

			List<LaTeXNode> content = new ArrayList<LaTeXNode>();
			while (stack.peek() != bn) {
				content.add(0, stack.pop());
			}
			stack.pop();
			stack.push(new GroupNode(bn, en, content));
		} else {
			stack.push(en);
		}
	}
}
