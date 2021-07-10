package de.cuuky.cfw.version;

import java.lang.reflect.Field;

public class MagmaVersionAdapter extends OneTwelveVersionAdapter {

	@Override
	protected void initXp(String entityHumanName, String foodMetaName) {
		//this is EXTREMELY unsafe
		try {
			Class<?> foodMetaClass = Class.forName(foodMetaName);
			int fieldNum = 0;
			for(Field field : Class.forName(entityHumanName).getDeclaredFields())
				if(fieldNum == 0 && field.getType() == foodMetaClass)
					fieldNum = 1;
				else if(fieldNum == 1 && field.getType() == foodMetaClass)
					fieldNum = 2;
				else if(fieldNum == 2 && field.getType() == int.class)
					fieldNum = 3;
				else if(fieldNum == 3 && field.getType() == float.class)
					fieldNum = 4;
				else if(fieldNum == 4 && field.getType() == float.class)
					fieldNum = 5;
				else if(fieldNum == 5 && field.getType() == int.class) {
					this.xpCooldownField = field;
					return;
				}else
					fieldNum = 0;

			throw new Error("Unable to find xp cooldown field");
		} catch (SecurityException | ClassNotFoundException e) {
			throw new Error(e);
		}
	}
}
