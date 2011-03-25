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
package org.scribble.lang.projection.impl;

import org.scribble.lang.model.*;

public class ProjectionUtil {

	static java.util.List<Class<?>> m_mainActivities=
				new java.util.Vector<Class<?>>();
	static java.util.List<Class<?>> m_secondaryActivities=
		new java.util.Vector<Class<?>>();

	static {
		m_mainActivities.add(ExprStatement.class);
		m_mainActivities.add(Interaction.class);
		
		// Secondary activities are important to prevent a
		// block from being immediately disgarded, but if
		// no main activities are found within a larger
		// structure, then the overall structure may be
		// disgarded.
		m_secondaryActivities.add(RecurCall.class);
	}

	public static boolean isMainActivity(Activity act) {
		boolean ret=m_mainActivities.contains(act.getClass());
		
		if (ret == false) {
			if (act.getClass() == Choice.class) {
				Choice choice=(Choice)act;
				
				// Main activity if choice has interactions
				for (int i=0; ret == false && i < choice.getWhens().size(); i++) {
					ret = choice.getWhens().get(i).getInteraction() != null;
				}
			} else if (act.getClass() == If.class) {
				If ifelem=(If)act;
				
				ret = ifelem.getExpression() != null;
				
				// Main activity if choice has interactions
				for (int i=0; ret == false && i < ifelem.getElseIfs().size(); i++) {
					ret = ifelem.getElseIfs().get(i).getExpression() != null;
				}
			} else if (act.getClass() == Recur.class) {
				Recur recur=(Recur)act;
				
				ret = recur.getBlock().size() > 0;
			}
		}
	
		return(ret);
	}

	public static boolean isSecondaryActivity(Activity act) {
		return(m_secondaryActivities.contains(act.getClass()));
	}
}
