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
