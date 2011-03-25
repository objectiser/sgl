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
package org.scribble.lang.eclipse;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.scribble.common.logging.Journal;
import org.scribble.lang.eclipse.osgi.SLClasspathContainer;
import org.scribble.lang.generator.Generator;
import org.scribble.lang.model.Actor;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.parser.LanguageParser;
import org.scribble.lang.parser.antlr.ANTLRLanguageParser;
import org.scribble.lang.projection.LanguageProjectorImpl;
import org.scribble.lang.projection.Projector;

public class JavaCodeGenerator {

	private static final String JUNIT4_PATH = "org.eclipse.jdt.junit.JUNIT_CONTAINER/4";
	private LanguageParser m_parser=null;
	
	/**
	 * This method sets the language parser.
	 * 
	 * @param parser The parser
	 */
	public void setLanguageParser(LanguageParser parser) {
		m_parser = parser;
	}
	
	/**
	 * This method generates the Java code associated with the supplied
	 * resource, containing a Scribble Language Model, into the containing
	 * Java project.
	 * 
	 * @param res The resource
	 */
	public void generate(IResource res) {
		
		// Get Java project
		IJavaProject jproject=JavaCore.create(res.getProject()); 
		
		if (jproject != null && res instanceof IFile) {
			
			// Get language model
			Journal j=new Journal() {

				public void error(String arg0, Map<String, Serializable> arg1) {
					report(arg0);
				}

				public void info(String arg0, Map<String, Serializable> arg1) {
					report(arg0);
				}

				public void warning(String arg0, Map<String, Serializable> arg1) {
					report(arg0);
				}

				protected void report(String mesg) {
					System.out.println(">> "+mesg);
				}
			};
			
			//ANTLRLanguageParser parser=new ANTLRLanguageParser();
			LanguageModel model=null;
			
			try {
				java.io.InputStream is=((IFile)res).getContents();
			
				model = m_parser.parse(is, j);
			
				is.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (model != null) {
				
				Generator generator=new Generator();
				IFolder srcFolder=null;
				boolean f_containerRegistered=false;
				String serviceComponent=null;
				
				try {
					IClasspathEntry[] curclspath=jproject.getRawClasspath();
					
					for (int i=0; curclspath != null &&
								i < curclspath.length; i++) {
						
						if (curclspath[i].getEntryKind() == IClasspathEntry.CPE_SOURCE &&
								srcFolder == null) {
							srcFolder = ResourcesPlugin.getWorkspace().
									getRoot().getFolder(curclspath[i].getPath());
						} else if (curclspath[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
								curclspath[i].getPath().toString().equals(
											SLClasspathContainer.CONTAINER_PATH)) {
							f_containerRegistered = true;
						}
					}

					// Check if container needs to be added to classpath
					if (f_containerRegistered == false) {
						int len=jproject.getRawClasspath().length;
						IClasspathEntry[] entries=new IClasspathEntry[len+2];
						
						for (int i=0; i < len; i++) {
							entries[i] = jproject.getRawClasspath()[i];
						}
						
						entries[len] = JavaCore.newContainerEntry(new Path(SLClasspathContainer.CONTAINER_PATH));
						entries[len+1] = JavaCore.newContainerEntry(new Path(JUNIT4_PATH));
						
						jproject.setRawClasspath(entries, new org.eclipse.core.runtime.NullProgressMonitor());
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				for (Actor actor : model.getLocalActors()) {
					Projector projector=new LanguageProjectorImpl();
					LanguageModel local=projector.project(model, actor.getName(), j);
					
					if (srcFolder != null) {
						try {
							
							// Write scribble language generated Java code
							IPath path=new Path(local.getDeclaration().getFullName().replace('.', java.io.File.separatorChar)+
									"_"+actor.getName()+".java");
						
							IFile file=srcFolder.getFile(path);
							
							IFolder parent=(IFolder)file.getParent();
							
							create(parent);
							
							String processText=generator.generateProcess(local, actor);
							
							java.io.InputStream is=new java.io.ByteArrayInputStream(processText.getBytes());
							
							if (file.exists()) {
								file.delete(true, new org.eclipse.core.runtime.NullProgressMonitor());
							}
							
							file.create(is, true,
									new org.eclipse.core.runtime.NullProgressMonitor());
							
							file.setDerived(true, new org.eclipse.core.runtime.NullProgressMonitor());
							
							is.close();
							
							// Create the factory class
							path=new Path(local.getDeclaration().getFullName().replace('.', java.io.File.separatorChar)+
									"_"+actor.getName()+"_factory.java");
						
							file=srcFolder.getFile(path);
							
							String processFactoryText=generator.generateProcessFactory(local, actor);
							
							is = new java.io.ByteArrayInputStream(processFactoryText.getBytes());
							
							if (file.exists()) {
								file.delete(true, new org.eclipse.core.runtime.NullProgressMonitor());
							}
							
							file.create(is, true,
									new org.eclipse.core.runtime.NullProgressMonitor());
							
							file.setDerived(true, new org.eclipse.core.runtime.NullProgressMonitor());
							
							is.close();
							
							// Create the test class
							path=new Path(local.getDeclaration().getFullName().replace('.', java.io.File.separatorChar)+
									"_"+actor.getName()+"_test.java");
						
							file=srcFolder.getFile(path);
							
							if (file.exists() == false) {
								String processTestText=generator.generateProcessTest(local, actor);
								
								is = new java.io.ByteArrayInputStream(processTestText.getBytes());
								
								if (file.exists()) {
									file.delete(true, new org.eclipse.core.runtime.NullProgressMonitor());
								}
								
								file.create(is, true,
										new org.eclipse.core.runtime.NullProgressMonitor());
								
								is.close();
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					// Check if manifest exists
					IFile manifestFile=res.getProject().getFile("META-INF/MANIFEST.MF");
					
					if (manifestFile.exists()) {						
						try {
							// Generate OSGi descriptor
							String osgiDescriptorFileName="OSGI-INF/"+
									model.getDeclaration().getLocalName()+"-"+actor.getName()+".xml";
							
							IFile osgiDescriptorFile=res.getProject().getFile(osgiDescriptorFileName);
							
							IFolder parent=(IFolder)osgiDescriptorFile.getParent();							
							create(parent);

							String osgiDescriptorText=generator.generateOSGiDescriptor(local, actor);
							
							java.io.InputStream is = new java.io.ByteArrayInputStream(osgiDescriptorText.getBytes());
							
							if (osgiDescriptorFile.exists()) {
								osgiDescriptorFile.delete(true, new org.eclipse.core.runtime.NullProgressMonitor());
							}
							
							osgiDescriptorFile.create(is, true,
									new org.eclipse.core.runtime.NullProgressMonitor());
							
							osgiDescriptorFile.setDerived(true, new org.eclipse.core.runtime.NullProgressMonitor());
							
							is.close();
							
							if (serviceComponent == null) {
								serviceComponent = osgiDescriptorFileName;
							} else {
								serviceComponent += ", "+osgiDescriptorFileName;
							}
							
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				// Check if manifest exists
				IFile manifestFile=res.getProject().getFile("META-INF/MANIFEST.MF");
				
				if (manifestFile.exists()) {
					// Update dependencies based on the model
					java.util.jar.Manifest manifest=new java.util.jar.Manifest();
					
					try {
						java.io.InputStream is=manifestFile.getContents();
						
						manifest.read(is);
						
						is.close();
						
						generator.generateManifest(model, manifest, serviceComponent);
						
						java.io.ByteArrayOutputStream os=new java.io.ByteArrayOutputStream();
						
						manifest.write(os);
						
						os.close();
						
						byte[] b=os.toByteArray();

						// Write back the new manifest
						is = new java.io.ByteArrayInputStream(b);
						
						manifestFile.setContents(is, true, true,
								new org.eclipse.core.runtime.NullProgressMonitor());
						
						is.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	protected void create(IFolder folder) throws Exception {
		
		if (folder != null && folder.exists() == false) {
			
			if (folder.getParent() instanceof IFolder) {
				create((IFolder)folder.getParent());
			}
			
			folder.create(true, true,
					new org.eclipse.core.runtime.NullProgressMonitor());
		}
	}
}
