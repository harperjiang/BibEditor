package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_BIBTEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COMPILE_DOC;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_TEMP_FILE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_BIB_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_COMPILE_DOCUMENT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_TEMP_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.IDocument;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXConstant;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;

public class LaTeXBuilder extends IncrementalProjectBuilder {

	class LaTeXDeltaVisitor implements IResourceDeltaVisitor {

		private IProgressMonitor monitor;

		public LaTeXDeltaVisitor(IProgressMonitor monitor) {
			super();
			this.monitor = monitor;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			try {
				IResource resource = delta.getResource();
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					// handle added resource
					compile(resource, monitor);
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					break;
				case IResourceDelta.CHANGED:
					// handle changed resource
					compile(resource, monitor);
					break;
				}
				// return true to continue visiting children.
				return true;
			} catch (Exception e) {
				// TODO Handle Error
				return true;
			}
		}
	}

	class LaTeXResourceVisitor implements IResourceVisitor {

		private IProgressMonitor monitor;

		public LaTeXResourceVisitor(IProgressMonitor monitor) {
			super();
			this.monitor = monitor;
		}

		public boolean visit(IResource resource) {
			try {
				compile(resource, monitor);
			} catch (Exception e) {
				// TODO Handle Error
			}
			// return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "edu.uchicago.cs.hao.texdojo.latexeditor.LaTeXBuilder";

	private static final String MARKER_TYPE = "edu.uchicago.cs.hao.texdojo.latexeditor.LaTeXProblem";

	private void addMarker(IFile file, String message, int lineNumber, int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new LaTeXResourceVisitor(monitor));
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new LaTeXDeltaVisitor(monitor));
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void compile(IResource resource, IProgressMonitor monitor) throws Exception {
		if (resource instanceof IFile && resource.getName().endsWith(".tex")) {
			IFile inputFile = (IFile) resource;
			deleteMarkers(inputFile);

			// Check to see whether to compile current file
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

			String tempFiles = prefs.get(P_TEMP_FILE, DEFAULT_TEMP_FILE);

			boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
			String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

			// Parse the document
			IDocument document = new LaTeXDocumentProvider().getDocument(resource);
			LaTeXModel model = new LaTeXModel();
			model.init(document);

			if (compileDoc && !model.has("document")) {
				return;
			} else if (!mainTex.equals(inputFile.getName())) {
				return;
			}

			String executable = prefs.get(P_LATEX_EXE, DEFAULT_LATEX_EXE);
			String bibexe = prefs.get(P_BIBTEX_EXE, DEFAULT_BIB_EXE);

			// Detect whether the file contains an bib command
			LaTeXCompiler.compile(executable, bibexe, inputFile, LaTeXEditor.getConsole(),
					model.has(LaTeXConstant.EXTERNAL_BIB), monitor);

			// Remove temporary files under the same folder
			File parent = new File(inputFile.getLocationURI()).getParentFile();
			removeTempFiles(parent, tempFiles);
		}

	}

	private void removeTempFiles(final File dir, String tempFiles) {
		final String[] tfs = tempFiles.split(",");
		File[] candidates = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File d, String name) {
				if (!dir.equals(d))
					return false;
				for (String tf : tfs) {
					if (name.matches(tf.replace(".", "\\.").replace("*", ".*")))
						return true;
				}
				return false;
			}
		});
		for (File can : candidates) {
			can.delete();
		}
	}
}
