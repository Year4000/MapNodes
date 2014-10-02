package net.year4000.mapnodes.utils.typewrappers;

import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class DamageCauseList<T> extends ArrayList<T> implements List<T> {

    public static DamageCauseList<EntityDamageEvent.DamageCause> createAllDamageCauses() {
        DamageCauseList<EntityDamageEvent.DamageCause> causes = new DamageCauseList<>();

        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            causes.add(cause);
        }

        return causes;
    }
}
