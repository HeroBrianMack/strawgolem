package com.t2pellet.strawgolem.mixin;

import com.mojang.authlib.GameProfile;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.StrawGolemOrderer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerMixin implements StrawGolemOrderer {

    private static final EntityDataAccessor<Integer> GOLEM_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineGolemID(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        player.getEntityData().define(GOLEM_ID, -1);
    }

    @Override
    public Optional<StrawGolem> getOrderedGolem() {
        Player player = (Player) (Object) this;
        int golemID = player.getEntityData().get(GOLEM_ID);
        if (golemID > 0) {
            StrawGolem golem = (StrawGolem) player.level().getEntity(golemID);
            return Optional.ofNullable(golem);
        }
        return Optional.empty();
    }

    @Override
    public void setOrderedGolem(@Nullable StrawGolem golem) {
        Player player = (Player) (Object) this;
        if (golem == null) {
            player.getEntityData().set(GOLEM_ID, -1);
        } else {
            int id = golem.getId();
            player.getEntityData().set(GOLEM_ID, id);
        }
    }
}
