package io.github.maliciousfiles.maliUtils.demons;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

public class DemonAnimal extends Spider {
    private EntityType<?> type;

    public DemonAnimal(Level world, EntityType<?> type) {
        super(EntityType.SPIDER, world);
        getEntityData().packDirty();

        this.type = type;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 0;
    }

    @Override
    public void setClimbing(boolean climbing) {
        super.setClimbing(climbing);
        getEntityData().packDirty();
    }

    @Override
    public void setNoAi(boolean aiDisabled) {
        super.setNoAi(aiDisabled);
        getEntityData().packDirty();
    }

    @Override
    public void setAggressive(boolean attacking) {
        super.setAggressive(attacking);
        getEntityData().packDirty();
    }

    @Override
    public void setLeftHanded(boolean leftHanded) {
        super.setLeftHanded(leftHanded);
        getEntityData().packDirty();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
        return new ClientboundAddEntityPacket(
                getId(),
                getUUID(),
                trackingPosition().x(),
                trackingPosition().y(),
                trackingPosition().z(),
                entityTrackerEntry.getLastSentXRot(),
                entityTrackerEntry.getLastSentYRot(),
                type,
                0,
                entityTrackerEntry.getLastSentMovement(),
                entityTrackerEntry.getLastSentYHeadRot()
        );
    }
}
