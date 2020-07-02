package de.cuuky.cfw.serialize.loader;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cuuky.cfw.serialize.identifiers.CFWSerializeField;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.identifiers.NullClass;

public class FieldLoader {

	private Map<Field, Class<? extends CFWSerializeable>> arrayTypes;
	private Map<String, Field> fields;
	
	private Class<?> clazz;

	public FieldLoader(Class<?> clazz) {
		this.fields = new HashMap<String, Field>();
		this.arrayTypes = new HashMap<Field, Class<? extends CFWSerializeable>>();
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