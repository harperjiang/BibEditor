/*******************************************************************************
 * Copyright (c) Oct 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.bibeditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Resource;

public class ResourcePool {

	List<Resource> pool = new ArrayList<Resource>();

	public void add(Resource... rs) {
		for (Resource r : rs)
			pool.add(r);
	}

	public void remove(Resource r) {
		pool.remove(r);
	}

	public void dispose() {
		for (Resource r : pool) {
			if (null != r) {
				r.dispose();
			}
		}
	}
}
