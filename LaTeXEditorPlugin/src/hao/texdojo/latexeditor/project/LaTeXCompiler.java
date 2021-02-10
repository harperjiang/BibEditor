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

package hao.texdojo.latexeditor.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;

import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.LaTeXModel;
import hao.texdojo.latexeditor.model.LaTeXNode;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXCompiler {

	public static synchronized void compile(IncrementalProjectBuilder builder, String latexExe, String latexOpt,
			String bibExe, IFile inputFile, IOConsole console, IProgressMonitor monitor) {
		console.clearConsole();

		OutputStream output = console.newOutputStream();
		File input = new File(inputFile.getLocationURI());
		File parent = input.getParentFile();

		String inputFileName = inputFile.getName().replaceAll("\\.tex$", "");

		ProcessBuilder latexBuilder = new ProcessBuilder()
				.command(latexExe, "-interaction=nonstopmode", latexOpt, inputFileName).directory(parent)
				.redirectErrorStream(true);

		ProcessBuilder bibBuilder = new ProcessBuilder().command(bibExe, inputFileName).directory(parent)
				.redirectErrorStream(true);

		int totalWork = 100;
		try {
			String message = "Compiling " + inputFile.getName();
			monitor.beginTask(message, totalWork);
			monitor.subTask(message + " - first pass");

			connect(latexBuilder.start(), console, builder, monitor);

			monitor.worked(25);

			if (monitor.isCanceled())
				return;

			boolean runbib = checkAuxAndBbl(input);

			if (runbib) {
				monitor.subTask(message + " - invoking BibTeX");

				connect(bibBuilder.start(), console, builder, monitor);
				monitor.worked(25);

				if (monitor.isCanceled())
					return;
				monitor.subTask(message + " - second pass");
				connect(latexBuilder.start(), console, builder, monitor);
				monitor.worked(25);

				if (monitor.isCanceled())
					return;
			}

			monitor.subTask(message + " - final pass");
			connect(latexBuilder.start(), console, builder, monitor);
			monitor.worked(25);

			if (monitor.isCanceled())
				return;

			monitor.done();
		} catch (Exception e) {
			e.printStackTrace(new PrintStream(output));
		}
		return;
	}

	static final long TIMEOUT = 300000L;

	static void connect(Process p, IOConsole console, IncrementalProjectBuilder builder, IProgressMonitor monitor)
			throws IOException, InterruptedException {
		OutputStream output = console.newOutputStream();

		long consumed = 0;
		long checkInterval = 500L;
		while (p.isAlive()) {
			copyStream(p.getInputStream(), output);
			copyStream(console.getInputStream(), p.getOutputStream());
			Thread.sleep(checkInterval);
			consumed += checkInterval;
			if (monitor.isCanceled() || builder.isInterrupted()) {
				// Do not use timeout for now
				p.destroy();
				p.waitFor();
			}
		}
		copyStream(p.getInputStream(), output);
		copyStream(console.getInputStream(), p.getOutputStream());
		output.close();
	}

	static void copyStream(InputStream in, OutputStream output) throws IOException {
		try {
			if (in.available() <= 0)
				return;
		} catch (IOException e) {
			// It is closed
			return;
		}
		byte[] buffer = new byte[1000];
		int readcount = 0;
		while ((readcount = in.read(buffer)) == buffer.length) {
			output.write(buffer);
		}
		if (readcount != -1) {
			output.write(buffer, 0, readcount);
			output.flush();
		}
	}

	// Check if the entries in aux and bbl match
	static boolean checkAuxAndBbl(File input) {
		String auxFile = input.getAbsolutePath().replaceFirst("tex$", "aux");
		String bblFile = input.getAbsolutePath().replaceFirst("tex$", "bbl");

		try {
			if (!Files.exists(Paths.get(auxFile))) {
				throw new RuntimeException();
			}
			// Does aux contains bibcite command
			LaTeXModel auxModel = LaTeXModel.parseFromFile(new FileInputStream(auxFile));
			// No citation, no need to invoke bibtex
			if (!auxModel.has("citation"))
				return false;
			// No bbl file, need to run bibtex
			if (!Files.exists(Paths.get(bblFile)))
				return true;
			// Check if all citations are included in bbl
			LaTeXModel bblModel = LaTeXModel.parseFromFile(new FileInputStream(bblFile));
			Set<String> dict = bblModel.find(n -> n.has("bibitem")).stream()
					.map(n -> ((InvokeNode) n).getArgs().get(0).getContent()).collect(Collectors.toSet());
			return auxModel.find((LaTeXNode n) -> n.has("citation")).stream().anyMatch((LaTeXNode n) -> {
				InvokeNode ivk = (InvokeNode) n;
				String[] entries = ivk.getArgs().get(0).getContent().split(",");
				for (String entry : entries) {
					if (!dict.contains(entry))
						return true;
				}
				return false;
			});
		} catch (Exception e) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Error while checking aux and bbl", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
