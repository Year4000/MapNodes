package net.year4000.mapnodes.utils;

import junit.framework.Assert;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

@Log
public class CommonTest {

    @Test
    public void xor() {
        String[] teams = new String[] {"Red", "Yellow", "Blue", "Cyan", "Pink", "Players", "Defenders", "Attackers"};
        String[] names = new String[] {"McMurdom","Kalapauses","ewized","Ktec_Gamer","Sukor_GamerBR","Gabigamer","Arthur1209_ADR","TheTechFork","tiavamp","titi8panda","HaxedClient","ChancePlayz","Manuel7panda","Mey_BR","NXtreme_PT","Red_Games","Franciscothelink","Pirateka","Spiderzin1","layodutra1","ninjamatt2004","davigrilo","Matheush1212","huzake","The_Martinis"};
        Set<Integer> nameSet = new HashSet<>();
        Set<Integer> teamSet = new HashSet<>();

        for (String team : teams) {
            int id = chars(team);
            teamSet.add(id);
            // System.out.println(team + ": " + id + " " + Integer.toHexString(id));
        }

        for (String name : names) {
            int id = chars(name);
            nameSet.add(id);
            // System.out.println(name + ": " + id + " " + Integer.toHexString(id));
        }

        Assert.assertEquals("Team sizes", teams.length, teamSet.size());
        Assert.assertEquals("Name sizes", names.length, nameSet.size());
    }

    public static int chars(String string) {
        return Common.chars(string);
    }
}
