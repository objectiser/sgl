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
package org.scribble.lang.generator;

import org.scribble.lang.model.Actor;
import org.scribble.lang.model.ActorExpression;
import org.scribble.lang.model.ActorList;
import org.scribble.lang.model.ActorParameter;
import org.scribble.lang.model.ActorState;
import org.scribble.lang.model.ExprStatement;
import org.scribble.lang.model.Block;
import org.scribble.lang.model.Choice;
import org.scribble.lang.model.ConcurrentPath;
import org.scribble.lang.model.Else;
import org.scribble.lang.model.ElseIf;
import org.scribble.lang.model.If;
import org.scribble.lang.model.Import;
import org.scribble.lang.model.Interaction;
import org.scribble.lang.model.LangUnit;
import org.scribble.lang.model.LanguageModel;
import org.scribble.lang.model.ModelObject;
import org.scribble.lang.model.Par;
import org.scribble.lang.model.Recur;
import org.scribble.lang.model.RecurCall;
import org.scribble.lang.model.StateAccessor;
import org.scribble.lang.model.StateDefinition;
import org.scribble.lang.model.Type;
import org.scribble.lang.model.Variable;
import org.scribble.lang.model.Visitor;
import org.scribble.lang.model.When;
import org.scribble.lang.model.While;


// TODO:

// Tasks and process could have auto-generated externalizable methods to minimize the
// size of serialized representation of process instance.


// THINK ABOUT:

// In a recursion situation, where a large number of recursions are performed, if there
// is a stack of invoked tasks, then this would mean persistence would required complete
// process to be stored and retrieved. Might be worth thinking whether there is a different
// approach where the tasks are more decoupled, so could potentially be retrieved
// independently - but this needs to be offset with the benefits of having access to
// variabes in a parent scoped task


public class JavaTextGeneratorVisitor implements Visitor {
	
	private Actor m_actor=null;
	private java.util.List<Task> m_tasks=new java.util.Vector<Task>();
	private Task m_current=null;
	private int m_number=0;
	private java.util.Map<ModelObject, Task> m_taskStore=new java.util.HashMap<ModelObject, Task>();
	private java.util.Map<ModelObject, Task> m_taskStore2=new java.util.HashMap<ModelObject, Task>();
	private java.util.Map<String, Task> m_taskRecur=new java.util.HashMap<String, Task>();
	private java.util.List<Scope> m_scopes=new java.util.Vector<Scope>();
	private LanguageModel m_model=null;
	
	public JavaTextGeneratorVisitor(LanguageModel model, Actor actor) {
		m_model = model;
		m_actor = actor;
	}
	
	public String toText() {
		StringBuffer ret=new StringBuffer();
		
		// Generate the class header
		if (m_model.getDeclaration() != null) {
			ret.append("package "+m_model.getDeclaration().getScope()+";\r\n");
		}
		
		ret.append("import org.scribble.runtime.*;\r\n");
		ret.append("import org.scribble.runtime.messaging.*;\r\n");
		
		for (Import imp : m_model.getImports()) {
			ret.append("import "+imp.getName()+";\r\n");
		}
		
		// NOTE: Generation can only be done if single actor type in local model
		String className=m_model.getDeclaration().getLocalName()+"_"+
							m_actor.getName();
		
		ret.append("public class "+className+" extends DefaultProcess {\r\n");
		
		ret.append("\r\n\tpublic static String PROCESS_NAME=\""+className+"\";\r\n");
		
		ret.append("\r\n");
		
		ret.append("\tpublic "+className+"(Endpoint endpoint) {\r\n");
		ret.append("\t\tsuper(PROCESS_NAME, endpoint);\r\n");
		ret.append("\t\tgetTasks().add(new "+m_model.getLanguageUnit().getName()+"(null,null));\r\n");
		ret.append("\t}\r\n\r\n");
		
		for (int i=0; i < m_tasks.size(); i++) {
			Task t=m_tasks.get(i);
			
			if (t.getParent() == null) {
				ret.append(t.getText()+"\r\n");
			}
		}
		
		ret.append("}\r\n");
		
		return(ret.toString());
	}

