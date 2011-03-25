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
 * This class provides the Interaction implementation of the
 * projector rule.
 */
public class InteractionProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == Interaction.class);
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
		Interaction ret=new Interaction();
		Interaction source=(Interaction)model;
		boolean f_projected=false;
		
		ret.derivedFrom(source);
		
		if (source.getFromActor() != null) {
			if (source.getFromActor().getActor().getName().equals(role)) {
				f_projected = true;
			}
			ret.setFromActor((ActorExpression)context.project(source.getFromActor(),
						role, l));
		}
		
		ActorState projectedState=null;
		
		for (int i=0; i < source.getToActors().size(); i++) {
			ActorState actorState=(ActorState)
					context.project(source.getToActors().get(i),
								role, l);
			
			if (actorState != null) {
				if (actorState.getActor().getName().equals(role)) {
					projectedState = actorState;
					f_projected = true;
				}
				ret.getToActors().add(actorState);
			}
		}
		
		// If role is associated with a 'to' actor, then only project
		// that actor state
		if (projectedState != null) {
			ret.getToActors().clear();
			ret.getToActors().add(projectedState);
		}
		
		// Only return projected interaction if role associated with 'from'
		// or 'to' actors
		return(f_projected ? ret : null);
	}
}
