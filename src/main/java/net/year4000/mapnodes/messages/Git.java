/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.messages;

import com.google.common.base.Charsets;
import net.year4000.mapnodes.MapNodesPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class Git {
    private static final String GIT_PROPERTIES = "/git.properties";
    private static Git inst;
    private Properties git = new Properties();

    private Git() {
        try {
            InputStream gitProperties = Git.class.getResourceAsStream(GIT_PROPERTIES);
            git.load(new InputStreamReader(gitProperties, Charsets.UTF_8));
        }
        catch (IOException e) {
            MapNodesPlugin.log(e, true);
        }
    }

    private static Git init() {
        if (inst == null) {
            inst = new Git();
        }

        return inst;
    }

    public static String get(String key) {
        return init().git.getProperty(key, key);
    }
}