	public void accept(ActorList elem) {
		Scope scope=m_scopes.get(0);
		
		for (Actor actor : elem.getActors()) {
			scope.add(actor);
			
			if (actor.equals(m_actor) == false) {
				if (!actor.isLocal()) {
						
					// TODO: Does not take into account scope of actor - can't be class
					// scope, as this would then be only useful for a single actor per
					// process, so needs to cater for scopes that are being passed
					// between the tasks? But then when an expression references the
					// actor, it must be available in the local code scope - so might
					// need to use local vars that retrieve the appropriate actor from
					// the context?
					
					// Could generate an inner class at that point, so that the tasks
					// are created in the scope of outer classes.
					
					output("java.util.Properties "+actor.getName()+"Props=new java.util.Properties();");
					
					for (ActorParameter param : actor.getParameters()) {
						output(actor.getName()+"Props.put(\""+param.getName()+"\","+param.getValue()+");");
					}
					
					output(actor.getName()+" = context.find(\""+actor.getImplements()+"\","+
									actor.getName()+"Props);");
					
				} else {  //m_current.isOpen(actor) == false) {
					String actorName=actor.getRepresents() == null ? actor.getName() : actor.getRepresents();
					
					// TODO: Need to decide how to create actor reference for local
					// process. The local process would be in the same Java package
					// and class, with the different _<actor> suffix.
					
					//output(actor.getName()+" = context.create("+m_model.getDeclaration().getFullName()+
					//								"_"+actor.getRepresents()+".class);");
					
					output(actor.getName()+" = "+m_model.getDeclaration().getFullName()+
							"_"+actorName+"_factory.instance().getEndpoint();");
				}
				
				addActor(actor, false);
			}
		}
	}
	
	/**
	 * This method searches for the named actor in the
	 * list of scopes - starting with the latest.
	 * 
	 * @param name The name of the actor
	 * @return The actor, or null if not found
	 */
	public Actor getActor(String name) {
		Actor ret=null;
		
		for (Scope scope : m_scopes) {
			ret = scope.getActor(name);
			
			if (ret != null) {
				break;
			}
		}
		
		return(ret);
	}
	
	public void start(Block elem) {
		Scope scope=new Scope();
		m_scopes.add(0, scope);
	}
	
	public void end(Block elem) {
		m_scopes.remove(0);
	}

	public void start(Choice elem) {
		Task post=createTask();
		m_current.addSubTask(post);
		
		m_taskStore.put(elem, m_current);
		
		// Generate code for recursion
		output("MutuallyExclusiveTasks choicetask=new MutuallyExclusiveTasks();");
		output(post.getName()+" postchoice=new "+post.getName()+"(this);");
		output("choicetask.scheduleOnCompletion(postchoice);");
		
		for (int i=0; i < elem.getWhens().size(); i++) {
			When when=elem.getWhens().get(i);
			
			m_current = m_taskStore.get(elem);
			
			Task whentask=createTask();
			m_current.addSubTask(whentask);
			
			output("choicetask.addTask(new "+whentask.getName()+"(null));");
			
			m_taskStore.put(when, whentask);
			
			addTask(whentask);
		}
		
		m_current = m_taskStore.get(elem);
		output("if (choicetask.onMessage(context,mesg)) {");
		
		increment();
		
		output("ret = true;");
		output("mesg = null;");

		decrement();
		
		output("}");

		output("if (choicetask.isCompleted() == false) {");
		increment();
		output("getTasks().add(choicetask);");
		decrement();
		output("}");
		
		addTask(post);
		m_taskStore.put(elem, post);
	}

	public void end(Choice elem) {
	}

