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

package edu.uchicago.cs.hao.texdojo.latexeditor.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

/**
 * @author Hao Jiang
 *
 */
public class PreferenceChangeMonitor implements IPreferenceChangeListener {

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (PreferenceConstants.P_COLOR_COMMAND.equals(event.getKey())) {

		}
		if (PreferenceConstants.P_COLOR_ARG.equals(event.getKey())) {

		}
		if (PreferenceConstants.P_COLOR_OPTION.equals(event.getKey())) {

		}
		if (PreferenceConstants.P_COLOR_COMMENT.equals(event.getKey())) {

		}

		return;
	}

}
