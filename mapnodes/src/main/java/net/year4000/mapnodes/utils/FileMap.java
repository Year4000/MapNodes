package net.year4000.mapnodes.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class FileMap<K, V> implements Map<K, V> {
    private final Type MAP_TYPE = new TypeToken<LinkedHashMap<K, V>>(){}.getType();
    private static final Gson GSON = new Gson();
    private final File file;
    private long lastModified;
    private LinkedHashMap<K, V> elements = Maps.newLinkedHashMap();

    public FileMap(File file) {
        this.file = checkNotNull(file, "There must be a file");
        read();
    }

    public FileMap(String fileName) {
        this(fileName == null ? new File("null") : new File(fileName));
    }

    /** Write the data to the file */
    private synchronized void write() {
        try {
            if (!file.exists()) {
                while (!file.createNewFile()) {
                    lastModified = file.lastModified();
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(elements, writer);
                lastModified = file.lastModified();
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    /** Read the data from the file */
    private synchronized void read() {
        try {
            if (!file.exists()) {
                write();
            }
            try (FileReader reader = new FileReader(file)) {
                elements = GSON.fromJson(reader, MAP_TYPE);
            }
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

    /** Check if the file has been modified then update the object */
    private void checkFileModified() {
        if (lastModified != file.lastModified()) {
            read();
        }
    }

    @Override
    public int size() {
        checkFileModified();
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        checkFileModified();
        return elements.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        checkFileModified();
        return elements.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        checkFileModified();
        return elements.containsValue(value);
    }

    @Override
    public V get(Object key) {
        checkFileModified();
        return elements.get(key);
    }

    @Override
    public V put(K key, V value) {
        checkFileModified();
        V valueTmp = elements.put(key, value);
        write();
        return valueTmp;
    }

    @Override
    public V remove(Object key) {
        checkFileModified();
        V value = elements.remove(key);
        write();
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        checkFileModified();
        elements.putAll(map);
        write();
    }

    @Override
    public void clear() {
        checkFileModified();
        elements.clear();
        write();
    }

    @Override
    public Set<K> keySet() {
        checkFileModified();
        return elements.keySet();
    }

    @Override
    public Collection<V> values() {
        checkFileModified();
        return elements.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        checkFileModified();
        return elements.entrySet();
    }
}
