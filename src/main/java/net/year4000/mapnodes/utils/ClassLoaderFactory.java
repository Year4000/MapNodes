package net.year4000.mapnodes.utils;

import com.google.common.annotations.Beta;
import net.year4000.mapnodes.MapNodesPlugin;

import java.lang.annotation.Annotation;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The is an experiment to abstract the class listener loader.
 *
 * @param <A> The Annotation type must contain a value() method.
 * @param <E> The Enum type that contains the values, this is
 *              the return type of the annotation value() method.
 * @param <L> The Listener type the will be registered.
 */
@Beta
public abstract class ClassLoaderFactory<A, E, L> {
    private Set<Class<? extends L>> rawEventTypes = new HashSet<>();
    private Map<A, Class<? extends L>> eventTypes = new HashMap<>();
    private Class<? extends Annotation> annotationClass;
    private boolean built = false;

    public ClassLoaderFactory(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /** Builder pattern to add Listener classes to this loader */
    public ClassLoaderFactory<A, E, L> add(Class<? extends L> type) {
        rawEventTypes.add(type);
        return this;
    }

    /** This will take apart the listener classes and the value type and make a map */
    public void build() {
        rawEventTypes.forEach(type -> {
            try {
                Annotation typeName = type.getAnnotation(annotationClass);
                eventTypes.put((A) typeName, type);
            } catch (Exception e) {
                MapNodesPlugin.log(e, false);
            }
        });

        built = true;
    }

    /** The point of this class to get the region type by name */
    public Class<? extends L> getRegionType(String name) {
        checkArgument(built); // Make sure we are built

        for (A type : eventTypes.keySet()) {
            try {
                if (((E) type.getClass().getMethod("value").invoke(type)).equals(name.toLowerCase())) {
                    return eventTypes.get(type);
                }
            } catch (Exception e) {
                MapNodesPlugin.debug(e, true);
            }
        }

        throw new InvalidParameterException(name + " is not a valid region type.");
    }
}
