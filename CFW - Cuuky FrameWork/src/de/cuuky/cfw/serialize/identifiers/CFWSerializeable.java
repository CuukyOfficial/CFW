package de.cuuky.cfw.serialize.identifiers;

public interface CFWSerializeable {

	/**
	 * To mark classes
	 */

	default void onDeserializeEnd() {}

	default void onSerializeStart() {}

}