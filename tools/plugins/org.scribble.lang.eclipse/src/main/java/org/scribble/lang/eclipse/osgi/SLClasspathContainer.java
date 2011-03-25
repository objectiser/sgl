/*
 * Copyright 2010 scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.lang.eclipse.osgi;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class SLClasspathContainer implements org.eclipse.jdt.core.IClasspathContainer {

	public static String CONTAINER_PATH="org.scribble.lang.SCRIBBLE_LANGUAGE_CONTAINER";
	
	private IPath m_containerPath=null;
	private IJavaProject m_project=null;
	
	public SLClasspathContainer(IPath containerPath, IJavaProject project) {
		m_containerPath = containerPath;
		m_project = project;
	}
	
	protected IClasspathEntry getClasspathEntry(String bundleName) {	
		Bundle bundle=org.eclipse.core.runtime.Platform.getBundle(bundleName);
		URL baseurl = bundle.getEntry("/");
		
		try {
			baseurl = org.eclipse.core.runtime.Platform.resolve(baseurl);
		} catch(Exception e) {
			e.printStackTrace();
		}

		String base=baseurl.getFile();
		java.io.File f=new java.io.File(base);
		
		base = f.getPath().replace('\\', '/');			

		if (f.isDirectory()) {
			base += java.io.File.separatorChar+"bin";
		}
		
		IClasspathEntry path=JavaCore.newLibraryEntry(new Path(base), null, null);
		
		return(path);
	}
	
	public IClasspathEntry[] getClasspathEntries() {
		
		IClasspathEntry path1=getClasspathEntry("org.scribble.runtime");
		IClasspathEntry path2=getClasspathEntry("org.scribble.runtime.manager.inmemory");
		IClasspathEntry path3=getClasspathEntry("org.scribble.runtime.registry.inmemory");
		IClasspathEntry path4=getClasspathEntry("org.scribble.runtime.testing");
		
		return(new IClasspathEntry[]{path1, path2, path3, path4});
	}

	public String getDescription() {
		return("Scribble Language Runtime");
	}

	public int getKind() {
		return(org.eclipse.jdt.core.IClasspathContainer.K_APPLICATION);
	}

	public IPath getPath() {
		return(m_containerPath);
	}

}
