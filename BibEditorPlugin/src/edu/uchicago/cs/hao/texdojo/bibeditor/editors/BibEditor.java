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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.EditorPart;

import edu.uchicago.cs.hao.texdojo.bibeditor.Activator;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParseException;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;
import edu.uchicago.cs.hao.texdojo.bibeditor.preferences.PreferenceConstants;

public class BibEditor extends EditorPart implements PropertyChangeListener {

	private BibModel model;

	private EditorUI ui;

	public BibEditor() {
		super();
		ui = new EditorUI();
		ui.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == ui) {
			if ("dirty".equals(evt.getPropertyName())) {
				firePropertyChange(PROP_DIRTY);
			}
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		setContentDescription(input.getToolTipText());
		setTitleToolTip(input.getToolTipText());

		IPath path = ((IPathEditorInput) input).getPath();
		try {
			model = new BibParser().parse(new FileInputStream(path.toFile()));
			ui.setModel(model);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BibParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private ContextManager contextManager;

	@Override
	public void createPartControl(Composite parent) {
		ui.createUI(parent);
		// register the menu with the framework
		getSite().registerContextMenu(ui.getMenuManager(), ui.getTable());
		// make the viewer selection available
		getSite().setSelectionProvider(ui.getTable());

		contextManager = new ContextManager();

		getSite().getPage().addPartListener(contextManager);
	}

	@Override
	public void dispose() {
		super.dispose();
		ui.dispose();

		getSite().getPage().removePartListener(contextManager);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor pm) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		boolean preserveCase = prefs.getBoolean(PreferenceConstants.P_PRESERVE_CASE, false);

		ui.save(((IPathEditorInput) getEditorInput()).getPath().toFile(), preserveCase);
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isDirty() {
		return ui.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public BibModel getModel() {
		return model;
	}

	public EditorUI getUi() {
		return ui;
	}

	static class ContextManager extends PartListenerSupport {
		IContextActivation activation = null;

		public void activateContext() {
			IContextService contextService = (IContextService) PlatformUI.getWorkbench()
					.getService(IContextService.class);
			if (contextService != null)
				activation = contextService.activateContext(Constants.CONTEXT_ID);
		}

		public void deactivateContext() {
			IContextService contextService = (IContextService) PlatformUI.getWorkbench()
					.getService(IContextService.class);
			if (contextService != null && activation != null)
				contextService.deactivateContext(activation);
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			if (Constants.EDITOR_ID.equals(partRef.getId())) {
				activateContext();
			} else if (partRef.getPart(false) instanceof IEditorPart) {
				// Other Editor is activated
				deactivateContext();
			}
		}
	};
}