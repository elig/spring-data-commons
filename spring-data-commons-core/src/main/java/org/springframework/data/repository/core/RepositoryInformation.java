/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.repository.core;

import java.lang.reflect.Method;

/**
 * Aditional repository specific information
 * 
 * @author Oliver Gierke
 */
public interface RepositoryInformation extends RepositoryMetadata {

	/**
	 * Returns the base class to be used to create the proxy backing instance.
	 * 
	 * @return
	 */
	Class<?> getRepositoryBaseClass();

	/**
	 * Returns if the configured repository interface has custom methods, that might have to be delegated to a custom
	 * implementation. This is used to verify repository configuration.
	 * 
	 * @return
	 */
	boolean hasCustomMethod();

	/**
	 * Returns whether the given method is a custom repository method.
	 * 
	 * @param method
	 * @param baseClass
	 * @return
	 */
	boolean isCustomMethod(Method method);

	/**
	 * Returns all methods considered to be query methods.
	 * 
	 * @param repositoryInterface
	 * @return
	 */
	Iterable<Method> getQueryMethods();

	/**
	 * Returns the target class method that is backing the given method. This can be necessary if a repository interface
	 * redeclares a method of the core repository interface (e.g. for transaction behaviour customization). Returns the
	 * method itself if the target class does not implement the given method.
	 * 
	 * @param method
	 * @return
	 */
	Method getTargetClassMethod(Method method);
}
