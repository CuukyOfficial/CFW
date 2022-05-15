package de.cuuky.cfw.configuration.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class Instantiator<T extends BasicSerializable> {

    private final Class<T> clazz;

    Instantiator(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> getInitConstructor(Class<?> current) throws NoSuchMethodException {
        Constructor<?> objDef;
        try {
            objDef = current.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return this.getInitConstructor(current.getSuperclass());
        }
        objDef.setAccessible(true);
        return (Constructor<T>) objDef;
    }

    T instantiate()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> intConstr = this.getInitConstructor(this.clazz);
        return this.clazz.cast(intConstr.newInstance());
    }
}