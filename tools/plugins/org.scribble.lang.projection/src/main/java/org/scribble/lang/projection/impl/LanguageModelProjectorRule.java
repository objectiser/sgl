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
package org.scribble.lang.projection.impl;

import org.scribble.common.logging.Journal;
import org.scribble.lang.model.*;

/**
 * This class provides the LanguageModel implementation of the
 * projector rule.
 */
public class LanguageModelProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == LanguageModel.class);
	}
	
	/**
	 * This method projects the supplied model object based on the
	 * specified role.
	 * 
	 * @param model The model object
	 * @param role The role
	 * @param l The model listener
	 * @return The projected model object
	 */
	public ModelObject project(ProjectorContext context, ModelObject model,
					String role, Journal l) {
		LanguageModel ret=new LanguageModel();
		LanguageModel source=(LanguageModel)model;
		
		ret.derivedFrom(source);
		
		if (source.getDeclaration() != null) {
			ret.setDeclaration((Namespace)context.project(source.getDeclaration(), role, l));
		}
		
		// Project import statements
		for (int i=0; i < source.getImports().size(); i++) {
			
			Import newImport=(Import)
					context.project(source.getImports().get(i),
								role, l);
			
			if (newImport != null) {
				ret.getImports().add(newImport);
			}
		}
		
		// Project requirements
		for (int i=0; i < source.getRequirements().size(); i++) {
			
			Requirement newRequirement=(Requirement)
					context.project(source.getRequirements().get(i),
								role, l);
			
			if (newRequirement != null) {
				ret.getRequirements().add(newRequirement);
			}
		}
		
		if (source.getLanguageUnit() != null) {
			ret.setLanguageUnit((LangUnit)context.project(source.getLanguageUnit(),
						role, l));
		}
		
		return(ret);
	}
}
