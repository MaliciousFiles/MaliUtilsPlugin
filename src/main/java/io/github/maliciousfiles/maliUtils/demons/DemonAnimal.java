package io.github.maliciousfiles.maliUtils.demons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DemonAnimal extends Spider {
    private Animal entity;

    public DemonAnimal(Level world, Animal entity) {
        super(EntityType.SPIDER, world);
        getEntityData().packDirty();

        this.entity = entity;
        this.lootTable = entity.lootTable;
    }

    @Override
    public Component getName() {
        return entity.getName();
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
    public SoundEvent getAmbientSound() {
        return entity.getAmbientSound();
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return entity.getHurtSound(source);
    }

    @Override
    public SoundEvent getDeathSound() {
        return entity.getDeathSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        try {
            Method method = entity.getClass().getDeclaredMethod("playStepSound", BlockPos.class, BlockState.class);
            method.setAccessible(true);
            method.invoke(entity, pos, state);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

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
                entity.getType(),
                0,
                entityTrackerEntry.getLastSentMovement(),
                entityTrackerEntry.getLastSentYHeadRot()
        );
    }
}
