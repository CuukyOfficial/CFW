package de.cuuky.cfw.serialize.identifiers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CFWSerializeField {

	Class<? extends CFWSerializeable> arrayClass() default NullClass.class;

	String enumValue() default "ENUM";

	String path() default "PATH";

}
