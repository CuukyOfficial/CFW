package de.cuuky.cfw.serialize.identifiers;

@Deprecated
public interface CFWSerializeable {

	/**
	 * To mark classes
	 */

	default void onDeserializeEnd() {}

	default void onSerializeStart() {}

}