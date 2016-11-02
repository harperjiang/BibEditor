package edu.uchicago.cs.hao.bibeditor.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

import edu.uchicago.cs.hao.bibeditor.filemodel.BibEntry;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser.Token;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser.TokenType;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibProp;

public class EditorUI {

	private BibModel model;

	private TableViewer table;

	private StyledText editText;

	private Color brown;

	private Color darkBlue;

	private Color magenta;

	public void createUI(Composite parent) {
		brown = new Color(Display.getCurrent(), 147, 2, 22);
		darkBlue = new Color(Display.getCurrent(), 6, 45, 107);
		magenta = new Color(Display.getCurrent(), 66, 6, 14);

		SashForm form = new SashForm(parent, SWT.VERTICAL);
		form.setLayout(new FillLayout());

		createTable(form);

		createEditPanel(form);

		form.setWeights(new int[] { 70, 30 });

	}

	public void dispose() {
		brown.dispose();
		darkBlue.dispose();
		magenta.dispose();

		table = null;
		editText = null;
	}

	private void createTable(Composite form) {
		Composite tablePanel = new Composite(form, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tablePanel.setLayout(tableColumnLayout);

		table = new TableViewer(tablePanel, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setContentProvider(new ArrayContentProvider());

		TableColumn keyColumn = new TableColumn(table.getTable(), SWT.NONE);
		keyColumn.setText("cite_key");
		TableViewerColumn keyColumnViewer = new TableViewerColumn(table, keyColumn);
		keyColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(0));

		TableColumn typeColumn = new TableColumn(table.getTable(), SWT.NONE);
		typeColumn.setText("Type");
		TableViewerColumn typeColumnViewer = new TableViewerColumn(table, typeColumn);
		typeColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(1));

		TableColumn titleColumn = new TableColumn(table.getTable(), SWT.NONE);
		titleColumn.setText("Title");
		TableViewerColumn titleColumnViewer = new TableViewerColumn(table, titleColumn);
		titleColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(2));

		TableColumn yearColumn = new TableColumn(table.getTable(), SWT.NONE);
		yearColumn.setText("Year");
		TableViewerColumn yearColumnViewer = new TableViewerColumn(table, yearColumn);
		yearColumnViewer.setLabelProvider(new BibEntryColumnLabelProvider(3));

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
	}

	private void createEditPanel(Composite parent) {
		Composite editPanel = new Composite(parent, SWT.NONE);
		editPanel.setLayout(new FillLayout());
		editText = new StyledText(editPanel, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	private void updateTextContent(String content) {
		editText.setText(content);
		try {
			InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
			Iterable<Token> tokens = new BibParser().tokenize(in);
			tokens.forEach(new Consumer<Token>() {
				@Override
				public void accept(Token t) {
					switch (t.getType()) {
					case TokenType.KEY:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(), brown, null, SWT.BOLD));
						break;
					case TokenType.TYPE:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(), darkBlue, null, SWT.BOLD));
						break;
					case TokenType.PROP_KEY:
						editText.setStyleRange(new StyleRange(t.getFrom(), t.getLength(), magenta, null, SWT.BOLD));
						break;
					default:
						break;
					}
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public BibModel getModel() {
		return model;
	}

	public void setModel(BibModel model) {
		this.model = model;
	}

	private static class BibEntryColumnLabelProvider extends ColumnLabelProvider {

		private int index;

		public BibEntryColumnLabelProvider(int index) {
			super();
			this.index = index;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof BibEntry) {
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
					break;
				}
			}
			return super.getText(element);
		}
	}
}
