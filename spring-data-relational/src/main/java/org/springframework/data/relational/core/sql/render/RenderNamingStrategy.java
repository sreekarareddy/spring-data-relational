/*
 * Copyright 2019 the original author or authors.
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
package org.springframework.data.relational.core.sql.render;

import java.util.function.Function;

import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.core.sql.render.NamingStrategies.DelegatingRenderNamingStrategy;
import org.springframework.util.Assert;

/**
 * Naming strategy for SQL rendering.
 *
 * @author Mark Paluch
 * @see NamingStrategies
 * @since 1.1
 */
public interface RenderNamingStrategy {

	/**
	 * Return the {@link Column#getName() column name}.
	 *
	 * @param column the column.
	 * @return the {@link Column#getName() column name}.
	 * @see Column#getName()
	 */
	default String getName(Column column) {
		return column.getName();
	}

	/**
	 * Return the {@link Column#getName() column reference name}.
	 *
	 * @param column the column.
	 * @return the {@link Column#getName() column reference name}.
	 * @see Column#getReferenceName() ()
	 */
	default String getReferenceName(Column column) {
		return column.getReferenceName();
	}

	/**
	 * Return the {@link Table#getName() table name}.
	 *
	 * @param table the table.
	 * @return the {@link Table#getName() table name}.
	 * @see Table#getName()
	 */
	default String getName(Table table) {
		return table.getName();
	}

	/**
	 * Return the {@link Table#getReferenceName() table reference name}.
	 *
	 * @param table the table.
	 * @return the {@link Table#getReferenceName() table name}.
	 * @see Table#getReferenceName()
	 */
	default String getReferenceName(Table table) {
		return table.getReferenceName();
	}

	/**
	 * Applies a {@link Function mapping function} after retrieving the object (column name, column reference name, …)
	 * name.
	 *
	 * @param mappingFunction the function that maps an object name.
	 * @return a new {@link RenderNamingStrategy} applying {@link Function mapping function}.
	 */
	default RenderNamingStrategy map(Function<String, String> mappingFunction) {

		Assert.notNull(mappingFunction, "Mapping function must not be null!");

		return new DelegatingRenderNamingStrategy(this, mappingFunction);
	}
}