package hao.texdojo.latexeditor.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;

public class PlatformUtil {

	public static IPath getOpenFile() {
		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		IPathEditorInput finput = (IPathEditorInput) input;
		IPath path = finput.getPath();
		return path;
	}

	public static IProject getOpenProject() {
		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput();
		return input.getAdapter(IFile.class).getProject();
	}
}
