package net.year4000.mapnodes.gamemodes.destory;

import lombok.Data;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.exceptions.InvalidJsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
@GameModeConfigName("destroy")
public class DestroyConfig implements GameModeConfig {
    private List<DestroyTarget> targets = new ArrayList<>();

    @Override
    public void validate() throws InvalidJsonException {
        targets.forEach(DestroyTarget::validate);
    }

    public List<DestroyTarget> getTeamTargets(String team) {
        return targets.stream().filter(target -> target.getOwner().equalsIgnoreCase(team)).collect(Collectors.toList());
    }

    public List<DestroyTarget> getChallengerTargets(String team) {
        return targets.stream().filter(target -> target.getChallenger().equalsIgnoreCase(team)).collect(Collectors.toList());
    }
}
