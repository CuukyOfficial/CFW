/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.configuration.serialization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class BasicSerializable implements ConfigurationSerializable {

	private final Map<String, Field> fields = new HashMap<>();
	private final Map<Class<?>, SerializationPolicy<?, ?>> policies = new HashMap<>();
	private boolean scanned;

	public BasicSerializable() {
	}

	public BasicSerializable(Map<String, Object> data) {
		try {
			this.deserialized(data);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void initSerialization() {
		if (!this.scanned) {
			this.scanFields(this.getClass());
			this.registerPolicies();
			this.scanned = true;
		}
	}

	private void scanFields(Class<?> clazz) {
		this.checkFields(clazz.getFields());
		this.checkFields(clazz.getDeclaredFields());
		if (BasicSerializable.class.isAssignableFrom(clazz.getSuperclass()))
			this.scanFields(clazz.getSuperclass());
	}

	private void checkFields(Field[] fields) {
		for (Field field : fields) {
			Serialize info = field.getAnnotation(Serialize.class);
			if (info != null)
				this.fields.put(info.value(), field);
		}
	}

	private Object parseObject(Object object, Field field, BiFunction<SerializationPolicy<?, ?>, Object, Object> parseFunc) {
		if (object == null)
			return null;
		SerializationPolicy<?, ?> policy = this.policies.get(field.getType());
		return policy == null ? object : parseFunc.apply(policy, object);
	}

	private Object getFieldValue(Field field) throws IllegalAccessException {
		field.setAccessible(true);
		Object value = field.get(this);
		return this.parseObject(value, field, SerializationPolicy::serialize);
	}

	private void deserializeField(Field field, Object value) throws IllegalAccessException {
		field.setAccessible(true);
		field.set(this, this.parseObject(value, field, SerializationPolicy::deserialize));
	}

	void deserialized(Map<String, Object> data) throws IllegalAccessException {
		this.initSerialization();
		for (String location : this.fields.keySet()) {
			Object value = data.get(location);
			if (value != null)
				this.deserializeField(this.fields.get(location), value);
		}
	}

	protected <P, C> void registerPolicy(Class<C> clazz, SerializationPolicy<P, C> policy) {
		this.policies.put(clazz, policy);
	}

	protected <P, C> void registerPolicy(Class<C> clazz, Function<C, P> supplier, Function<P, C> consumer) {
		this.policies.put(clazz, new SerializationPolicy<>(supplier, consumer));
	}

	/**
	 * List of default policies: - UUID - Material
	 */
	protected void registerPolicies() {
		this.registerPolicy(UUID.class, UUID::toString, UUID::fromString);
		this.registerPolicy(Material.class, Material::toString, Material::valueOf);
	}

	@Override
	public Map<String, Object> serialize() {
		this.initSerialization();
		Map<String, Object> data = new LinkedHashMap<>();
		for (String loc : this.fields.keySet()) {
			try {
				data.put(loc, this.getFieldValue(this.fields.get(loc)));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
}
