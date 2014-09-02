package net.year4000.mapnodes.utils;


import net.year4000.utilities.bukkit.MessageUtil;

public class TeamException extends Exception {
    public TeamException(String message) {
        super(message);
    }

    /** Send out a purity message. */
    @Override
    public String getMessage() {
        return MessageUtil.replaceColors(" &7[&e⚠&7] " + super.getMessage());
    }

    /** Use the raw message and not a purity warring notice. */
    public String getRawMessage() {
        return MessageUtil.replaceColors(super.getMessage());
    }
}
