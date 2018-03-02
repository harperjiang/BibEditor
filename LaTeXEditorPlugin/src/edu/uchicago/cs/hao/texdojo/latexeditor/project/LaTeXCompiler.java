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

package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.IOConsole;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXCompiler {

	public static synchronized void compile(IncrementalProjectBuilder builder, String latexExe, String bibExe,
			IFile inputFile, IOConsole console, boolean runbib, IProgressMonitor monitor) {
		console.clearConsole();

		OutputStream output = console.newOutputStream();

		File parent = new File(inputFile.getLocationURI()).getParentFile();

		String inputFileName = inputFile.getName().replaceAll("\\.tex$", "");

		ProcessBuilder latexBuilder = new ProcessBuilder().command(latexExe, "-interaction=nonstopmode", inputFileName)
				.directory(parent).redirectErrorStream(true);

		ProcessBuilder bibBuilder = new ProcessBuilder().command(bibExe, inputFileName).directory(parent)
				.redirectErrorStream(true);

		int totalWork = runbib ? 100 : 25;
		try {
			String message = "Compiling " + inputFile.getName();
			monitor.beginTask(message, totalWork);
			monitor.subTask(message + " - first pass");
			
			connect(latexBuilder.start(), console, builder, monitor);

			monitor.worked(25);

			if (monitor.isCanceled())
				return;

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

				monitor.subTask(message + " - final pass");
				connect(latexBuilder.start(), console, builder, monitor);
				monitor.worked(25);

				if (monitor.isCanceled())
					return;
			}

			monitor.done();
		} catch (Exception e) {
			e.printStackTrace(new PrintStream(output));
		}
		return;
	}

	static final long TIMEOUT = 120000L;

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
			if (monitor.isCanceled() || builder.isInterrupted() || consumed >= TIMEOUT) {
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
}
