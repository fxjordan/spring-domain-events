/*
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Identifier for a publication target.
 *
 * @author Oliver Drotbohm
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class PublicationTargetIdentifier {

	private static Field LISTENER_METHOD_FIELD;
	private static Map<Object, PublicationTargetIdentifier> IDENTIFIERS = new ConcurrentHashMap<>();

	static {

		LISTENER_METHOD_FIELD = ReflectionUtils.findField(ApplicationListenerMethodAdapter.class, "method");
		ReflectionUtils.makeAccessible(LISTENER_METHOD_FIELD);
	}

	String value;

	/**
	 * Creates a {@link PublicationTargetIdentifier} for the given {@link Method}.
	 *
	 * @param method must not be {@literal null}.
	 * @return
	 */
	public static PublicationTargetIdentifier forMethod(Method method) {

		return IDENTIFIERS.computeIfAbsent(method, it -> computeForMethod(method));
	}

	/**
	 * Creates a {@link PublicationTargetIdentifier} for the given listener instance.
	 *
	 * @param listener
	 * @return
	 */
	public static PublicationTargetIdentifier forListener(Object listener) {

		return IDENTIFIERS.computeIfAbsent(listener, it -> {

			if (it instanceof ApplicationListenerMethodAdapter) {

				Method method = (Method) ReflectionUtils.getField(LISTENER_METHOD_FIELD, it);
				/*
				 * Do not call forMethod(method) here. This may cause non-deterministic
				 * 'IllegalStateException: Recursive update' thrown from ConcurrentHashMap.computeIfAbsent.
				 * ConcurrentHashMap documentations defines that the computation 'must not attempt to update
				 * any other mappings of this map.'
				 * 
				 * We could maintain two Maps, one for listener IDs and one for method IDs but recomputing
				 * the id at most two times is the easier solution.
				 */
				return computeForMethod(method);
			}

			throw new IllegalStateException("Unsupported listener implementation!");
		});
	}
	
	/**
	 * Returns a {@link PublicationTargetIdentifier} for the given {@link Method}.
	 * 
	 * @param method
	 * @return
	 */
	private static PublicationTargetIdentifier computeForMethod(Method method) {
	    String typeName = ClassUtils.getUserClass(method.getDeclaringClass()).getName();
        String methodName = method.getName();
        String parameterTypes = StringUtils.arrayToDelimitedString(method.getParameterTypes(), ", ");

        return PublicationTargetIdentifier.of(String.format("%s.%s(%s)", typeName, methodName, parameterTypes));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
