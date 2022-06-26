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

package de.varoplugin.cfw.dependencies;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This class uses a fuckton of undocumented stuff and will most likely stop working at some point in the future. Have fun xd
 * 
 * @author Almighty-Satan
 */
class BukkitJarLoader implements JarLoader {

    private final HackClassLoader hackClassLoader;

    BukkitJarLoader() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        Object pluginClassLoader = this.getClass().getClassLoader();
        Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");

        if (pluginClassLoader.getClass() != pluginClassLoaderClass)
            throw new RuntimeException("Unknown ClassLoader: " + pluginClassLoader.getClass());

        this.hackClassLoader = new HackClassLoader();

        Field classesField = pluginClassLoaderClass.getDeclaredField("classes");
        classesField.setAccessible(true);
        Map<?, ?> originalMap = (Map<?, ?>) classesField.get(pluginClassLoader);
        classesField.set(pluginClassLoader, new HackMap<>(originalMap, this.hackClassLoader));
    }

    @Override
    public void load(File jar) throws Exception {
        this.hackClassLoader.addURL(jar.toURI().toURL());
    }

    private static class HackClassLoader extends URLClassLoader {

        public HackClassLoader() {
            super(new URL[0]);
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }
    }

    private static class HackMap<K, V> implements Map<K, V> {

        private final Map<K, V> parent;
        private final HackClassLoader classLoader;

        private HackMap(Map<K, V> parent, HackClassLoader classLoader) {
            this.parent = parent;
            this.classLoader = classLoader;
        }

        @Override
        public int size() {
            return this.parent.size();
        }

        @Override
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.parent.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.parent.containsValue(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public V get(Object key) {
            V parentObject = this.parent.get(key);
            if (parentObject != null)
                return parentObject;
            else
                try {
                    return (V) this.classLoader.loadClass((String) key);
                } catch (ClassNotFoundException e) {
                    return null;
                }
        }

        @Override
        public V put(K key, V value) {
            return this.parent.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return this.parent.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            this.parent.putAll(m);
        }

        @Override
        public void clear() {
            this.parent.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.parent.keySet();
        }

        @Override
        public Collection<V> values() {
            return this.parent.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return this.parent.entrySet();
        }
    }
}
