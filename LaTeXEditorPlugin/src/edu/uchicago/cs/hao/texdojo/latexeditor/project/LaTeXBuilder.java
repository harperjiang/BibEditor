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
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.ArgNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.InvokeNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXConstant;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;

public class LaTeXBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "edu.uchicago.cs.hao.texdojo.latexeditor.LaTeXBuilder";

	private static final String MARKER_TYPE = "edu.uchicago.cs.hao.texdojo.latexeditor.LaTeXProblem";

	private String DEPENDENCY = "dep";

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
		// Load dependency if any exists
		IFile config = getProject().getFile(".texdojo");
		if (config != null) {
			try {
				DependencyMap newmap = new DependencyMap();
				JsonElement configData = new JsonParser().parse(new FileReader(config.getFullPath().toFile()));

				JsonObject dep = configData.getAsJsonObject().get(DEPENDENCY).getAsJsonObject();
				dep.entrySet().forEach(entry -> {
					List<String> items = new ArrayList<>();
					entry.getValue().getAsJsonArray().forEach(item -> items.add(item.getAsString()));
					newmap.load(entry.getKey(), items);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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

	private DependencyMap dependency = new DependencyMap();

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		// Check to see whether to compile current file
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

		try {
			if (!compileDoc) {
				compile(getProject().getFile(mainTex), monitor);
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
						e.printStackTrace();
						// TODO Handle Error
					}
					// return true to continue visiting children.
					return true;
				}
			});
			dependency.roots().forEach(root -> {
				try {
					compile(getProject().getFile(root), monitor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {

		// Check to see whether to compile current file
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

		// the visitor does the work.
		Set<String> affectedRoots = new HashSet<>();
		delta.accept(new IResourceDeltaVisitor() {
			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				try {
					IResource resource = delta.getResource();
					if (resource instanceof IFile && resource.getName().endsWith(".tex")) {
						String rname = resource.getName().replaceFirst("\\.tex$", "");
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
					return true;
				}
			}
		});

		affectedRoots.forEach(root -> {
			try {
				if (!compileDoc) {
					if (root.equals(mainTex)) {
						compile(getProject().getFile(root), monitor);
					}
				} else
					compile(getProject().getFile(root), monitor);
			} catch (Exception e) {
				e.printStackTrace();
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
		String rname = resource.getProjectRelativePath().toString().replaceFirst("\\.tex$", "");
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
			dependency.include(rname, rname.replaceFirst(rname + "$", arg.getContent()));
		});

		monitor.worked(5);
	}

	void compile(IResource resource, IProgressMonitor monitor) throws Exception {
		if (resource instanceof IFile && resource.getName().endsWith(".tex")) {
			IFile inputFile = (IFile) resource;
			deleteMarkers(inputFile);

			// Check to see whether to compile current file
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
			String tempFiles = prefs.get(P_TEMP_FILE, DEFAULT_TEMP_FILE);

			String executable = prefs.get(P_LATEX_EXE, DEFAULT_LATEX_EXE);
			String bibexe = prefs.get(P_BIBTEX_EXE, DEFAULT_BIB_EXE);

			LaTeXModel model = LaTeXModel.parseFromFile(inputFile.getContents());

			// Detect whether the file contains an bib command
			LaTeXCompiler.compile(this, executable, bibexe, inputFile, LaTeXEditor.getConsole(),
					model.has(LaTeXConstant.EXTERNAL_BIB), monitor);

			// Remove temporary files under the same folder
			File parent = new File(inputFile.getLocationURI()).getParentFile();
			removeTempFiles(parent, tempFiles);
			// Refresh generated pdf resource

			IFile pdfFile = resource.getParent().getFile(new Path(resource.getName().replaceAll("tex$", "pdf")));
			pdfFile.refreshLocal(1, monitor);
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
