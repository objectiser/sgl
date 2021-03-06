/*
 * Copyright 2009-10 www.scribble.org
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

import java.text.MessageFormat;
import org.scribble.common.logging.*;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.model.ModelObject;
import org.scribble.lang.projection.impl.DefaultProjectorContext;

/**
 * This class provides an implementation of the language projector.
 *
 */
public class LanguageProjectorImpl implements Projector {

	public LanguageModel project(LanguageModel model, String role, Journal journal) {
		LanguageModel ret=null;
		
		DefaultProjectorContext context=new DefaultProjectorContext();
		ModelObject obj=context.project(model, role, journal);
		
		if (obj != null) {
			if (obj instanceof LanguageModel) {
				ret = (LanguageModel)obj;
			} else {
				journal.error(MessageFormat.format(
						java.util.PropertyResourceBundle.getBundle("org.scribble.lang.projection.Messages").getString(
						"_NOT_PROJECTED_MODEL"), model.getDeclaration()), null);
			}
		}
		
		return(ret);
	}
}
