package main.Service;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Binding {
    public enum Mode {
        ONEWAY,
        TWOWAY,
        ONEWAYTOSOURCE
    }

    private static Dictionary<Node, List<Binding>> bindings = new Hashtable<>();

    private Node node;
    private String binding;
    private Mode mode;

    public Binding() {
    }

    public Binding(Node node, String binding, Mode mode) {
        this.node = node;
        this.binding = binding;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Binding{" +
                "node=" + node +
                ", binding='" + binding + '\'' +
                ", mode=" + mode +
                '}';
    }

    public static void addBinding(Node n, Binding b) {
        List<Binding> lst = bindings.get(n);
        if (lst == null) {
            lst = new ArrayList<Binding>();
            bindings.put(n, lst);
        }
        lst.add(b);
    }

    public static void setTwoWay(Node n, String binding) {
        Binding b = new Binding(n, binding, Mode.TWOWAY);
        addBinding(n, b);
        System.out.println(String.format("Setting two way binding for %s", b));
    }

    public static void setOneWay(Node n, String binding) {
        Binding b = new Binding(n, binding, Mode.ONEWAY);
        addBinding(n, b);
        System.out.println(String.format("Setting one way binding for %s", b));
    }

    public static void applyBinding(Node n, Object bean) {
        Pattern p = Pattern.compile("\\{(\\w+)=(\\w+)\\}");

        List<Binding> lst = bindings.get(n);
        if (lst != null) {
            for (Binding b : lst) {
                Matcher m = p.matcher(b.binding);
                if (m.matches()) {
                    String destName = m.group(1); // 0 = everything
                    String sourceName = m.group(2);
                    Property<Object> dest = ReflectionHelper.getProperty(n, destName);
                    ObservableValue<Object> destRO = ReflectionHelper.getReadonlyProperty(n, destName);
                    Property<Object> source = ReflectionHelper.getProperty(bean, sourceName);
                    ObservableValue<Object> sourceRO = ReflectionHelper.getReadonlyProperty(bean, sourceName);
                    if ((dest != null || destRO != null) && (source != null || sourceRO != null)) {
                        switch (b.mode) {
                            case ONEWAY:
                                if(dest != null) {
                                    dest.bind(sourceRO);
                                } else {
                                    System.out.print(String.format("Invalid binding %s, destination is not a Property<?>", b));
                                }
                                break;
                            case TWOWAY:
                                if ((dest != null || destRO != null) && (source != null || sourceRO != null)) {
                                    dest.bindBidirectional(source);
                                } else {
                                    System.out.print(String.format("Invalid binding %s", b));
                                    if (source == null) {
                                        System.out.println(String.format(", source is not a Property<?>"));
                                    }
                                    if (dest == null) {
                                        System.out.println(String.format(", destination is not a Property<?>"));
                                    }
                                }
                                break;
                            case ONEWAYTOSOURCE:
                                if(source != null) {
                                    source.bind(destRO);
                                } else {
                                    System.out.print(String.format("Invalid binding %s, source is not a Property<?>", b));
                                }
                                break;
                        }
                    } else {
                        System.out.print(String.format("Invalid binding %s", b));
                        if (source == null && sourceRO == null) {
                            System.out.println(String.format(", source property %s not found", sourceName));
                        }
                        if (dest == null && destRO == null) {
                            System.out.println(String.format(", destination property %s not found", destName));
                        }
                        System.out.println();
                    }
                }
            }
        }

        if (n instanceof Pane) {
            Pane pane = (Pane) n;
            // go down
            for (Node child : pane.getChildren()) {
                applyBinding(child, bean);
            }
        }
    }

    /**
     * TODO: Is this called by JavaFX?
     *
     * @param n
     */
    public static void clearConstraints(Node n) {
        System.out.println(String.format("clear all constrains for %s", n.getId()));
    }
}