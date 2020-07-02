package de.cuuky.cfw.serialize.field;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cuuky.cfw.serialize.identifier.NullClass;
import de.cuuky.cfw.serialize.identifier.CFWSerializeField;
import de.cuuky.cfw.serialize.identifier.CFWSerializeable;

public class FieldLoader {

	private Map<Field, Class<? extends CFWSerializeable>> arrayTypes;
	private Map<String, Field> fields;

	public FieldLoader(Class<?> clazz) {
		this.fields = new HashMap<String, Field>();
		this.arrayTypes = new HashMap<Field, Class<? extends CFWSerializeable>>();

		Field[] declFields = clazz.getDeclaredFields();
		for (Field field : declFields) {
			if (field.getAnnotation(CFWSerializeField.class) == null)
				continue;

			CFWSerializeField anno = field.getAnnotation(CFWSerializeField.class);

			fields.put(anno.path(), field);

			if (Collection.class.isAssignableFrom(field.getType()) && anno.arrayClass() != NullClass.class)
				arrayTypes.put(field, anno.arrayClass());
		}
	}

	public Map<Field, Class<? extends CFWSerializeable>> getArrayTypes() {
		return arrayTypes;
	}

	public Map<String, Field> getFields() {
		return fields;
	}
}