package Service;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ReflectionHelper {
    @SuppressWarnings("unchecked")
    public static <E> E getPropertyValue(Object obj, String propName) {
        final Class<? extends Object> c = obj.getClass();

        // try prop
        try {
            final Method prop = c.getMethod("get" + propName);
            return (E) prop.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
        }

        return null;
    }

    public static Property<Object> getProperty(Object obj, String propName) {
        final Class<? extends Object> c = obj.getClass();

        // try prop
        try {
            final Method prop = c.getMethod(propName + "Property");
            return (Property<Object>) prop.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
        }

        return null;
    }

    public static ObservableValue<Object> getReadonlyProperty(Object obj, String propName) {
        final Class<? extends Object> c = obj.getClass();

        // try prop
        try {
            final Method prop = c.getMethod(propName + "Property");
            return (ObservableValue<Object>) prop.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
        }

        return null;
    }

    public static Method findMethod(Object obj, String name,
                                    Class<?>... types) {
        if (obj == null)
            throw new IllegalArgumentException("Object is null");
        try {
            Class<? extends Object> c = obj.getClass();
            while (c != null) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.getName().equals(name)
                            && m.equals(c.getDeclaredMethod(m.getName(), types))) {
                        return m;
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception ex) {

        }

        return null;
    }

    public static Object callMethod(Object obj, String name,
                                    Object... parameters) throws Exception {
        Class<?>[] types = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> pc = parameters[i].getClass();
            if(pc == Boolean.class)
                types[i] = boolean.class;
            else if(pc == Character.class)
                types[i] = char.class;
            else if(pc == Short.class)
                types[i] = short.class;
            else if(pc == Integer.class)
                types[i] = int.class;
            else if(pc == Long.class)
                types[i] = long.class;
            else if(pc == Float.class)
                types[i] = float.class;
            else if(pc == Double.class)
                types[i] = double.class;
            else if(pc == Void.class)
                types[i] = void.class;
            else
                types[i] = pc;
        }

        Method m = findMethod(obj, name, types);
        if (m == null) {
            throw new Exception(String.format("Method %s not found", name));
        }

        return m.invoke(obj, parameters);
    }
}
