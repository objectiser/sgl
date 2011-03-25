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
 * This class provides the LangUnit implementation of the
 * projector rule.
 */
public class LangUnitProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == LangUnit.class);
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
		LangUnit ret=new LangUnit();
		LangUnit source=(LangUnit)model;
		
		ret.derivedFrom(source);
		
		ret.setName(source.getName());
		
		if (source.getBlock() != null) {
			projectBlockForRole(source.getBlock(), ret, context, role, l);
		}
		
		// Project actors
		java.util.List<Actor> clients=buildListOfClientActors(ret.getBlock());
		
		for (int i=0; i < clients.size(); i++) {
			
			Actor actor=(Actor)
					context.project(clients.get(i),
								role, l);
			
			if (actor != null) {
				ret.getActors().add(actor);
			}
		}
		
		return(ret);
	}
	
	protected void projectBlockForRole(Block source, final LangUnit target,
			final ProjectorContext context, final String role, final Journal l) {
		
		source.visit(new DefaultVisitor() {
			public void accept(ActorList elem) {
				if (elem.getActor(role) != null) {
					
					if (target.getBlock() != null && target.getBlock().size() > 0) {
						l.error("Only one role block can be projected currently", null);
					} else {
						target.setBlock((Block)context.project((Block)elem.getParent(),
								role, l));
					}
				}
			}
		});
	}
	
	protected java.util.List<Actor> buildListOfClientActors(Block source) {
		final java.util.List<Actor> defined=new java.util.Vector<Actor>();
		final java.util.List<Actor> ret=new java.util.Vector<Actor>();
		
		source.visit(new DefaultVisitor() {
			public void accept(ActorList elem) {
				
				for (int i=0; i < elem.getActors().size(); i++) {
					Actor actor=elem.getActors().get(i);
					
					defined.add(actor);
				}
			}
			
			public void start(When elem) {
				accept(elem.getInteraction());
			}
			
			public void accept(Interaction elem) {
				if (ret.contains(elem.getFromActor().getActor()) == false) {
					ret.add(elem.getFromActor().getActor());
				}
				
				for (ActorState as : elem.getToActors()) {
					if (ret.contains(as.getActor()) == false) {
						ret.add(as.getActor());
					}
				}
			}
		});
		
		ret.removeAll(defined);
		
		return(ret);
	}
}
