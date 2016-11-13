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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.compile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.MessageConsole;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXCompiler {

	public static synchronized void compile(String latexExe, String bibExe, File inputFile, MessageConsole console,
			boolean runbib) {
		Job job = new CompileJob(latexExe, bibExe, inputFile, console, runbib);
		job.schedule();
	}

	public static class CompileJob extends Job {

		String latexExe;

		String bibExe;

		File inputFile;

		org.eclipse.ui.console.MessageConsole console;

		boolean runbib;

		boolean cancelled = false;

		/**
		 * @param latexExe
		 * @param bibExe
		 * @param inputFile
		 * @param output
		 */
		public CompileJob(String latexExe, String bibExe, File inputFile, MessageConsole console, boolean runbib) {
			super("TeXDojo Compile Job");
			this.latexExe = latexExe;
			this.bibExe = bibExe;
			this.inputFile = inputFile;
			this.console = console;
			this.runbib = runbib;
		}

		private void copyStream(InputStream in, OutputStream output) throws IOException {
			byte[] buffer = new byte[1000];
			int readcount = 0;
			while ((readcount = in.read(buffer)) == buffer.length) {
				output.write(buffer);
			}
			output.write(buffer, 0, readcount);
		}

		public void run() {
			run(new NullProgressMonitor());
		}

		@Override
		protected void canceling() {
			super.canceling();
			cancelled = true;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			console.clearConsole();

			OutputStream output = console.newOutputStream();
			ProcessBuilder latexBuilder = new ProcessBuilder().command(latexExe, inputFile.getName())
					.directory(inputFile.getParentFile()).redirectErrorStream(true);
			ProcessBuilder bibBuilder = new ProcessBuilder().command(bibExe, inputFile.getName())
					.directory(inputFile.getParentFile()).redirectErrorStream(true);
			try {

				monitor.beginTask("Compiling LaTeX Files", 80);
				monitor.subTask("Invoking LaTeX");
				Process process = latexBuilder.start();
				process.waitFor();
				copyStream(process.getInputStream(), output);

				monitor.worked(20);

				if (cancelled)
					return Status.CANCEL_STATUS;

				if (runbib) {
					monitor.subTask("Invoking BibTeX");

					process = bibBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);

					monitor.worked(20);

					if (cancelled)
						return Status.CANCEL_STATUS;

					monitor.subTask("Invoking LaTeX");

					process = latexBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);

					monitor.worked(20);

					if (cancelled)
						return Status.CANCEL_STATUS;

					monitor.subTask("Invoking LaTeX");

					process = latexBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);

					monitor.worked(20);

					if (cancelled)
						return Status.CANCEL_STATUS;
				}

				// Remove temporary files

				monitor.done();
			} catch (Exception e) {
				e.printStackTrace(new PrintStream(output));
			}
			return Status.OK_STATUS;
		}
	}

}
