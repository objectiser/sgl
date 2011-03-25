/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.lang.parser.antlr;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.scribble.common.logging.Journal;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.parser.LanguageParser;

/**
 * This class provides the ANTLR implementation of the Language Parser
 * interface.
 *
 */
public class ANTLRLanguageParser implements LanguageParser {

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
			
			System.out.println("Model="+model);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public LanguageModel parse(InputStream is, Journal journal) {
		LanguageModel ret=null;
		
        try {
        	byte[] b=new byte[is.available()];
        	is.read(b);
        	
        	is.close();
        	
        	String document=new String(b);
        	
            ScribbleLangLexer lex = new ScribbleLangLexer(new ANTLRStringStream(document));
           	CommonTokenStream tokens = new CommonTokenStream(lex);
           	
    		ScribbleLangParser parser = new ScribbleLangParser(tokens);

    		LangTreeAdaptor adaptor=new LangTreeAdaptor();
    		adaptor.setParser(parser);
    		
    		parser.setDocument(document);
    		parser.setTreeAdaptor(adaptor);
    		
    		parser.setJournal(journal);

    		parser.description();
    		
    		ret = adaptor.getLanguageModel();
            
        } catch (Exception e)  {
            e.printStackTrace();
        }
		
		return(ret);
	}

}
