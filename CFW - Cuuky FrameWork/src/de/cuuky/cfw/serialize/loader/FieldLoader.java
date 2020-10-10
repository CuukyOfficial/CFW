package de.cuuky.cfw.serialize.loader;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.identifiers.NullClass;
import de.cuuky.cfw.utils.JavaUtils;

public class FieldLoader {

	private Map<Field, Class<? extends CFWSerializeable>> keyTypes, valueTypes;
	private Map<String, Field> fields;

	private Class<?> clazz;

	public FieldLoader(Class<?> clazz) {
		this.fields = new LinkedHashMap<String, Field>();
		this.keyTypes = new LinkedHashMap<Field, Class<? extends CFWSerializeable>>();
		this.valueTypes = new LinkedHashMap<Field, Class<? extends CFWSerializeable>>();
		this.clazz = clazz;

		loadFields();
	}

	private void loadFields() {
		Field[] declFields = clazz.getDeclaredFields();
		for (Field field : declFields) {
			CFWSerializeField anno = field.getAnnotation(CFWSerializeField.class);
			if (anno == null)
				continue;

			String path = anno.path();
			fields.put(path.equals("PATH") ? anno.enumValue() : path, field);
			if (anno.keyClass() != NullClass.class)
				keyTypes.put(field, anno.keyClass());

			if (anno.valueClass() != NullClass.class)
				valueTypes.put(field, anno.valueClass());
		}

		this.reverseMaps();
	}

	private void reverseMaps() {
		this.fields = JavaUtils.reverseMap(this.fields);
		this.keyTypes = JavaUtils.reverseMap(this.keyTypes);
		this.valueTypes = JavaUtils.reverseMap(this.valueTypes);
	}

	public Class<? extends CFWSerializeable> getKeyType(Field field) {
		return this.keyTypes.get(field);
	}

	public Class<? extends CFWSerializeable> getValueType(Field field) {
		return this.valueTypes.get(field);
	}

	public Map<String, Field> getFields() {
		return fields;
	}
}