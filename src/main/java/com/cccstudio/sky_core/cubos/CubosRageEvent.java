package com.cccstudio.sky_core.cubos;

import com.cccstudio.sky_core.api.mythical_event.IMythicalEvent;
import com.cccstudio.sky_core.api.mythical_event.MythicalEventContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Range;
import java.util.ArrayList;
import java.util.List;

//TODO rage event
public class CubosRageEvent implements IMythicalEvent {

    @Override
    public @Range(from = 1, to = 100) int getWeight() {
        return 40;
    }

    @Override
    public boolean isApplicable(MinecraftServer server) {
        for(ServerPlayer player : server.getPlayerList().getPlayers()) {
            if(player.getData(CubosGod.CUBOS.getPoints()) < -10) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(MythicalEventContext context) {
        List<ServerPlayer> provokingPlayers = new ArrayList<>();
        for(ServerPlayer player : context.getServer().getPlayerList().getPlayers()) {
            if(player.getData(CubosGod.CUBOS.getPoints()) < -5) {
                provokingPlayers.add(player);
            }
        }
        context.write("provokingPlayer", provokingPlayers);
    }

    @Override
    public void stop(MythicalEventContext context) {
        IMythicalEvent.super.stop(context);
    }

    @Override
    public void reward(MythicalEventContext context) {

    }

    @Override
    public void tick(MythicalEventContext context) {
        IMythicalEvent.super.tick(context);
    }

}
