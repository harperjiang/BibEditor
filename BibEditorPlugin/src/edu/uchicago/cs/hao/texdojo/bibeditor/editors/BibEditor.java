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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.uchicago.cs.hao.texdojo.bibeditor.Activator;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;
import edu.uchicago.cs.hao.texdojo.bibeditor.preferences.PreferenceConstants;

public class BibEditor extends EditorPart implements PropertyChangeListener {

	private BibModel model;

	private EditorUI ui;

	private IResourceChangeListener resMonitor;

	public BibEditor() {
		super();
		ui = new EditorUI();
		ui.addPropertyChangeListener(this);
		resMonitor = new ResourceChangeListener();
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

		refresh();

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resMonitor,
				IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
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
		contextManager.deactivateContext();
		getSite().getPage().removePartListener(contextManager);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resMonitor);
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
	
	protected void refresh() {
		IPath path = ((IPathEditorInput)getEditorInput()).getPath();
		try {
			model = new BibParser().parse(new FileInputStream(path.toFile()));
			ui.setModel(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
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

	class ResourceChangeListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {

					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						if (delta.getResource().getFullPath().equals(BibEditor.this.getEditorInput())) {
							// File Changed
							refresh();
						}
						return false;
					}

				});
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
