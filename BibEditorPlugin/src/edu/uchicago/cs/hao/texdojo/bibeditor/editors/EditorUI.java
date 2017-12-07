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

package edu.uchicago.cs.hao.texdojo.bibeditor.editors;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser.Token;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser.TokenType;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibProp;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.EntryType;

public class EditorUI implements PropertyChangeListener {

	private BibModel model;

	public BibEntry selected() {
		StructuredSelection sel = (StructuredSelection) table.getSelection();
		if (sel.isEmpty())
			return null;
		return (BibEntry) sel.getFirstElement();
	}

	public BibEntry[] allSelected() {
		StructuredSelection sel = (StructuredSelection) table.getSelection();
		if (sel == null || sel.isEmpty())
			return null;
		Object[] sels = sel.toArray();
		BibEntry[] allsels = new BibEntry[sels.length];
		for (int i = 0; i < sels.length; i++) {
			allsels[i] = (BibEntry) sels[i];
		}
		return allsels;
	}

	private boolean dirty;

	private PropertyChangeSupport support;

	// Listeners

	private ModifyListener textModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if (selected() != null) {
				// Try parse the content, block the change if syntex incorrect
				String text = editText.getText();
				try {
					BibModel tempmodel = new BibParser().parse(text);
					if (1 != tempmodel.getEntries().size())
						throw new IllegalArgumentException();
					else {
						// Update table info
						BibEntry newEntry = tempmodel.getEntries().get(0);
						// Reset selection
						EditorUI.this.model.replaceEntry(selected(), newEntry);
						table.setSelection(new StructuredSelection(newEntry));
					}
					// Add color to the text
					updateTextContent(editText.getText());
					EditorUI.this.setDirty(true);
					editText.setBackground(null);
				} catch (Exception ex) {
					editText.setBackground(Resources.color(Resources.COLOR_WARNBACK));
				}

			}
		}
	};

	// Components
	private TableViewer table;

	private ColumnViewerComparator tableComparator;

	private StyledText editText;

	private MenuManager menuManager;

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public TableViewer getTable() {
		return table;
	}

	public StyledText getText() {
		return editText;
	}

	public EditorUI() {
		super();
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof BibModel) {

			if ("addEntry".equals(evt.getPropertyName())) {
				// Disable comparator
				table.setComparator(null);
				table.insert(evt.getNewValue(), model.getEntries().size() - 1);
				table.setSelection(new StructuredSelection(evt.getNewValue()));
				updateTextContent(evt.getNewValue().toString());
			}
			if ("removeEntry".equals(evt.getPropertyName())) {
				table.remove(evt.getOldValue());
				if (model.getEntries().size() > 0) {
					BibEntry first = model.getEntries().get(0);
					table.setSelection(new StructuredSelection(first));
					updateTextContent(first.toString());
				} else {
					updateTextContent("");
				}
			}
			if ("entries".equals(evt.getPropertyName())) {
				IndexedPropertyChangeEvent ipc = (IndexedPropertyChangeEvent) evt;
				table.replace(ipc.getNewValue(), ipc.getIndex());
			}
			setDirty(true);
		}
	}

	public void createUI(Composite parent) {
		prepareColors();
		// plusIcon = new Image(Display.getCurrent(), "icons/toolbar_plus.gif");
		// minusIcon = new Image(Display.getCurrent(),
		// "icons/toolbar_minus.gif");

		SashForm form = new SashForm(parent, SWT.VERTICAL);
		form.setLayout(new FillLayout());

		createTable(form);

		createEditPanel(form);

		form.setWeights(new int[] { 70, 30 });

		createMenu();
	}

	public void dispose() {
		table = null;
		editText = null;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		boolean oldDirty = this.dirty;
		this.dirty = dirty;
		support.firePropertyChange("dirty", oldDirty, dirty);
	}

	public void save(File target, boolean preserveCase) {
		try {
			PrintWriter output = new PrintWriter(new FileOutputStream(target));

			for (BibEntry entry : model.getEntries()) {
				entry.setPreserveCase(preserveCase);
				output.println(entry.toString());
			}
			output.close();
			setDirty(false);

			table.refresh(true);

			BibEntry selected = selected();
			if (selected != null)
				updateTextContent(selected.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void updateTextContent(String content) {
		editText.removeModifyListener(textModifyListener);

		try {
			if (!content.equals(editText.getText()))
				editText.setText(content);
			InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			Iterable<Token> tokens = new BibParser().tokenize(in);
			tokens.forEach(new Consumer<Token>() {
				@Override
				public void accept(Token t) {
					switch (t.getType()) {
					case TokenType.TYPE:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(),
								Resources.color(Resources.COLOR_BROWN), null, SWT.BOLD));
						break;
					case TokenType.KEY:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(),
								Resources.color(Resources.COLOR_MAGENTA), null, SWT.BOLD));
						break;
					case TokenType.PROP_KEY:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(),
								Resources.color(Resources.COLOR_DARKBLUE), null, SWT.BOLD));
						break;
					case TokenType.PROP_VAL:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(),
								Resources.color(Resources.COLOR_GRASS), null, SWT.NORMAL));
						break;
					default:
						break;
					}
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			editText.addModifyListener(textModifyListener);
		}

	}

	public BibModel getModel() {
		return model;
	}

	public void setModel(BibModel model) {
		if (this.model != null)
			this.model.removePropertyChangeListener(this);
		this.model = model;
		if (this.model != null)
			this.model.addPropertyChangeListener(this);
	}

	public static class BibEntryColumnLabelProvider extends ColumnLabelProvider {

		private int index;

		public BibEntryColumnLabelProvider(int index) {
			super();
			this.index = index;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof BibEntry) {
				return extract(element, index);
			}
			return super.getText(element);
		}

		static String extract(Object element, int index) {
			BibEntry entry = (BibEntry) element;
			switch (index) {
			case 0:
				return entry.getId();
			case 1:
				return entry.getType();
			case 2:
				return entry.getProperty(BibProp.TITLE);
			case 3:
				return entry.getProperty(BibProp.YEAR);
			case 4:
				return entry.getProperty(BibProp.AUTHOR);
			default:
				return "";
			}
		}

		@Override
		public Color getBackground(Object element) {
			if (element instanceof BibEntry && ((BibEntry) element).isHighlight())
				return Resources.color(Resources.COLOR_HIGHLIGHT);
			return super.getBackground(element);
		}
	}

	public static class ColumnViewerComparator extends ViewerComparator {
		private int propertyIndex;
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public ColumnViewerComparator() {
			this.propertyIndex = 0;
			direction = DESCENDING;
		}

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			String data1 = BibEntryColumnLabelProvider.extract(e1, propertyIndex);
			String data2 = BibEntryColumnLabelProvider.extract(e2, propertyIndex);
			if (null == data1)
				data1 = "";
			if (null == data2)
				data2 = "";
			int rc = data1.compareTo(data2);
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}
	}

	public class ColumnSelectionListener implements SelectionListener {

		int index = -1;

		public ColumnSelectionListener(int index) {
			super();
			this.index = index;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			tableComparator.setColumn(this.index);
			table.setComparator(tableComparator);
			table.refresh();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	}

	/*
	 * =======================================================================
	 * UI Section
	 * =======================================================================
	 */
	private void createTable(Composite form) {
		Composite tablePanel = new Composite(form, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tablePanel.setLayout(tableColumnLayout);

		table = new TableViewer(tablePanel, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		table.setContentProvider(new ArrayContentProvider());

		TableColumn keyColumn = new TableColumn(table.getTable(), SWT.NONE);
		keyColumn.setText("cite_key");
		TableViewerColumn keyColumnViewer = new TableViewerColumn(table, keyColumn);
		keyColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(0));
		keyColumn.addSelectionListener(new ColumnSelectionListener(0));

		TableColumn typeColumn = new TableColumn(table.getTable(), SWT.NONE);
		typeColumn.setText("Type");
		TableViewerColumn typeColumnViewer = new TableViewerColumn(table, typeColumn);
		typeColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(1));
		typeColumn.addSelectionListener(new ColumnSelectionListener(1));

		TableColumn titleColumn = new TableColumn(table.getTable(), SWT.NONE);
		titleColumn.setText("Title");
		TableViewerColumn titleColumnViewer = new TableViewerColumn(table, titleColumn);
		titleColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(2));
		titleColumn.addSelectionListener(new ColumnSelectionListener(2));

		TableColumn yearColumn = new TableColumn(table.getTable(), SWT.NONE);
		yearColumn.setText("Year");
		TableViewerColumn yearColumnViewer = new TableViewerColumn(table, yearColumn);
		yearColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(3));
		yearColumn.addSelectionListener(new ColumnSelectionListener(3));

		TableColumn authorColumn = new TableColumn(table.getTable(), SWT.NONE);
		authorColumn.setText("Author");
		TableViewerColumn authorColumnViewer = new TableViewerColumn(table, authorColumn);
		authorColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(4));

		tableColumnLayout.setColumnData(keyColumn, new ColumnWeightData(5, 150, true));
		tableColumnLayout.setColumnData(typeColumn, new ColumnWeightData(5, 150, true));
		tableColumnLayout.setColumnData(titleColumn, new ColumnWeightData(50, 400, true));
		tableColumnLayout.setColumnData(yearColumn, new ColumnPixelData(50));
		tableColumnLayout.setColumnData(authorColumn, new ColumnWeightData(40, 400, true));

		table.setInput(model.getEntries());
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		table.getTable().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Object selection = ((StructuredSelection) table.getSelection()).getFirstElement();
				if (null != selection) {
					updateTextContent(selection.toString());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});
		// Supporting Table Sorting, only when clicking
		tableComparator = new ColumnViewerComparator();
		table.setComparator(null);
	}

	private void createEditPanel(Composite parent) {
		Composite editPanel = new Composite(parent, SWT.NONE);
		editPanel.setLayout(new FillLayout());
		editText = new StyledText(editPanel, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		editText.setFont(JFaceResources.getTextFont());
		editText.addModifyListener(textModifyListener);
		JFaceResources.getFontRegistry().addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
				if (JFaceResources.getFontRegistry().equals(event.getSource())
						&& JFaceResources.TEXT_FONT.equals(event.getProperty())) {
					editText.setFont(JFaceResources.getTextFont());
				}
				return;
			}
		});
	}

	public MessageConsole getConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (Constants.CONSOLE_NAME.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(Constants.CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private void createMenu() {
		menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(table.getTable());
		// set the menu on the SWT widget
		table.getTable().setMenu(menu);
	}

	private void prepareColors() {
		ColorRegistry reg = JFaceResources.getColorRegistry();
		if (null == reg.get(Resources.COLOR_BROWN))
			reg.put(Resources.COLOR_BROWN, new RGB(66, 6, 14));
		if (null == reg.get(Resources.COLOR_DARKBLUE))
			reg.put(Resources.COLOR_DARKBLUE, new RGB(6, 45, 107));
		if (null == reg.get(Resources.COLOR_MAGENTA))
			reg.put(Resources.COLOR_MAGENTA, new RGB(147, 2, 22));
		if (null == reg.get(Resources.COLOR_GRASS))
			reg.put(Resources.COLOR_GRASS, new RGB(92, 114, 39));
		if (null == reg.get(Resources.COLOR_WARNBACK))
			reg.put(Resources.COLOR_WARNBACK, new RGB(255, 188, 196));
		if (null == reg.get(Resources.COLOR_HIGHLIGHT))
			reg.put(Resources.COLOR_HIGHLIGHT, new RGB(255, 250, 196));
	}

	/**
	 * Search for the given text in title, author and keyword
	 * 
	 * @param text
	 *            Keyword to search
	 */
	public void search(String text) {
		if (text == null || text.isEmpty()) {
			// Clear all searches
			for (BibEntry entry : model.getEntries()) {
				entry.setHighlight(false);
				table.refresh(entry);
			}
		} else {
			String keyword = text.toLowerCase();
			for (BibEntry entry : model.getEntries()) {
				boolean highlight = entry.getProperty(EntryType.title).toLowerCase().contains(keyword)
						|| entry.getProperty(EntryType.author).toLowerCase().contains(keyword)
						|| entry.getProperty(EntryType.keywords).toLowerCase().contains(keyword);
				entry.setHighlight(highlight);
				table.refresh(entry);
			}
		}
	}
}
