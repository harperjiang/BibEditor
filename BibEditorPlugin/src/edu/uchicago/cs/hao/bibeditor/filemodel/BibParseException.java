package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.text.MessageFormat;

public class BibParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6614563236482795646L;

	public BibParseException(int row, int col) {
		super(MessageFormat.format("Parsing Error at row {0}, column {1}", row, col));
	}

	public BibParseException(int pos) {
		super(MessageFormat.format("Parsing Error at position {0}", pos));
	}
}
