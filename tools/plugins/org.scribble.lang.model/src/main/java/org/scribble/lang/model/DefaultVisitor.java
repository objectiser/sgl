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
package org.scribble.lang.model;

/**
 * This class provides the default implementation of the visitor.
 *
 */
public class DefaultVisitor implements Visitor {

	public void accept(ActorList elem) {
	}

	public void start(Block elem) {
	}

	public void end(Block elem) {
	}

	public void start(Choice elem) {
	}

	public void end(Choice elem) {
	}

	public void start(When elem) {
	}

	public void end(When elem) {
	}

	public void start(If elem) {
	}

	public void end(If elem) {
	}

	public void start(ElseIf elem) {
	}

	public void end(ElseIf elem) {
	}

	public void start(Else elem) {
	}

	public void end(Else elem) {
	}

	public void start(Par elem) {
	}

	public void end(Par elem) {
	}

	public void start(ConcurrentPath elem) {
	}

	public void end(ConcurrentPath elem) {
	}

	public void accept(Interaction elem) {
	}

	public void start(LangUnit elem) {
	}

	public void end(LangUnit elem) {
	}

	public void start(Recur elem) {
	}

	public void end(Recur elem) {
	}

	public void accept(RecurCall elem) {
	}

	public void accept(ExprStatement elem) {
	}

	public void accept(Variable elem) {
	}

	public void start(While elem) {
	}

	public void end(While elem) {
	}

}
