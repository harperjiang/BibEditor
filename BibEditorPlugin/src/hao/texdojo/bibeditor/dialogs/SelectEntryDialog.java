/*******************************************************************************
 * Copyright (c) 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package hao.texdojo.bibeditor.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import hao.texdojo.bibeditor.editors.EditorUI.BibEntryColumnLabelProvider;
import hao.texdojo.bibeditor.filemodel.BibEntry;

/**
 * @author Hao Jiang
 *
 */
public class SelectEntryDialog extends Dialog {

	private List<BibEntry> entries;
	
	private Object[] choice;

	private TableViewer table;

	public SelectEntryDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		container.setLayout(tableColumnLayout);

		table = new TableViewer(container, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		table.setContentProvider(new ArrayContentProvider());

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

		tableColumnLayout.setColumnData(typeColumn, new ColumnWeightData(5, 150, true));
		tableColumnLayout.setColumnData(titleColumn, new ColumnWeightData(50, 400, true));
		tableColumnLayout.setColumnData(yearColumn, new ColumnPixelData(50));
		tableColumnLayout.setColumnData(authorColumn, new ColumnWeightData(40, 400, true));

		table.setInput(entries);
		
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		table.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object selected = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (null != selected) {
					choice = new Object[] { selected };
					SelectEntryDialog.this.close();
				}
			}
		});

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select an entry");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 260);
	}

	@Override
	protected void okPressed() {
		// Obtain selection
		IStructuredSelection sel = table.getStructuredSelection();
		if (!sel.isEmpty()) {
			choice = table.getStructuredSelection().toArray();
		}
		super.okPressed();
	}

	public void setEntries(List<BibEntry> entries) {
		this.entries = entries;
	}

	public Object[] getChoice() {
		return choice;
	}

}
