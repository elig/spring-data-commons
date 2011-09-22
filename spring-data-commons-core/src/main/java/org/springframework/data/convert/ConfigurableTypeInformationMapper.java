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
package org.springframework.data.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

/**
 * {@link TypeInformationMapper} implementation that can be either set up using a {@link MappingContext} or manually set
 * up {@link Map} of {@link String} aliases to types. If a {@link MappingContext} is used the {@link Map} will be build
 * inspecting the {@link PersistentEntity} instances for type alias information.
 * 
 * @author Oliver Gierke
 */
public class ConfigurableTypeInformationMapper implements TypeInformationMapper {

	private final Map<TypeInformation<?>, Object> typeMap;

	/**
	 * Creates a {@link ConfigurableTypeInformationMapper} from the given {@link MappingContext}. Inspects all
	 * {@link PersistentEntity} instances for alias information and builds a {@link Map} of aliases to types from it.
	 * 
	 * @param mappingContext
	 */
	public ConfigurableTypeInformationMapper(MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext) {

		Assert.notNull(mappingContext);
		this.typeMap = new HashMap<TypeInformation<?>, Object>();

		for (PersistentEntity<?, ?> entity : mappingContext.getPersistentEntities()) {
			Object alias = entity.getTypeAlias();
			if (alias != null) {
				typeMap.put(entity.getTypeInformation(), alias);
			}
		}
	}

	/**
	 * Creates a new {@link ConfigurableTypeMapper} for the given type map.
	 * 
	 * @param sourceTypeMap must not be {@literal null}.
	 */
	public ConfigurableTypeInformationMapper(Map<? extends Class<?>, String> sourceTypeMap) {

		Assert.notNull(sourceTypeMap);

		this.typeMap = new HashMap<TypeInformation<?>, Object>(sourceTypeMap.size());

		for (Entry<? extends Class<?>, String> entry : sourceTypeMap.entrySet()) {
			TypeInformation<?> key = ClassTypeInformation.from(entry.getKey());
			String value = entry.getValue();

			if (typeMap.containsValue(value)) {
				throw new IllegalArgumentException(String.format(
						"Detected mapping ambiguity! String %s cannot be mapped to more than one type!", value));
			}

			this.typeMap.put(key, value);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.convert.TypeInformationMapper#createAliasFor(org.springframework.data.util.TypeInformation)
	 */
	public Object createAliasFor(TypeInformation<?> type) {

		Object key = typeMap.get(type);
		return key == null ? null : key;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.convert.TypeInformationMapper#resolveTypeFrom(java.lang.Object)
	 */
	public TypeInformation<?> resolveTypeFrom(Object alias) {

		for (Entry<TypeInformation<?>, Object> entry : typeMap.entrySet()) {
			if (entry.getValue().equals(alias)) {
				return entry.getKey();
			}
		}

		return null;
	}
}
