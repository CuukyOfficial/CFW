package de.cuuky.cfw.serialize.identifiers;

public class NullClass implements CFWSerializeable {

	/*
	 * Empty class to make a default value in the VaroSerializeField
	 */

	@Override
	public void onDeserializeEnd() {}

	@Override
	public void onSerializeStart() {}

}