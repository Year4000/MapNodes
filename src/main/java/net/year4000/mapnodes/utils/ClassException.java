package net.year4000.mapnodes.utils;

import com.ewized.utilities.bukkit.util.MessageUtil;

public class ClassException extends Exception {
    public ClassException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return MessageUtil.replaceColors(" &7[&e⚠&7] &6" + super.getMessage());
    }
}
