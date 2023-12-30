package de.plixo.galactic.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents any Path. e.g. java.lang.String or java/lang/String
 */
@Getter
public class ObjectPath {
    private final List<String> names;

    public ObjectPath(String name) {
        this.names = List.of(name);
    }

    public ObjectPath(String name, String split) {
        this.names = Arrays.asList(name.split(Pattern.quote(split)));
    }

    public ObjectPath(String... args) {
        this.names = Arrays.asList(args);
    }

    public ObjectPath(List<String> names) {
        this.names = new ArrayList<>(names);
    }

    public ObjectPath add(String name) {
        var strings = new ArrayList<>(names);
        strings.add(name);
        return new ObjectPath(strings);
    }

    public ObjectPath join(ObjectPath path) {
        var strings = new ArrayList<>(names);
        strings.addAll(path.names);
        return new ObjectPath(strings);
    }

    public String asDotString() {
        return String.join(".", names);
    }


    public String asSlashString() {
        return String.join("/", names);
    }

    public String asJVMPath() {
        return STR."\{String.join("/", names)}.class";
    }

    @Override
    public String toString() {
        return STR."\{asDotString()}[\{names.size()}]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPath that = (ObjectPath) o;
        return Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(names);
    }


}
