package de.cuuky.cfw.serialize.serializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.CFWSerializeObject;
import de.cuuky.cfw.serialize.identifier.CFWSerializeable;

public class CFWSerializer {

	private CFWSerializeManager manager;
	
	private CFWSerializeable instance;
	private YamlConfiguration saveTo;
	private String saveUnder;

	public CFWSerializer(CFWSerializeManager manager, String saveUnder, CFWSerializeable instance, YamlConfiguration saveTo) {
		this.manager = manager;
		this.saveUnder = saveUnder;
		this.instance = instance;
		this.saveTo = saveTo;

		serialize();
	}

	public void serialize() {
		CFWSerializeObject handler = manager.getHandler(instance.getClass());
		instance.onSerializeStart();

		ArrayList<String> list1 = new ArrayList<String>();
		list1.addAll(handler.getFieldLoader().getFields().keySet());
		Collections.reverse(list1);
		for (String fieldIdent : list1) {
			try {
				Field field = handler.getFieldLoader().getFields().get(fieldIdent);
				field.setAccessible(true);

				Object obj = field.get(instance);
				if (obj != null) {
					if (obj instanceof List) {
						ArrayList<?> list = (ArrayList<?>) obj;
						if (!list.isEmpty())
							if (list.get(0) instanceof CFWSerializeable) {
								for (int i = 0; i < list.size(); i++) {
									Object listObject = list.get(i);

									CFWSerializeObject handl = manager.getHandler((Class<?>) listObject.getClass());
									if (handl != null) {
										new CFWSerializer(manager, saveUnder + "." + fieldIdent + "." + i, (CFWSerializeable) listObject, saveTo);
										continue;
									}
								}
								continue;
							}

						if (obj instanceof Long)
							obj = String.valueOf((long) obj);
					}

					if (field.getType() == Location.class) {
						Location loc = (Location) obj;
						if (loc.getWorld() == null) {
							obj = manager.getNullReplace();
							saveTo.set(saveUnder + "." + fieldIdent, manager.getNullReplace());
							continue;
						}

						saveTo.set(saveUnder + "." + fieldIdent + ".world", loc.getWorld().getName());
						saveTo.set(saveUnder + "." + fieldIdent + ".x", loc.getX());
						saveTo.set(saveUnder + "." + fieldIdent + ".y", loc.getY());
						saveTo.set(saveUnder + "." + fieldIdent + ".z", loc.getZ());
						continue;
					}

					CFWSerializeObject handl = manager.getHandler((Class<?>) obj.getClass());
					if (handl != null) {
						new CFWSerializer(manager, saveUnder + "." + fieldIdent, (CFWSerializeable) field.get(instance), saveTo);
						continue;
					}
				} else
					obj = manager.getNullReplace();

				saveTo.set(saveUnder + "." + fieldIdent, (obj instanceof Enum ? manager.getStringByEnum((CFWSerializeable) obj) : obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}