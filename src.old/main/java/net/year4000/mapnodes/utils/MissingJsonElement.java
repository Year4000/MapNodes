package net.year4000.mapnodes.utils;

public class MissingJsonElement extends Throwable {
    public MissingJsonElement(Exception e, String message) {
        super(String.format(
            "%s (%s)",
            message,
            e.getStackTrace()[0]
        ));
    }
}
