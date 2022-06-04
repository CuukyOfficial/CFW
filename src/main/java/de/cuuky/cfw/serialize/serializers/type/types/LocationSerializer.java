/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.serialize.serializers.type.types;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import de.cuuky.cfw.serialize.CFWSerializeManager;
import de.cuuky.cfw.serialize.identifiers.CFWSerializeable;
import de.cuuky.cfw.serialize.serializers.type.CFWSerializeType;

@Deprecated
public class LocationSerializer extends CFWSerializeType {

    public LocationSerializer(CFWSerializeManager manager) {
        super(manager);
    }

    @Override
    public Object deserialize(CFWSerializeable instance, String key, Field field, ConfigurationSection section) {
        if (field.getType() != Location.class || manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")) == null)
            return null;

        Number x = (Number) section.get(key + ".x"), y = (Number) section.get(key + ".y"), z = (Number) section.get(key + ".z");

        // Compatibility
        try {
            Number yaw = (Number) section.get(key + ".yaw"), pitch = (Number) section.get(key + ".pitch");
            return new Location(manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")), x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.floatValue(), pitch.floatValue());
        } catch (Exception e) {
        }

        return new Location(manager.getOwnerInstance().getServer().getWorld(section.getString(key + ".world")), x.doubleValue(), y.doubleValue(), z.doubleValue());
    }

    @Override
    public boolean serialize(CFWSerializeable instance, Field field, Object value, String saveUnder, ConfigurationSection section) {
        if (field.getType() != Location.class)
            return false;

        Location loc = (Location) value;
        section.set(saveUnder + ".world", loc.getWorld().getName());
        section.set(saveUnder + ".x", loc.getX());
        section.set(saveUnder + ".y", loc.getY());
        section.set(saveUnder + ".z", loc.getZ());
        section.set(saveUnder + ".yaw", loc.getYaw());
        section.set(saveUnder + ".pitch", loc.getPitch());
        return true;
    }
}
