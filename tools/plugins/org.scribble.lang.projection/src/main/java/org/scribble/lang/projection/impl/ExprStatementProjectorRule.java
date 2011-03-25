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
 * This class provides the ExprStatement implementation of the
 * projector rule.
 */
public class ExprStatementProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == ExprStatement.class);
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
		ExprStatement ret=new ExprStatement();
		ExprStatement source=(ExprStatement)model;
		
		ret.derivedFrom(source);

		if (source.getActor() != null && source.getActor().getName().equals(role)) {
			ret.setActor((Actor)context.project(source.getActor(),
						role, l));
		
			if (source.getExpression() != null) {
				ret.setExpression((Expression)context.project(source.getExpression(),
							role, l));
			}
		} else {
			ret = null;
		}
		
		return(ret);
	}
}
