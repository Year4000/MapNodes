package net.year4000.mapnodes.game.kits;

import com.google.gson.annotations.Since;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.TimeDuration;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect implements Validator {
    /** The id of the effect. */
    @Since(1.0)
    private PotionEffectType name;

    /** The time the effect will last in secs. */
    @Since(1.0)
    private TimeDuration duration;

    /** The level the effect will have. */
    @Since(1.0)
    private int level = 1;

    /** Decrease and translucent particle effects. */
    @Since(1.0)
    private boolean ambient = false;

    /** Wheather or not to show particles. */
    //private boolean showParticles = true;

    @Override
    public void validate() throws InvalidJsonException {
        if (duration == null) {
            throw new InvalidJsonException(Msg.util("settings.kit.effect.duration"));
        }

        if (level < 1) {
            throw new InvalidJsonException(Msg.util("settings.kit.effect.level"));
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    public PotionEffect makeEffect() {
        return new PotionEffect(name, duration.isInfinite() ? Integer.MAX_VALUE : duration.toTicks(), level, ambient);
    }
}
