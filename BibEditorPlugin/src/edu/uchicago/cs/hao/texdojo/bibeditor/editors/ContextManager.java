package edu.uchicago.cs.hao.texdojo.bibeditor.editors;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

public class ContextManager extends PartListenerSupport {
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
		if (contextService != null && activation != null) {
			// Use a dialog to prompt the switch of context
			// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
			// "Prompt", "Switching Context");
			contextService.deactivateContext(activation);
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		if (Constants.EDITOR_ID.equals(partRef.getId())) {
			activateContext();
		} else if (partRef.getPart(false) instanceof IEditorPart) {
			// Other Editor is activated
			deactivateContext();
		}
	}
};