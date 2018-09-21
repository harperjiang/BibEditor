package edu.uchicago.cs.hao.texdojo.latexeditor.editors.handlers;

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_LINE_WRAP;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.IDocumentProvider;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.GroupNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.InvokeNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.TextNode;

public class TextFormatterHandler extends TextHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event) throws ExecutionException {
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		if (doc != null) {
			// Format the entire document

			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			int lineLimit = prefs.getInt(P_LINE_WRAP, -1);
			doc.set(format(doc.get(), lineLimit));
		}
		return null;
	}

	public static String format(String input, int lineLimit) {
		if (lineLimit == -1) {
			return input;
		}
		LaTeXModel model = LaTeXModel.parseFromFile(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
		final StringBuffer buffer = new StringBuffer();

		model.traverse(n -> {
			if (n instanceof TextNode) {
				TextNode t = (TextNode) n;
				buffer.append(Arrays.stream(t.getContent().split("[\n\r]", -1)).map(line -> formatLine(line, lineLimit))
						.collect(Collectors.joining("\n")));
			} else if (n instanceof InvokeNode || n instanceof GroupNode) {

			} else {
				buffer.append(n.toString());
			}
		});

		return buffer.toString();
	}

	public static final String formatLine(String line, int limit) {
		String[] words = line.split("\\s+");
		StringBuffer buffer = new StringBuffer();
		int currentLength = 0;
		for (String word : words) {
			if (word.length() + 1 + currentLength > limit) {
				if (currentLength == 0) {
					buffer.append(word).append("\n");
				} else {
					buffer.append("\n");
					buffer.append(word);
					currentLength = word.length();
				}
			} else {
				if (currentLength != 0) {
					buffer.append(" ");
					currentLength += 1;
				}
				buffer.append(word);
				currentLength += word.length();
			}
		}

		return buffer.toString().trim();
	}
}