	public void start(When elem) {
		// Restore 'path' task
		m_current = m_taskStore.get(elem);
		
		if (elem.getInteraction() != null) {
			accept(elem.getInteraction());
		}
	}

	public void end(When elem) {
	}

	public void start(If elem) {
		Task cur=createTask();
		Task post=createTask();
		
		m_current.addSubTask(cur);
		m_current.addSubTask(post);
		
		// Generate code for recursion
		output(cur.getName()+" cur=new "+cur.getName()+"(null);");
		output(post.getName()+" post=new "+post.getName()+"(this);");
		output("cur.scheduleOnCompletion(post);");
		
		output("if (cur.onMessage(context,mesg)) {");

		increment();
		
		output("ret = true;");
		output("mesg = null;");

		decrement();
		
		output("}");

		output("if (cur.isCompleted() == false) {");
		increment();
		output("getTasks().add(cur);");
		decrement();
		output("}");
				
		// Add follow on tasks
		addTask(post);
		addTask(cur);

		// Add if construct
		output("if ("+elem.getExpression()+") {");
		increment();
		
		m_taskStore.put(elem, cur);
		m_taskStore2.put(elem, post);
	}

	public void end(If elem) {
		
		// Issue - should this restore current, or just
		// place output on the stored task? May differ
		// for each construct
		m_current = m_taskStore.get(elem);
		
		decrement();
		output("}");
		
		// Restore 'if' post task
		m_current = m_taskStore2.get(elem);
	}

	public void start(ElseIf elem) {
		m_current = m_taskStore.get(elem.getParent());

		decrement();
		output("} else if ("+elem.getExpression()+") {");
		increment();
	}

	public void end(ElseIf elem) {
	}

	public void start(Else elem) {
		m_current = m_taskStore.get(elem.getParent());

		decrement();
		output("} else {");
		increment();
	}

	public void end(Else elem) {
	}

	public void accept(Interaction elem) {
		
		if (elem.isWaitState(m_actor)) {
			
			// Check if current task has any content
			if (m_current == null || m_current.hasContent()) {
				
				// Need to generate new task
				Task t=createTask();

				// Only generate transition to task if current task has set
				if (m_current != null) {
					m_current.addSubTask(t);				
					
					// Generate scheduling
					output(t.getName()+" intcall=new "+t.getName()+"(this);");
		
					output("if (intcall.onMessage(context,mesg)) {");
					
					increment();
					
					output("ret = true;");
					output("mesg = null;");
		
					decrement();
					
					output("}");
		
					output("if (intcall.isCompleted() == false) {");
					increment();
					output("getTasks().add(intcall);");
					decrement();
					output("}");
				}
				
				addTask(t);
			}
			
			output("// "+elem.toString());
			
			String typeName=null;
			String instOfName=null;
			
			ActorState ae=elem.getToActors().get(0);
			
			Type type = m_current.getType(ae.getState());
			
			if (type != null) {
				typeName = type.toString();
				
				Class<?> cls=type.toClass();
				
				if (cls != null) {
					instOfName = type.toClass().getName();
				} else {
					instOfName = typeName;
				}
			}
			
			if (typeName == null) {
				throw new RuntimeException("Unable to identify received type. "+
						"ActorState="+ae+" type="+type);
			}
			
			output("if (mesg == null || (mesg.getValue() instanceof "+instOfName+") == false ||");
			output("\t\t("+elem.getFromActor().getActor().getName()+" != null && "+
						elem.getFromActor().getActor().getName()+".getChannelId() != null &&");
			output("\t\t\t\tmesg.getSource().getChannelId() != null &&");
			output("\t\t\t\t"+elem.getFromActor().getActor().getName()+
						".getChannelId().equals(mesg.getSource().getChannelId())==false)) {");
			
			output("\treturn(false);");
			output("}");
			
			output(ae.getState()+" = ("+typeName+")mesg.getValue();");
			
			output(elem.getFromActor().getActor().getName()+" = mesg.getSource();");
			output("ret = true;");
			output("mesg = null;");
			
		} else {
			String actor="<unknown>";
			
			if (elem.getToActors().size() > 0) {
				actor = elem.getToActors().get(0).getActor().getName();
			}
			
			output("context.send("+actor+", "+elem.getFromActor().getExpression().getText()+");");
		}
	}

