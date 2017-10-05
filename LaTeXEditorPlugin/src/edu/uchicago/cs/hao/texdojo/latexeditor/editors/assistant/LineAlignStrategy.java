package edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants;

/**
 * This Strategy align the current line to the previous line, and auto-break
 * lines when it's too long (configurable)
 * 
 * @author harper
 *
 */
public class LineAlignStrategy implements IAutoEditStrategy {

	static Pattern leadingSpace = Pattern.compile("^([\t ]+)[^\t ].*");
	static String crlf = System.getProperty("line.separator");

	int lineWrapLimit = -1;
	boolean lineAlign = false;
	long lastFetch = -1;

	Map<IDocument, Map<Integer, IRegion>> insertedCarriages = new HashMap<IDocument, Map<Integer, IRegion>>();

	public int getLineWrapLimit() {
		long current = System.currentTimeMillis();
		if (current - lastFetch > 1000) {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			lineWrapLimit = prefs.getInt(PreferenceConstants.P_LINE_WRAP, -1);
		}
		return lineWrapLimit;
	}

	public boolean getLineAlign() {
		long current = System.currentTimeMillis();
		if (current - lastFetch > 1000) {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			lineAlign = prefs.getBoolean(PreferenceConstants.P_LINE_ALIGN, true);
		}
		return lineAlign;
	}

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if (command.text.equals("\n")) {
			if (getLineAlign()) {
				// Look for the start of the line
				int start = command.offset - 1;
				try {
					// Look for previous line
					IRegion prevLine = document.getLineInformationOfOffset(start);
					String lineContent = document.get(prevLine.getOffset(), prevLine.getLength());

					Matcher matcher = leadingSpace.matcher(lineContent);
					if (matcher.matches()) {
						String leading = matcher.group(1);
						String text = MessageFormat.format("\n{0}", leading);
						command.text = text;
					}
				} catch (BadLocationException e) {
					// Do nothing
				}
			}
		} else {
			// Line Auto-wrap
			Map<Integer, IRegion> carriages = insertedCarriages.get(document);
			if (carriages == null) {
				carriages = new HashMap<Integer, IRegion>();
				insertedCarriages.put(document, carriages);
			}

			int lwm = getLineWrapLimit();
			if (lwm != -1) {
				try {
					int textLength = command.text.length();
					FindReplaceDocumentAdapter searcher = new FindReplaceDocumentAdapter(document);
					if (textLength > 0) { // Only deal with insertion
						IRegion currentLine = document.getLineInformationOfOffset(command.offset);
						if (currentLine.getLength() + textLength > lwm) {
							// Find the last space to insert a crlf
							IRegion lastSpace = searcher.find(currentLine.getOffset() + currentLine.getLength(),
									"[\t ]+", false, false, false, true);
							if (lastSpace.getOffset() > currentLine.getOffset()) {
								// Replace last space with a crlf
								document.replace(lastSpace.getOffset(), lastSpace.getLength(), crlf);

								// Adjust caret position if the insertion is
								// before the command
								if (command.offset > lastSpace.getOffset()) {
									command.caretOffset = command.offset + command.text.length() + crlf.length()
											- lastSpace.getLength() - 1;
								}
							}
						}
					}
				} catch (BadLocationException e) {
				}
			}
		}
	}

	public static List<IRegion> wrapText(StringBuilder text, int globalOffset, int lineOffset, int limit) {
		List<IRegion> replaced = new ArrayList<IRegion>();

		int current = lineOffset;
		int lastSpaceOffset = -1;
		int lastSpaceLength = -1;
		boolean inSpace = false;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			current++;
			if (current > limit) {
				if (lastSpaceOffset != -1) {
					replaced.add(new Region(globalOffset + lastSpaceOffset, lastSpaceLength));
					text.replace(lastSpaceOffset, lastSpaceOffset + lastSpaceLength - 1, LineAlignStrategy.crlf);
					i -= lastSpaceLength - LineAlignStrategy.crlf.length();
					current = 0;
				}
			}
			switch (ch) {
			case '\n':
			case '\r':
				current = 0;
				inSpace = false;
				break;
			case '\t':
			case ' ':
				if (!inSpace) {
					inSpace = true;
					lastSpaceOffset = i;
				}
				break;
			default:
				if (inSpace) {
					inSpace = false;
					lastSpaceLength = i - lastSpaceOffset;
				}
				break;
			}
		}

		return replaced;
	}
}
