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
package org.springframework.events.mongodb;

import org.springframework.events.EventSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Felix Jordan
 */
@Slf4j
public class MongoEventSerializer implements EventSerializer {
	
    /*
     * (non-Javadoc)
     * @see de.olivergierke.events.EventSerializer#serialize(java.lang.Object)
     */
    @Override
    public Object serialize(Object event) {
        return event;
    }
    
    /*
     * (non-Javadoc)
     * @see de.olivergierke.events.EventSerializer#deserialize(java.lang.Object, java.lang.Class)
     */
    @Override
    public Object deserialize(Object serialized, Class<?> type) {
    	/*
    	 * TODO Refactor this ! Check is unnecessary, because Spring data
    	 * will always deserialize into the correct type and will throw if type
    	 * does not exist.
    	 */
    	if (!serialized.getClass().getName().equals(type.getName())) {
    		log.warn("Serialized event has unexpected type: {}. Expecting: {}", serialized.getClass(), type);
    	}
        return serialized;
    }
    
}
