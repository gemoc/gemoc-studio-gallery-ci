package org.eclipse.gemoc.studio.gallery.tests;

import org.eclipse.amalgam.discovery.InstallableComponent;

public class InstallableComponentWrapper {
	InstallableComponent ic;

	public InstallableComponentWrapper(InstallableComponent ic) {
		super();
		this.ic = ic;
	}

	@Override
	public String toString() {
		return ""+ic.getName();
	}

	public InstallableComponent getIc() {
		return ic;
	}

	public void setIc(InstallableComponent ic) {
		this.ic = ic;
	}
	
	
}
