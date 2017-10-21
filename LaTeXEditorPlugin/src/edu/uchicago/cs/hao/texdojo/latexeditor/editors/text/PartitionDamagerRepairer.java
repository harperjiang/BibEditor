package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

public class PartitionDamagerRepairer extends DefaultDamagerRepairer {

	public PartitionDamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation damages entire lines unless clipped by the given
	 * partition.
	 * </p>
	 *
	 * @return the full lines containing the document changes described by the
	 *         document event, clipped by the given partition. If there was a
	 *         partitioning change then the whole partition is returned.
	 */
	@Override
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
		return partition;
	}

	// ---- IPresentationRepairer

	@Override
	public void createPresentation(TextPresentation presentation, ITypedRegion region) {
		int lastStart = region.getOffset();
		int length = 0;
		boolean firstToken = true;
		IToken lastToken = Token.UNDEFINED;
		TextAttribute lastAttribute = getTokenTextAttribute(lastToken);

		fScanner.setRange(fDocument, lastStart, region.getLength());

		while (true) {
			IToken token = fScanner.nextToken();
			if (token.isEOF())
				break;

			TextAttribute attribute = getTokenTextAttribute(token);
			if (lastAttribute != null && lastAttribute.equals(attribute)) {
				length += fScanner.getTokenLength();
				firstToken = false;
			} else {
				if (!firstToken)
					addRange(presentation, lastStart, length, lastAttribute);
				firstToken = false;
				lastToken = token;
				lastAttribute = attribute;
				lastStart = fScanner.getTokenOffset();
				length = fScanner.getTokenLength();
			}
		}

		addRange(presentation, lastStart, length, lastAttribute);
	}
}
