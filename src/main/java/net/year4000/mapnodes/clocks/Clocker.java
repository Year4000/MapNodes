package net.year4000.mapnodes.clocks;

import lombok.Getter;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.scheduler.BukkitTask;

/** Clocker to allow easy code for clocks */
public abstract class Clocker {
    private int offset;
    @Getter
    private int time;
    @Getter
    private Clock clock;

    /** Run a clock for x amount of time */
    public Clocker(int time) {
        this(time, 0);
    }

    /** Run a clock for x amount of time after x amount of time */
    public Clocker(int time, int offset) {
        this.time = time;
        this.offset = offset;
    }

    public BukkitTask run() {
        clock = new Clock(time);
        clock.task = SchedulerUtil.repeatSync(clock, offset);

        return clock.task;
    }

    /** Simple math formula to convert ticks to secs. */
    public int sec(int ticks) {
        return ticks / 20 + 1;
    }

    /** Code to ran each clock tock */
    public void runFirst(int position) {}

    /** Code to ran each clock tock */
    public abstract void runTock(int position);

    /** Code to be ran on the last clock tick */
    public void runLast(int position) {}

    public void reduceTime(int time) {
        clock.index -= MathUtil.ticks(time);
    }

    public void increaseTime(int time) {
        clock.index += MathUtil.ticks(time);
    }

    public class Clock implements Runnable {
        public BukkitTask task;
        @Getter
        private int index;

        protected Clock(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            if (index <= 0) {
                runLast(index);
                task.cancel();
            }
            else if (index == time) {
                runFirst(index);
            }
            else {
                runTock(index);
            }

            index--;
        }
    }
}

