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
package org.scribble.runtime;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;

public class ProcessHandle implements java.io.Externalizable {
	
	private Process m_process=null;
	private byte[] m_serialized=null;

	public ProcessHandle(Process process) {
		m_process = process;
	}
	
	public boolean isHydrated() {
		return(m_process != null);
	}
	
	public Process getProcess() {
		return(m_process);
	}
	
	/**
	 * This method is called to re-hydrate a process instance,
	 * of the type managed by the supplied factory.
	 * 
	 * @param factory The process factory
	 * @return The rehydrated process instance
	 * @throws java.io.IOException Failed to re-hydrate process
	 * @throws ClassNotFoundException Failed to find required class
	 */
	public Process hydrate(final ProcessFactory factory) throws java.io.IOException,
								ClassNotFoundException {
		
		if (m_process == null && m_serialized != null) {
			java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(m_serialized);
			java.io.ObjectInputStream ois=new java.io.ObjectInputStream(bais) {
				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {
					String className = desc.getName();
					return factory.getClass().getClassLoader().loadClass(className);
				}
			};
			
			m_process = (Process)ois.readObject();
			
			bais.close();
			ois.close();
		}
		
		return(m_process);
	}
	
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int len=in.readInt();
		
		m_serialized = new byte[len];
		
		in.read(m_serialized);
		
		// Clear the process, in case the deserialized representation
		// has been updated
		m_process = null;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		
		// Serialize the process (if not already done)
		if (m_serialized == null && m_process != null) {
			
			java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos=new java.io.ObjectOutputStream(baos);
			
			oos.writeObject(m_process);
			
			oos.close();
			baos.close();
			
			m_serialized = baos.toByteArray();

		}
		
		out.write(m_serialized.length);
		
		out.write(m_serialized, 0, m_serialized.length);
	}
}
