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
package org.scribble.runtime.messaging;

public class DefaultMessage implements Message {

	private Endpoint m_source=null;
	private Object m_value=null;
	private Endpoint m_destination=null;
		
	public DefaultMessage(Endpoint source, Object value) {
		m_source = source;
		m_value = value;
	}
	
	public Endpoint getSource() {
		return(m_source);
	}
	
	public Endpoint getDestination() {
		return(m_destination);
	}
	
	public void setDestination(Endpoint endpoint) {
		m_destination = endpoint;
	}

	public Object getValue() {
		return(m_value);
	}

	public String toString() {
		return("Message[from="+getSource()+",to="+getDestination()+",value="+getValue()+"]");
	}
}
