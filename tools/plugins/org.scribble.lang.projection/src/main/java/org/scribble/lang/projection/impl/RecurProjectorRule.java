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
 * This class provides the Recur implementation of the
 * projector rule.
 */
public class RecurProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == Recur.class);
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
		Recur ret=new Recur();
		Recur source=(Recur)model;
		
		ret.derivedFrom(source);
		
		ret.setName(source.getName());
		
		if (source.getBlock() != null) {
			ret.setBlock((Block)context.project(source.getBlock(),
						role, l));
		}
		
		return(ret);
	}
}
