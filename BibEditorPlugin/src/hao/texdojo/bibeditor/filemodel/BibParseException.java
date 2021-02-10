/*******************************************************************************
 * Copyright (c) Oct 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package hao.texdojo.bibeditor.filemodel;

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
