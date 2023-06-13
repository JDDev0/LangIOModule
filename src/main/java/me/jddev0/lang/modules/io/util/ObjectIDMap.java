package me.jddev0.lang.modules.io.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ObjectIDMap<T> {
    private final Map<Integer, T> objects;
    private final Function<T, Integer> getObjectHashCode;
    private int currentID;

    public ObjectIDMap(Function<T, Integer> getObjectHashCode) {
        this.objects = new HashMap<>();
        this.getObjectHashCode = getObjectHashCode;
        this.currentID = 0;
    }

    /**
     * Should prevent users from introducing bugs by guessing or calculating object ids [THIS IS NO SECURITY FEATURE]
     */
    private int generateNextID(T object) {
        do {
            int selfHashCode = hashCode();
            do {
                selfHashCode += (int)System.currentTimeMillis() + (int)System.nanoTime();
            }while(selfHashCode == 0);

            int objectHashCode = getObjectHashCode.apply(object);
            do {
                objectHashCode += (int)System.currentTimeMillis() + (int)System.nanoTime();
            }while(objectHashCode == 0);

            currentID += objectHashCode % selfHashCode + selfHashCode;
        }while(objects.containsKey(currentID));

        return currentID;
    }

    public int size() {
        return objects.size();
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public boolean containsId(int id) {
        return objects.containsKey(id);
    }

    public T get(int id) {
        return objects.get(id);
    }

    /**
     * @param value The value to be stored
     * @return The ID the value is stored as
     */
    public int add(T value) {
        int id = generateNextID(value);

        objects.put(id, value);

        return id;
    }

    public T remove(int id) {
        return objects.remove(id);
    }

    public void clear() {
        objects.clear();
    }

    public Set<Map.Entry<Integer, T>> entries() {
        return objects.entrySet();
    }
}
