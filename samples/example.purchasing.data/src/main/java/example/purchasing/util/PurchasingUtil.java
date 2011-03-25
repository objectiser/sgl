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
package example.purchasing.util;

public class PurchasingUtil {

	public static java.util.List<String> validCodes=new java.util.Vector<String>();
	public static java.util.List<String> supplierList=new java.util.Vector<String>();
	
	static {
		validCodes.add("pc1");
		validCodes.add("pc2");
		
		supplierList.add("supplier1");
		supplierList.add("supplier2");
	}
	
	public static boolean isValidProduct(String code) {
		return(validCodes.contains(code));
	}
}
