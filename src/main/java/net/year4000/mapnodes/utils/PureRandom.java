package net.year4000.mapnodes.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class PureRandom {
    /** The list of raw numbers. */
    private List<Integer> rand = new ArrayList<>();
    /** Random Object */
    private Random random = new Random(System.currentTimeMillis());
    /** The size of init */
    private int init;

    public PureRandom(int number) {
        init = number;

        for (int i = 0; i < number; i++) {
            rand.add(i);
        }
    }

    /** Get the next int from the list. */
    public int nextInt() {
        int tempRand;

        // Logic
        if (rand.size() != 0) {
            int temp = random.nextInt(rand.size());
            tempRand = rand.get(temp);
            rand.remove(temp);
        }
        else {
            tempRand = random.nextInt(init);
        }

        return tempRand;
    }

}
