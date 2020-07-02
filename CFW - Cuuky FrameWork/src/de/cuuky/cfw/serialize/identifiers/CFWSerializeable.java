package de.cuuky.cfw.serialize.identifiers;

public interface CFWSerializeable {

	/**
	 * To mark classes
	 */
	
	void onDeserializeEnd();

	void onSerializeStart();

}