	public void start(LangUnit elem) {
		Task top=createTask();
		top.setName(elem.getName());
		top.setLanguageUnitTask(true);
		
		for (Actor actor : elem.getActors()) {
			top.addActor(actor, true);
		}
		
		addTask(top);
	}

	public void end(LangUnit elem) {
	}

	public void start(Par elem) {
		Task post=createTask();
		m_current.addSubTask(post);
		
		m_taskStore.put(elem, m_current);
		
		// Generate code for recursion
		output(post.getName()+" postpar=new "+post.getName()+"(this);");
		
		for (int i=0; i < elem.getPaths().size(); i++) {
			ConcurrentPath cp=elem.getPaths().get(i);
			
			m_current = m_taskStore.get(elem);
			
			Task pathtask=createTask();
			m_current.addSubTask(pathtask);
			
			output(pathtask.getName()+" pathtask"+i+"=new "+pathtask.getName()+"(null);");
			output("pathtask"+i+".scheduleOnCompletion(postpar);");
			
			m_taskStore.put(cp, pathtask);
		}
			
		for (int i=0; i < elem.getPaths().size(); i++) {
			ConcurrentPath cp=elem.getPaths().get(i);
			
			Task pathtask=m_taskStore.get(cp);

			m_current = m_taskStore.get(elem);

			output("if (pathtask"+i+".onMessage(context,mesg)) {");
			
			increment();
			
			output("ret = true;");
			output("mesg = null;");

			decrement();
			
			output("}");

			output("if (pathtask"+i+".isCompleted() == false) {");
			increment();
			output("getTasks().add(pathtask"+i+");");
			decrement();
			output("}");
			
			m_taskStore.put(cp, pathtask);
			
			addTask(pathtask);
		}
		
		addTask(post);
		m_taskStore.put(elem, post);
	}

	public void end(Par elem) {
		// Restore 'post' par task
		m_current = m_taskStore.get(elem);
	}

	public void start(ConcurrentPath elem) {
		// Restore 'path' task
		m_current = m_taskStore.get(elem);
	}

	public void end(ConcurrentPath elem) {
	}

	public void start(Recur elem) {
		Task recur=createTask();
		Task post=createTask();
		
		m_current.addSubTask(recur);
		m_current.addSubTask(post);
		
		// Generate code for recursion
		output(recur.getName()+" recur=new "+recur.getName()+"(null);");
		output(post.getName()+" postrecur=new "+post.getName()+"(this);");
		output("recur.scheduleOnCompletion(postrecur);");
		
		output("if (recur.onMessage(context,mesg)) {");

		increment();
		
		output("ret = true;");
		output("mesg = null;");

		decrement();
		
		output("}");

		output("if (recur.isCompleted() == false) {");
		increment();
		output("getTasks().add(recur);");
		decrement();
		output("}");
				
		// Add follow on tasks
		addTask(post);
		addTask(recur);
		
		m_taskStore.put(elem, post);
		m_taskRecur.put(elem.getName(), recur);
	}

	public void end(Recur elem) {
		
		// Restore 'post' recur task
		m_current = m_taskStore.get(elem);
		
	}

