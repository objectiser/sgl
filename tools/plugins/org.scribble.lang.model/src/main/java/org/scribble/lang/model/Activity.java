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
package org.scribble.lang.model;

/**
 * This class represents the base class for all Scribble definition
 * components.
 */
public abstract class Activity extends ModelObject {
	
	private static final long serialVersionUID = 6037288578996680696L;

	/**
	 * This method determines whether the activity is a wait
	 * state when considered in the context of the supplied
	 * actor.
	 * 
	 * @param actor The actor
	 * @return Whether the activity is a wait state
	 */
	public boolean isWaitState(Actor actor) {
		return(false);
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public abstract void visit(Visitor visitor);

}
