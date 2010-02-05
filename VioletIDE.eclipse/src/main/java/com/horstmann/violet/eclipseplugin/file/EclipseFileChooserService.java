package com.horstmann.violet.eclipseplugin.file;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.chooser.IFileChooserService;
import com.horstmann.violet.framework.file.chooser.IFileOpener;
import com.horstmann.violet.framework.file.chooser.IFileSaver;
import com.horstmann.violet.framework.file.naming.ExtensionFilter;

public class EclipseFileChooserService implements IFileChooserService {

	private org.eclipse.core.resources.IFile eclipseFile;
	
	private EclipseFileSaver eclipseFileSaver;
	
	private IFileOpener eclipseFileOpener;
	
	@Override
	public IFileOpener getFileOpener() throws IOException {
		throw new RuntimeException("Should never happen from Eclipse");
	}

	@Override
	public IFileOpener getFileOpener(IFile file) throws IOException {
		if (this.eclipseFileOpener == null) {
			if (this.eclipseFile == null) throw new RuntimeException("Eclipse file must be set before EclipseFileOpener creation");
			this.eclipseFileOpener = new EclipseFileOpener(this.eclipseFile);
		}
		return this.eclipseFileOpener;
	}

	@Override
	public IFileSaver getFileSaver(ExtensionFilter... extensions) throws IOException {
		throw new RuntimeException("Should never happen from Eclipse");
	}

	@Override
	public IFileSaver getFileSaver(IFile file) throws IOException {
		return this.getFileSaver();
	}


	private EclipseFileSaver getFileSaver() {
		if (this.eclipseFileSaver == null) {
			if (this.eclipseFile == null) throw new RuntimeException("Eclipse file must be set before EclipseFileSaver creation");
			this.eclipseFileSaver = new EclipseFileSaver(this.eclipseFile);
		}
		return this.eclipseFileSaver;
	}
	
	
	public void setEclipseFile(org.eclipse.core.resources.IFile eclipseFile) {
    	this.eclipseFile = eclipseFile;
    }
	
	

	public void changeProgressMonitor(IProgressMonitor progressMonitor) {
    	getFileSaver().setProgressMonitor(progressMonitor);
    }

	@Override
	public boolean isWebStart() {
		return false;
	}

}