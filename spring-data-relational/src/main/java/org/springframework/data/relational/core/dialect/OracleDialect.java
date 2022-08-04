/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.relational.core.dialect;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Collection;
import java.util.Collections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Period;

import static java.util.Arrays.*;

/**
 * An SQL dialect for Oracle.
 *
 * @author Jens Schauder
 * @since 2.1
 */
public class OracleDialect extends AnsiDialect {

	/**
	 * Singleton instance.
	 */
	public static final OracleDialect INSTANCE = new OracleDialect();

	private static final IdGeneration ID_GENERATION = new IdGeneration() {
		@Override
		public boolean driverRequiresKeyColumnNames() {
			return true;
		}
	};

	protected OracleDialect() {}

	@Override
	public IdGeneration getIdGeneration() {
		return ID_GENERATION;
	}

	@Override
	public Collection<Object> getConverters() {
		return asList(TimestampAtUtcToOffsetDateTimeConverter.INSTANCE, NumberToBooleanConverter.INSTANCE, BooleanToIntegerConverter.INSTANCE, PeriodToINTERVALYM.INSTANCE, DurationToINTERVALDS.INSTANCE);
	}

	@ReadingConverter
	enum NumberToBooleanConverter implements Converter<Number, Boolean> {
		INSTANCE;

		@Override
		public Boolean convert(Number number) {
			return number.intValue() != 0;
		}
	}
	@WritingConverter
	enum BooleanToIntegerConverter implements Converter<Boolean, Integer> {
		INSTANCE;

		@Override
		public Integer convert(Boolean bool) {
			return bool ? 1 : 0;
		}
	}
	@WritingConverter
	enum PeriodToINTERVALYM implements Converter<Period, Object> {
		INSTANCE;

		@Override
		public Object convert(Period period) {
			Object interval = null;
			if(period == null)
				return null;
			if (ClassUtils.isPresent("oracle.sql.INTERVALYM", this.getClass().getClassLoader())) {
				try {
					Class<?> intervalym = Class.forName("oracle.sql.INTERVALYM");
					Method method = intervalym.getDeclaredMethod("toIntervalym", Period.class);
					interval = method.invoke(null, period);
				} 
				catch (ClassNotFoundException | LinkageError |
						IllegalAccessException | IllegalArgumentException | InvocationTargetException |
						NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			return interval;
		}
	}
	@WritingConverter
	enum DurationToINTERVALDS implements Converter<Duration, Object> {
		INSTANCE;

		@Override
		public Object convert(Duration duration) {
			Object interval = new Object();
			if(duration==null)
				return null;
			if (ClassUtils.isPresent("oracle.sql.INTERVALDS", this.getClass().getClassLoader())) {
				try {
					Class<?> intervalds = Class.forName("oracle.sql.INTERVALDS");
					Method method = intervalds.getDeclaredMethod("toIntervalds", Duration.class);
					interval = method.invoke(intervalds.getDeclaredConstructor().newInstance(), duration);
				} 
				catch (ClassNotFoundException | IllegalAccessException |
						IllegalArgumentException | InvocationTargetException |
						InstantiationException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			return interval;
		}
	}
}
