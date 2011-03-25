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
package org.scribble.lang.projection;

import java.io.Serializable;
import java.util.Map;

import org.scribble.common.logging.Journal;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.parser.antlr.ANTLRLanguageParser;

public class ProjectorMain {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: ProjectorMain <filename> <actor>");
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
			
			Projector projector=new LanguageProjectorImpl();
			
			ANTLRLanguageParser parser=new ANTLRLanguageParser();
			
			LanguageModel model=parser.parse(is, j);
			
			LanguageModel local=projector.project(model, args[1], j);
			
			StringBuffer buf=new StringBuffer();
			
			if (local != null) {
				local.toText(buf, 0);
				
				System.out.println("Local Model");
				System.out.println(buf.toString());
			} else {
				System.err.println("Failed to project local model");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
