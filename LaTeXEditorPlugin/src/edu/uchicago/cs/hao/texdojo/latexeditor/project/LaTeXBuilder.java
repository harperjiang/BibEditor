package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_BIBTEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COMPILE_DOC;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_LATEX_EXE_OPT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER_OPTION;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_TEMP_FILE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_USE_MAKE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_BIB_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_COMPILE_DOCUMENT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_LATEX_EXE_OPT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER_OPT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_TEMP_FILE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_USE_MAKE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;
import edu.uchicago.cs.hao.texdojo.latexeditor.inmem.DependencyMap;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.ArgNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.BeginNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.EndNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.InvokeNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.SpellChecker;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.Suggestion;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.aspell.ASpellChecker;

public class LaTeXBuilder extends IncrementalProjectBuilder {

	private Logger logger = LoggerFactory.getLogger(getClass());

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
			logger.error("Failed to add marker", e);
		}
	}

	private void addMarker(IFile file, String message, int severity, int offset, int length) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.CHAR_START, offset);
			marker.setAttribute(IMarker.CHAR_END, offset + length);

		} catch (CoreException e) {
			logger.error("Failed to add marker", e);
		}
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		// Load dependency if any exists
		try {
//			if (kind == FULL_BUILD) {
				fullBuild(monitor);
//			} else {
//				IFile config = getProject().getFile(".texdojo");
//				this.dependency.load(new FileReader(config.getLocation().toFile()));
//
//				IResourceDelta delta = getDelta(getProject());
//				if (delta == null) {
//					fullBuild(monitor);
//				} else {
//					incrementalBuild(delta, monitor);
//				}
//			}

			// Write dependency to file

			Writer writer = new FileWriter(getProject().getFile(".texdojo").getLocation().toFile());
			this.dependency.save(writer);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Error while building project", e.getMessage());
				}
			});

		}
		return null;
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
			throw new RuntimeException("Error on deleting markers", ce);
		}
	}

	private DependencyMap dependency = new DependencyMap();

	protected void fullBuild(final IProgressMonitor monitor) throws Exception {
		// Check to see whether to compile current file
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		boolean useMakefile = prefs.getBoolean(P_USE_MAKE, DEFAULT_USE_MAKE);
		boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

		if (useMakefile && getProject().getFile("Makefile").exists()) {
			compileWithMake();
			return;
		}

		if (!compileDoc) {
			compile(getProject().getFile(mainTex), monitor, true);
			return;
		}
		dependency = new DependencyMap();
		getProject().accept(new IResourceVisitor() {
			public boolean visit(IResource resource) {
				try {
					if (resource instanceof IFile && resource.getName().endsWith(".tex")) {
						scan(resource, monitor);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				// return true to continue visiting children.
				return true;
			}
		});
		dependency.roots().forEach(root -> {
			try {
				compile(getProject().getFile(root + ".tex"), monitor, true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {

		// Check to see whether to compile current file
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		boolean useMakefile = prefs.getBoolean(P_USE_MAKE, DEFAULT_USE_MAKE);
		boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

		if (useMakefile && getProject().getFile("Makefile").exists()) {
			compileWithMake();
			return;
		}

		// the visitor does the work.
		Set<String> affectedRoots = new HashSet<>();
		delta.accept(new IResourceDeltaVisitor() {
			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				try {
					IResource resource = delta.getResource();
					if (resource instanceof IFile && resource.getName().endsWith(".tex")) {
						String rname = resource.getProjectRelativePath().removeFileExtension().toString();
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
							// handle added resource
							scan(resource, monitor);
							affectedRoots.addAll(dependency.roots(rname));
							break;
						case IResourceDelta.REMOVED:
							// handle removed resource
							affectedRoots.addAll(dependency.roots(rname));
							dependency.remove(rname);
							break;
						case IResourceDelta.CHANGED:
							// handle changed resource
							affectedRoots.addAll(dependency.roots(rname));
							scan(resource, monitor);
							affectedRoots.addAll(dependency.roots(rname));
							break;
						}
					}
					// return true to continue visiting children.
					return true;
				} catch (Exception e) {
					// TODO Handle Error
					logger.error("Exception on build", e);
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
									"Error while scanning changes", e.getMessage());
						}
					});

					return true;
				}
			}
		});

		affectedRoots.forEach(root -> {
			try {
				if (!compileDoc) {
					if (root.equals(mainTex)) {
						compile(getProject().getFile(root + ".tex"), monitor, false);
					}
				} else
					compile(getProject().getFile(root + ".tex"), monitor, false);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void scan(IResource resource, IProgressMonitor monitor) throws Exception {

		monitor.beginTask("Scanning " + resource.getName(), 10);

		IFile inputFile = (IFile) resource;
		LaTeXModel model = LaTeXModel.parseFromFile(inputFile.getContents());

		monitor.worked(5);
		IPath folder = resource.getProjectRelativePath().removeLastSegments(1);
		String rname = resource.getProjectRelativePath().removeFileExtension().toString();
		dependency.unmark(rname);

		if (model.has("document")) {
			dependency.mark(rname);
		}

		model.find(node -> {
			if (node instanceof InvokeNode) {
				return !StringUtils.isEmpty(node.getContent())
						&& (node.getContent().equals("input") || node.getContent().equals("include"));
			}
			return false;
		}).forEach(node -> {
			InvokeNode in = (InvokeNode) node;
			ArgNode arg = (ArgNode) (in.getArgs().get(0));
			String includeName = arg.getContent().replaceAll("\\.tex$", "");

			dependency.include(rname, folder.append(includeName).toString());
		});

		model.find(node -> {
			if (node instanceof InvokeNode) {
				return !StringUtils.isEmpty(node.getContent()) && (node.getContent().equals("bibliography"));
			}
			return false;
		}).forEach(node -> {
			InvokeNode in = (InvokeNode) node;
			ArgNode arg = (ArgNode) (in.getArgs().get(0));
			String includeName = arg.getContent();

			dependency.ref(rname, folder.append(includeName).toString());
		});

		monitor.worked(5);
	}

	void compile(IResource resource, IProgressMonitor monitor, boolean cleanTemp) throws Exception {
		if (resource instanceof IFile) {
			IFile inputFile = (IFile) resource;

			// Check to see whether to compile current file
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			String tempFiles = prefs.get(P_TEMP_FILE, DEFAULT_TEMP_FILE);

			if (cleanTemp) {
				// Remove temporary files for the given file
				removeTempFiles(new File(inputFile.getLocationURI()), tempFiles);
			}

			String executable = prefs.get(P_LATEX_EXE, DEFAULT_LATEX_EXE);
			String opt = prefs.get(P_LATEX_EXE_OPT, DEFAULT_LATEX_EXE_OPT);
			String bibexe = prefs.get(P_BIBTEX_EXE, DEFAULT_BIB_EXE);

			SpellChecker schecker = getSpellChecker();

			for (String child : dependency
					.children(inputFile.getProjectRelativePath().removeFileExtension().toString())) {

				IFile childFile = getProject().getFile(child + ".tex");
				if (!childFile.exists()) {
					continue;
				}
				deleteMarkers(childFile);

				LaTeXModel cmodel = LaTeXModel.parseFromFile(childFile.getContents());
				// Make marks for dangling environments
				// Note this may not be an error as the begin and end may separate in different
				// files. Thus we just warn it
				List<LaTeXNode> danglingEnvs = cmodel.find(p -> {
					return (p instanceof BeginNode || p instanceof EndNode)
							&& (p.getParent() == null || !p.getParent().getContent().equals(p.getContent()));
				});
				danglingEnvs.forEach(d -> {
					addMarker(childFile, (d instanceof BeginNode) ? "Dangling begin" : "Dangling end", d.getLine() + 1,
							IMarker.SEVERITY_WARNING);
				});
				// If spell check is enabled
				spellCheck(schecker, childFile);
			}

			LaTeXCompiler.compile(this, executable, opt, bibexe, inputFile, LaTeXEditor.getConsole(), monitor);

			// Refresh generated pdf resource

			IFile pdfFile = getProject()
					.getFile(resource.getProjectRelativePath().removeFileExtension().addFileExtension("pdf"));
			pdfFile.refreshLocal(1, monitor);
		}
	}

	void compileWithMake() {
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Not Implemented",
				"Compiling with Makefile not implemented");
	}

	private SpellChecker getSpellChecker() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		String spellChecker = prefs.get(P_SPELLCHECKER, DEFAULT_SPELLCHECKER);
		String spellCheckerExe = prefs.get(P_SPELLCHECKER_EXE, DEFAULT_SPELLCHECKER_EXE);
		String spellCheckerOption = prefs.get(P_SPELLCHECKER_OPTION, DEFAULT_SPELLCHECKER_OPT);

		SpellChecker checker = null;
		switch (spellChecker) {
		case "aspell":
			checker = new ASpellChecker(spellCheckerExe, spellCheckerOption);
			break;
		default:
			break;
		}
		return checker;
	}

	private void spellCheck(SpellChecker checker, IFile file) {
		if (checker != null) {
			// Check all lines
			try (BufferedReader reader = new BufferedReader(new FileReader(file.getLocation().toFile()))) {
				String fileLine = null;
				int charCounter = 0;
				while ((fileLine = reader.readLine()) != null) {
					List<Suggestion> suggestions = checker.check(fileLine);
					for (Suggestion sug : suggestions) {
						String message = (sug.getSuggestions() == null) ? "Spell Check :" + sug.getOrigin()
								: MessageFormat.format("Spell Check : {0}{1}", sug.getOrigin(),
										Arrays.stream(sug.getSuggestions()).collect(Collectors.joining("\n")));
						addMarker(file, message, IMarker.SEVERITY_WARNING, charCounter + sug.getOffset(),
								sug.getOrigin().length());
					}
					charCounter += fileLine.length() + 1;
				}
			} catch (Exception e) {
				// Silently fails
				logger.error("Exception on spell check", e);
			}
		}
	}

	private void removeTempFiles(final File sourceFile, String tempFiles) {
		final String[] tfs = tempFiles.split(",");

		for (String tf : tfs) {
			File temp = new File(tf.replaceFirst("\\*", sourceFile.getAbsolutePath().replaceFirst("\\.tex$", "")));
			if (temp.exists())
				temp.delete();
		}
	}
}
