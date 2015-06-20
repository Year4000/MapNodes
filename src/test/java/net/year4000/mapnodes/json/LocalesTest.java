/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.json;

import com.google.gson.Gson;
import junit.framework.Assert;
import net.year4000.mapnodes.game.NodeGame;
import org.junit.Test;

public final class LocalesTest {
    private static final Gson GSON = new Gson();
    private static final String KEY = "hello";
    private static final String JSON_DEFAULT = "{'locales': {'en_US': {'hello': 'Hello'}, 'pt_BR': {'hello': 'Ola'}}}";

    @Test
    public void test() {
        NodeGame game = GSON.fromJson(JSON_DEFAULT, NodeGame.class);

        //game.getLocales().forEach((k, v) -> v.forEach((a, b) -> System.out.printf("%s | %s | %s \n", k, a, b)));

        Assert.assertEquals("Hello", game.locale("en_US", KEY));
        Assert.assertEquals("Ola", game.locale("pt_BR", KEY));
        Assert.assertEquals("Hello", game.locale("blah", KEY));
    }
}