	public void accept(RecurCall elem) {
		Task recur=m_taskRecur.get(elem.getName());
		
		if (recur == null) {
			throw new IllegalArgumentException("Failed to find recur block for "+elem.getName());
		}
		
		Task post=createTask();
		m_current.addSubTask(post);
		
		// Generate code for recursion
		output(recur.getName()+" recur=new "+recur.getName()+"(null);");
		output(post.getName()+" postrecur=new "+post.getName()+"(this);");
		output("recur.scheduleOnCompletion(postrecur);");
		
		output("if (recur.onMessage(context,mesg)) {");
		
		increment();
		
		output("ret = true;");
		output("mesg = null;");

		decrement();
		
		output("}");
		
		output("if (recur.isCompleted() == false) {");
		increment();
		output("getTasks().add(recur);");
		decrement();
		output("}");
				
		// Add follow on tasks
		addTask(post);
		//addTask(recur);
		
		m_taskStore.put(elem, post);
	}

	public void accept(ExprStatement elem) {
		output(elem.getExpression()+";");	
	}

	public void accept(Variable elem) {
		addVariable(elem);
	}

	public void start(While elem) {
		// Need to store reference to state that checks condition.
		// Then store a 'scheduleOnCompletion' state which returns
		// to the original state, to recheck the condition.
		// Might need to create a new task, if the current task
		// already has activities.
		
		/*
		output("if ("+elem.getExpression()+") {");
		increment();
		
		m_taskStore.put(elem, m_current);
		*/
	}

	public void end(While elem) {

		/*
		m_current = m_taskStore.get(elem);
		
		decrement();
		output("}");
		*/
	}

	protected Task createTask() {
		return(new Task("T"+m_number++));
	}
	
	protected void addTask(Task t) {
		m_tasks.add(t);
		m_current = t;
	}
	
	protected void output(String text) {
		m_current.output(text);
	}

	protected void addActor(Actor actor, boolean open) {
		m_current.addActor(actor, open);
	}

	protected void addVariable(Variable var) {
		m_current.addVariable(var);
	}

	protected void increment() {
		m_current.increment();
	}
	
	protected void decrement() {
		m_current.decrement();
	}
	
	public class Task {
		
		private String m_name=null;
		private String m_text="";
		private int m_indent=3;
		private int m_baseLevel=0;
		private java.util.List<Actor> m_closedActors=
			new java.util.Vector<Actor>();
		private java.util.List<Actor> m_openActors=
			new java.util.Vector<Actor>();
		private java.util.Map<String,StateDefinition> m_stateDefinitions=
					new java.util.HashMap<String,StateDefinition>();
		private java.util.List<Task> m_subtasks=
					new java.util.Vector<Task>();
		private Task m_parent=null;
		private boolean m_languageUnitTask=false;
		
		public Task(String name) {
			m_name = name;
		}
		
		public void setLanguageUnitTask(boolean b) {
			m_languageUnitTask = b;
		}
		
		public boolean isLanguageUnitTask() {
			return(m_languageUnitTask);
		}
		
		public Type getType(String accessor) {
			Type ret=null;
			
			StateDefinition sd=getStateDefinition(accessor);
				
			if (sd != null) {
				ret = sd.getType();
			}
			
			if (ret == null && m_parent != null) {
				ret = m_parent.getType(accessor);
			}
			
			return(ret);
		}

		public void setBaseLevel(int level) {
			m_baseLevel = level;
		}
		
		public int getBaseLevel() {
			return(m_baseLevel);
		}
		
		public String getName() {
			return(m_name);
		}
		
		public void setName(String name) {
			m_name = name;
		}
		
		public void output(String text) {
			for (int i=0; i < m_baseLevel+m_indent; i++) {
				m_text += "\t";
			}
			m_text += text +"\r\n";
		}
		
		public void increment() {
			m_indent++;
		}
		
		public void decrement() {
			m_indent--;
		}
		
		public void addActor(Actor actor, boolean open) {
			if (open) {
				m_openActors.add(actor);
			} else {
				m_closedActors.add(actor);
			}
		}
		
		public boolean isOpen(Actor actor) {
			return(m_openActors.contains(actor));
		}
		
		public java.util.List<Actor> getClosedActors() {
			return(m_closedActors);
		}
		
		public java.util.List<Actor> getOpenActors() {
			return(m_openActors);
		}
		
