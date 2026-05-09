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
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.StringUtils;

public class DemonAnimal extends Spider {
    private EntityType<?> type;

    public DemonAnimal(Level world, EntityType<? extends Animal> type) {
        super(EntityType.SPIDER, world);
        getEntityData().packDirty();

        this.type = type;
        this.lootTable = type.getDefaultLootTable();
    }

    @Override
    public Component getName() {
        return Component.literal(StringUtils.capitalize(type.toShortString()));
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
        return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(
                "entity.%s.ambient".formatted(type.toShortString())));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(
                "entity.%s.hurt".formatted(type.toShortString())));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(
                "entity.%s.death".formatted(type.toShortString())));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(
                "entity.%s.step".formatted(type.toShortString()))), 0.15F, 1.0F);
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
