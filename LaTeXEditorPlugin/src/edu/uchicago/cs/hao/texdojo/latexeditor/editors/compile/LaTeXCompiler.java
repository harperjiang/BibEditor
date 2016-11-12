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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.ui.console.MessageConsole;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXCompiler {

	private static Queue<CompileTask> waitQueue = new ConcurrentLinkedQueue<CompileTask>();

	private static Thread thread = new CompileThread();

	static {
		thread.start();
	}

	public static synchronized void compile(String latexExe, String bibExe, File inputFile, MessageConsole console,
			boolean runbib) {
		// Will not insert duplicate tasks for the same file
		for (CompileTask task : waitQueue) {
			if (task.inputFile.equals(inputFile)) {
				return;
			}
		}
		waitQueue.offer(new CompileTask(latexExe, bibExe, inputFile, console, runbib));
	}

	private static class CompileTask {

		String latexExe;

		String bibExe;

		File inputFile;

		org.eclipse.ui.console.MessageConsole console;

		boolean runbib;

		/**
		 * @param latexExe
		 * @param bibExe
		 * @param inputFile
		 * @param output
		 */
		public CompileTask(String latexExe, String bibExe, File inputFile, MessageConsole console, boolean runbib) {
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

		public void execute() {
			console.clearConsole();
			OutputStream output = console.newOutputStream();
			ProcessBuilder latexBuilder = new ProcessBuilder().command(latexExe, inputFile.getName())
					.directory(inputFile.getParentFile()).redirectErrorStream(true);
			ProcessBuilder bibBuilder = new ProcessBuilder().command(bibExe, inputFile.getName())
					.directory(inputFile.getParentFile()).redirectErrorStream(true);
			try {
				Process process = latexBuilder.start();
				process.waitFor();
				copyStream(process.getInputStream(), output);
				if (runbib) {
					process = bibBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);
					process = latexBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);
					process = latexBuilder.start();
					process.waitFor();
					copyStream(process.getInputStream(), output);
				}
			} catch (Exception e) {
				e.printStackTrace(new PrintStream(output));
			}
		}
	}

	private static class CompileThread extends Thread {

		@Override
		public void run() {
			while (true) {
				CompileTask task = null;
				try {
					LaTeXCompiler.class.wait();
					if (!waitQueue.isEmpty()) {
						task = waitQueue.poll();
					}
				} catch (InterruptedException e) {

				} finally {
					LaTeXCompiler.class.notify();
				}

				if (task != null) {
					task.execute();
				}

				// Sleep for a while
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
			}
		}
	}
}