		public void addVariable(Variable var) {
			for (StateDefinition sd : var.getStateDefinitions()) {
				m_stateDefinitions.put(sd.getName(), sd);
			}
		}
		
		public java.util.Collection<StateDefinition> getStateDefinitions() {
			return(m_stateDefinitions.values());
		}
		
		public StateDefinition getStateDefinition(String name) {
			return(m_stateDefinitions.get(name));
		}
		
		public void addSubTask(Task task) {
			task.setBaseLevel(getBaseLevel()+1);
			m_subtasks.add(task);
			
			task.setParent(this);
		}
		
		public java.util.List<Task> getSubTasks() {
			return(m_subtasks);
		}
		
		public void setParent(Task task) {
			m_parent = task;
		}
		
		public Task getParent() {
			return(m_parent);
		}
		
		public boolean hasContent() {
			return(m_text.length() > 0);
		}
		
		protected String indent(int level) {
			String ret="";
			
			for (int i=0; i < level; i++) {
				ret += "\t";
			}
			
			return(ret);
		}
		
		public String getText() {
			String ret=indent(m_baseLevel)+"\tpublic class "+m_name+" extends DefaultTask {\r\n";
			String actorParams="";
			
			for (Actor actor : getOpenActors()) {
				ret += indent(m_baseLevel)+"\t\tpublic Endpoint "+actor.getName()+";\r\n";
				actorParams += ",Endpoint "+actor.getName();
			}
			
			for (Actor actor : getClosedActors()) {
				ret += indent(m_baseLevel)+"\t\tpublic Endpoint "+actor.getName()+";\r\n";
			}
			
			for (StateDefinition sd : getStateDefinitions()) {
				ret += indent(m_baseLevel)+"\t\tpublic "+sd.getType()+" "+
								sd.getName()+";\r\n";
			}
			
			ret += "\r\n";
			
			ret += indent(m_baseLevel)+"\t\tpublic "+m_name+"(DefaultTask parent"+
									(isLanguageUnitTask()?actorParams:"")+") {\r\n";			
			ret += indent(m_baseLevel)+"\t\t\tsuper(parent);\r\n";	
			
			if (isLanguageUnitTask()) {
				for (Actor actor : getOpenActors()) {
					ret += indent(m_baseLevel)+"\t\t\tthis."+actor.getName()+" = "+actor.getName()+";\r\n";
				}				
			}
			
			ret += indent(m_baseLevel)+"\t\t}\r\n";
			
			ret += indent(m_baseLevel)+"\t\tpublic boolean onMessage(Context context, Message mesg) {\r\n";			
			ret += indent(m_baseLevel)+"\t\t\tboolean ret=false;\r\n";
			ret += indent(m_baseLevel)+"\t\t\tstarted(context);\r\n";
			
			for (StateDefinition sd : getStateDefinitions()) {
				if (sd.getInitializer() != null) {
					ret += indent(m_baseLevel)+"\t\t\t"+sd.getName()+" = "+sd.getInitializer()+";\r\n";
				}
			}
			
			ret += m_text;
			
			ret += indent(m_baseLevel)+"\t\t\tcompleted(context);\r\n";
			ret += indent(m_baseLevel)+"\t\t\treturn(ret);\r\n";
			ret += indent(m_baseLevel)+"\t\t}\r\n";
			
			// Generate sub-tasks
			for (Task subtask : getSubTasks()) {
				ret += subtask.getText();
			}
			
			ret += indent(m_baseLevel)+"\t}\r\n";
			
			return(ret);
		}
	}
	
	public class Scope {
		private java.util.Map<String, Actor> m_actors=new java.util.HashMap<String, Actor>();
		
		public Scope() {
		}
		
		public Actor getActor(String name) {
			return(m_actors.get(name));
		}
		
		public void add(Actor actor) {
			m_actors.put(actor.getName(), actor);
		}
	}
}
