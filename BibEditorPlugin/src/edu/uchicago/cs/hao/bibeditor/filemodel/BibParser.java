package edu.uchicago.cs.hao.bibeditor.filemodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BibParser {

	static enum State {
		READ_ENTRY, APPEND_ENTRY, READ_ID, READ_PROPKEY, READ_PROPVAL, APPEND_PROPVAL, AFTER_PROPVAL
	}

	private static int BUFFER_SIZE = 8192;

	public BibModel parse(String input) throws IOException {
		return parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
	}

	public BibModel parse(InputStream input) throws IOException {
		BibModel model = new BibModel();
		BibEntry entry = null;
		BibProp prop = null;
		int expected = TokenType.TYPE;
		for (Token token : tokenize(input)) {
			if ((expected & token.type) == 0) {
				// Parse error
				throw new BibParseException(token.row, token.col);
			}
			switch (token.type) {
			case TokenType.TYPE:
				if (null != entry)
					model.addEntry(entry);
				entry = new BibEntry();
				entry.setType(token.content);
				expected = TokenType.KEY;
				break;
			case TokenType.KEY:
				entry.setId(token.content);
				expected = TokenType.PROP_KEY;
				break;
			case TokenType.PROP_KEY:
				if (prop != null) {
					entry.addProperty(prop);
				}
				prop = new BibProp(token.getContent(), null);

				expected = TokenType.PROP_VAL;
				break;
			case TokenType.PROP_VAL:
				prop.setValue(token.getContent());
				entry.addProperty(prop);
				prop = null;
				expected = TokenType.TYPE | TokenType.PROP_KEY;
				break;
			}
		}
		if (null != entry)
			model.addEntry(entry);
		input.close();
		return model;
	}

	public Iterable<Token> tokenize(InputStream input) throws IOException {

		List<Token> tokens = new ArrayList<Token>();

		State state = State.READ_ENTRY;

		byte[] buffer = new byte[BUFFER_SIZE];

		int readCount = 0;
		int charCounter = 0;
		StringBuilder string = new StringBuilder();
		int stackCount = 0;
		boolean hasBracket = false;
		boolean skipSpace = true;

		int row = 1;
		int col = 0;

		int tokenStart = 0;

		while (true) {
			readCount = input.read(buffer);
			for (int i = 0; i < readCount; i++) {
				int character = buffer[i];

				col++;
				// Ignore CRLF
				if (character == '\n' || character == '\r') {
					charCounter++;
					row++;
					col = 0;
					continue;
				}
				// Space
				if (skipSpace && (character == ' ' || character == '\t')) {
					charCounter++;
					continue;
				}

				// State Transition
				switch (state) {
				case READ_ENTRY:
					if ('@' == character) {
						state = State.APPEND_ENTRY;
						string.setLength(0);
					} else {
						// Ignore invalid character silently
						// throw new BibParseException(rowCount, colCount);
					}
					break;
				case APPEND_ENTRY:
					if ('{' == character) {
						state = State.READ_ID;
						String data = string.toString();
						tokens.add(new Token(tokenStart, TokenType.TYPE, data.trim(), row, col));
						string.setLength(0);
					} else {
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
					}
					break;
				case READ_ID:
					if (',' == character) {
						state = State.READ_PROPKEY;
						String data = string.toString();
						tokens.add(new Token(charCounter - data.length(), TokenType.KEY, data.trim(), row,
								col - data.length()));
						string.setLength(0);
					} else {
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
					}
					break;
				case READ_PROPKEY:
					if ('=' == character) {
						state = State.READ_PROPVAL;

						String data = string.toString();
						tokens.add(new Token(tokenStart, TokenType.PROP_KEY, data.trim(), row, col));

						string.setLength(0);
					} else if ('}' == character) {
						state = State.READ_ENTRY;
					} else {
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
					}
					break;
				case READ_PROPVAL:
					if ('{' == character) {
						string.setLength(0);
						hasBracket = true;
						state = State.APPEND_PROPVAL;
						skipSpace = false;
					} else if (',' == character) {
						state = State.READ_PROPKEY;
						tokens.add(new Token(charCounter, TokenType.PROP_VAL, "", row, col));
					} else {
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
						hasBracket = false;
						state = State.APPEND_PROPVAL;
						skipSpace = false;
					}
					break;
				case APPEND_PROPVAL:
					if ('{' == character) {
						stackCount++;
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
					} else if ('}' == character) {
						if (stackCount == 0) {
							state = State.AFTER_PROPVAL;
							skipSpace = true;

							String data = string.toString();
							tokens.add(new Token(tokenStart, TokenType.PROP_VAL, data.trim(), row, col));

							string.setLength(0);
						} else {
							stackCount--;
							string.append((char) character);
						}
					} else if (',' == character && hasBracket == false) {
						state = State.READ_PROPKEY;
						skipSpace = true;

						String data = string.toString();
						tokens.add(new Token(tokenStart, TokenType.PROP_VAL, data.trim(), row, col));

						string.setLength(0);
					} else {
						if (string.length() == 0)
							tokenStart = charCounter;
						string.append((char) character);
					}
					break;
				case AFTER_PROPVAL:
					if (',' == character) {
						state = State.READ_PROPKEY;
					} else if ('}' == character) {
						state = State.READ_ENTRY;
					} else
						throw new BibParseException(charCounter);
					break;
				default:
					break;
				}
				charCounter++;
			}

			if (readCount != BUFFER_SIZE)
				break;
		}

		return tokens;
	}

	public static class TokenType {
		public static final int TYPE = 1;
		public static final int KEY = 2;
		public static final int PROP_KEY = 4;
		public static final int PROP_VAL = 8;
	}

	public static class Token {

		int from;

		int length;

		int type;

		String content;

		int row;

		int col;

		public Token(int from, int type, String content, int row, int col) {
			super();
			this.from = from;
			this.length = content.length();
			this.type = type;
			this.content = content;
			this.row = row;
			this.col = col;
		}

		public int getFrom() {
			return from;
		}

		public int getLength() {
			return length;
		}

		public int getType() {
			return type;
		}

		public String getContent() {
			return content;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

	}
}
