package net.year4000.mapnodes.game.clocks;

import lombok.Getter;
import net.year4000.mapnodes.MapNodes;
import org.bukkit.Bukkit;

/** Clocker to allow easy code for clocks. */
@SuppressWarnings("all")
public abstract class Clocker {
    private int offset;
    @Getter
    private final int time;

    /** Run a clock for x amount of time. */
    public Clocker(int time) {
        this.time = time;
        this.offset = 0;
        run();
    }

    /** Run a clock for x amount of time after x amount of time. */
    public Clocker(int time, int offset) {
        this.time = time;
        this.offset = offset;
        run();
    }

    public void run() {
        for (int i = time; i >= 0; i--) {
            Bukkit.getScheduler().runTaskLater(MapNodes.getInst(), new Clock(i), offset * 20);
            offset++;
        }
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {}

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {}

    public class Clock implements Runnable {
        private int index;

        protected Clock(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            if (index != 0)
                runTock(index);
            else
                runLast(index);
        }
    }
}
