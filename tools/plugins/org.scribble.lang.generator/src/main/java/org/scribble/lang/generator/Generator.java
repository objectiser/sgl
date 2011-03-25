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
package org.scribble.lang.generator;

import java.io.Serializable;
import java.util.Map;
import java.util.jar.Attributes;

import javassist.CtClass;

import org.scribble.common.logging.Journal;
import org.scribble.lang.model.Actor;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.model.Requirement;
import org.scribble.lang.model.RequirementParameter;
import org.scribble.lang.parser.antlr.ANTLRLanguageParser;
import org.scribble.lang.projection.LanguageProjectorImpl;
import org.scribble.lang.projection.Projector;
import org.scribble.runtime.messaging.Endpoint;

public class Generator {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage: Parser <filename>");
			System.exit(1);
		}
		
		try {
			java.io.FileInputStream is=new java.io.FileInputStream(args[0]);
			
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
			
			ANTLRLanguageParser parser=new ANTLRLanguageParser();
			
			LanguageModel model=parser.parse(is, j);
			
			System.out.println("Global Model="+model);
			
			java.util.List<Actor> actors=model.getLocalActors();
			
			for (Actor actor : actors) {
				Projector projector=new LanguageProjectorImpl();
				LanguageModel local=projector.project(model, actor.getName(), j);
				
				if (local == null) {
					System.err.println("No local model for: "+actor.getName());
				} else {
					System.out.println("Local Model for "+actor.getName()+"="+local);

					Generator generator=new Generator();
				
					System.out.println("PROCESS="+generator.generateProcess(local, actor));
				}
			}
						
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String generateProcess(LanguageModel model, Actor actor) {
		JavaTextGeneratorVisitor visitor=new JavaTextGeneratorVisitor(model, actor);
		
		model.getLanguageUnit().visit(visitor);
		
		return(visitor.toText());
	}
	
	public String generateProcessFactory(LanguageModel model, Actor actor) {
		StringBuffer ret=new StringBuffer();
		
		if (model.getDeclaration() != null) {
			ret.append("package "+model.getDeclaration().getScope()+";\r\n");
		}
		
		ret.append("import org.scribble.runtime.*;\r\n");
		ret.append("import org.scribble.runtime.messaging.*;\r\n");
		
		String className=model.getDeclaration().getLocalName()+"_"+
						actor.getName();

		ret.append("public class "+className+"_factory implements ProcessFactory {\r\n\r\n");

		ret.append("\tprivate static "+className+"_factory m_instance=new "+className+"_factory();\r\n\r\n");
		
		ret.append("\tpublic static "+className+"_factory instance() {\r\n");
		ret.append("\t\treturn(m_instance);\r\n");
		ret.append("\t}\r\n");
		
		ret.append("\tpublic org.scribble.runtime.Process createProcess() {\r\n");
		ret.append("\t\treturn(new "+className+
				"(new Endpoint(getName().toString(),java.util.UUID.randomUUID())));\r\n");
		ret.append("\t}\r\n\r\n");

		ret.append("\tpublic String getName() {\r\n");
		ret.append("\t\treturn "+className+".PROCESS_NAME;\r\n");
		ret.append("\t}\r\n\r\n");

		ret.append("\tpublic java.util.Properties getProperties() {\r\n");
		ret.append("\t\treturn(null);\r\n");
		ret.append("\t}\r\n\r\n");

		ret.append("\tpublic java.util.Set<String> getImplements() {\r\n");
		ret.append("\t\treturn(java.util.Collections.EMPTY_SET);\r\n");
		ret.append("\t}\r\n\r\n");

		ret.append("\tpublic Endpoint getEndpoint() {\r\n");
		ret.append("\t\treturn(new Endpoint(getName().toString(),null));\r\n");
		ret.append("\t}\r\n");
		ret.append("}\r\n");

		return(ret.toString());
	}
	
	public String generateProcessTest(LanguageModel model, Actor actor) {
		StringBuffer ret=new StringBuffer();
		
		if (model.getDeclaration() != null) {
			ret.append("package "+model.getDeclaration().getScope()+";\r\n");
		}
		
		ret.append("import org.scribble.runtime.*;\r\n");
		ret.append("import org.scribble.runtime.messaging.*;\r\n");
		ret.append("import org.scribble.runtime.testing.*;\r\n");
		ret.append("import static org.junit.Assert.*;\r\n");
		
		String className=model.getDeclaration().getLocalName()+"_"+
						actor.getName();

		ret.append("public class "+className+"_test {\r\n\r\n");

		ret.append("\t@org.junit.Test\r\n");
		ret.append("\tpublic void test() {\r\n");
		ret.append("\t\tTestClient testClient=new TestClient(new "+className+"_factory());\r\n");
		
		// Added invoked process factories
		java.util.List<Actor> declaredActors=model.getDeclaredActors();
		declaredActors.remove(actor);
		
		for (Actor act : declaredActors) {
			if (act.isLocal()) {
				ret.append("\t\ttestClient.addInvokedProcess(new "+
						model.getDeclaration().getLocalName()+"_"+
						act.getName()+"_factory());\r\n");
			} else {
				ret.append("\t\t//testClient.addInvokedProcess(<for external actor '"+
							act.getName()+"'>);\r\n");
			}
		}
		
		ret.append("\r\n\t\ttestClient.setMessageListener(new MessageListener() {\r\n");
		ret.append("\t\t\tpublic void onMessage(Message msg) {\r\n");
		ret.append("\t\t\t\t// TODO: Handle the message\r\n");
		ret.append("\t\t\t}\r\n");
		ret.append("\t\t});\r\n\r\n");
		
		ret.append("\t\tjava.io.Serializable msg=null; // TODO: Set to first message\r\n");
		ret.append("\t\ttestClient.send(msg);\r\n\r\n");
		ret.append("\t\t// On completion of test, check that the process under test has completed\r\n");
		ret.append("\t\tif (testClient.completed() == false) {\r\n");
		ret.append("\t\t\tfail(\"Process '"+className+"' did not complete\");\r\n");
		ret.append("\t\t}\r\n");
		ret.append("\t}\r\n\r\n");

		ret.append("}\r\n");

		return(ret.toString());
	}
	
	public void generateManifest(LanguageModel model, java.util.jar.Manifest manifest,
							String serviceComponent) {
		
		StringBuffer requires=new StringBuffer();
		
		requires.append("org.scribble.runtime");
		
		for (Requirement req : model.getRequirements()) {
			if (req.getType().equals(Requirement.TYPE_MODULE)) {
				
				if (requires.length() > 0) {
					requires.append(", ");
				}
				
				RequirementParameter name=req.getParameter("name");
				RequirementParameter fromVersion=req.getParameter("fromVersion");
				RequirementParameter toVersion=req.getParameter("toVersion");
				
				requires.append(name.getValue());
				
				if (fromVersion != null && toVersion != null) {
					requires.append(";bundle-version=\"["+fromVersion.getValue()+","+toVersion.getValue()+")\"");
				} else if (fromVersion != null || toVersion != null) {
					requires.append(";bundle-version=\""+
							(fromVersion.getValue()==null?toVersion.getValue():fromVersion.getValue())+"\"");
				}
			}
		}
		
		manifest.getMainAttributes().put(new Attributes.Name("Require-Bundle"), requires.toString());
		
		if (serviceComponent != null) {
			manifest.getMainAttributes().put(new Attributes.Name("Service-Component"), serviceComponent);
		}
	}
	
	public String generateOSGiDescriptor(LanguageModel model, Actor actor)
							throws Exception {
		String className=model.getDeclaration().getFullName();
		String modelName=model.getDeclaration().getLocalName();
		
		StringBuffer buf=new StringBuffer();
		
		buf.append("<?xml version=\"1.0\"?>\r\n");
		buf.append("<component name=\""+modelName+"-"+actor.getName()+"\">\r\n");
		buf.append("\t<implementation class=\""+className+"_"+actor.getName()+"_factory\"/>\r\n");
		buf.append("\t<service>\r\n");
		buf.append("\t\t<provide interface=\"org.scribble.runtime.ProcessFactory\"/>\r\n");
		buf.append("\t</service>\r\n");
		buf.append("</component>\r\n");
		
		return(buf.toString());
	}
	
	/*
	public void generateClass(LanguageModel model, Actor actor) throws Exception {
		JavaClassGeneratorVisitor visitor=new JavaClassGeneratorVisitor(model, actor);
		
		model.getLanguageUnit().visit(visitor);
		
		CtClass cls=visitor.getGeneratedClass();
		
		System.out.println(cls.toString());
	}
	*/
}
