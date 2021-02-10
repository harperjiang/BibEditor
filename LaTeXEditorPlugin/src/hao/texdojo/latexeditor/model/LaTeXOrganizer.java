package hao.texdojo.latexeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.commons.lang.StringUtils;

public class LaTeXOrganizer {

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
			push(node);
		}

		return stack;
	}

	private void push(LaTeXNode node) {
		if (node instanceof EndNode && !StringUtils.isEmpty(node.getContent())) {
			EndNode en = (EndNode) node;
			matchBegin(en);
		} else if (node instanceof ArgNode) {
			LaTeXNode stackTop = stack.pop();
			if (stackTop instanceof CommandNode) {
				InvokeNode invoke = new InvokeNode((CommandNode) stackTop);
				invoke.attach(node);
				stack.push(invoke);
			} else if (stackTop instanceof InvokeNode) {
				((InvokeNode) stackTop).attach(node);
				stack.push(stackTop);
			} else if (stackTop instanceof BeginNode && StringUtils.isEmpty(stackTop.getContent())) {
				BeginNode bn = (BeginNode) stackTop;
				bn.append((ArgNode) node);
				stack.push(stackTop);
			} else if (stackTop instanceof EndNode && StringUtils.isEmpty(stackTop.getContent())) {
				EndNode en = (EndNode) stackTop;
				en.append((ArgNode) node);
				push(stackTop);
			} else {
				stack.push(stackTop);
				stack.push(node);
			}
		} else if (node instanceof OptionNode) {
			LaTeXNode stacktop = stack.pop();
			if (stacktop instanceof CommandNode) {
				InvokeNode in = new InvokeNode((CommandNode) stacktop);
				in.attach(node);
				stack.push(in);
			} else if (stacktop instanceof InvokeNode) {
				((InvokeNode) stacktop).attach(node);
				stack.push(stacktop);
			} else {
				stack.push(stacktop);
				stack.push(node);
			}
		} else {
			stack.push(node);
		}
	}

	private void matchBegin(EndNode en) {
		if (StringUtils.isEmpty(en.getContent())) {
			stack.push(en);
			return;
		}
		// find recent begin
		BeginNode bn = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i) instanceof BeginNode && !StringUtils.isEmpty(stack.get(i).getContent())
					&& stack.get(i).getContent().equals(en.getContent())) {
				bn = (BeginNode) stack.get(i);
				break;
			}
		}
		if (bn != null) {
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
