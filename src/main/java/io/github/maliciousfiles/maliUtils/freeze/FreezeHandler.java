package io.github.maliciousfiles.maliUtils.freeze;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeHandler implements Listener {

    private static final Map<UUID, ServerGamePacketListenerImpl> connections = new HashMap<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        connections.remove(evt.getPlayer().getUniqueId());
    }

    public static void disable() {
        connections.values().forEach(connection -> connection.player.connection = connection);
        connections.clear();
    }

    public static boolean toggle(Player player) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();

        if (connections.containsKey(player.getUniqueId())) {
            sp.connection = connections.remove(player.getUniqueId());

            try {
                Field field = Connection.class.getDeclaredField("packetListener");
                field.setAccessible(true);
                field.set(sp.connection.connection, sp.connection);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            connections.put(player.getUniqueId(), sp.connection);
            sp.connection = new FreezeConnection(sp);
            try {
                Field field = Connection.class.getDeclaredField("packetListener");
                field.setAccessible(true);
                field.set(sp.connection.connection, sp.connection);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return connections.containsKey(player.getUniqueId());
    }

    private static class FreezeConnection extends ServerGamePacketListenerImpl {

        public FreezeConnection(ServerPlayer sp) {
            super(sp.level().getServer(), sp.connection.connection, sp, CommonListenerCookie.createInitial(sp.gameProfile, false));
        }

        @Override
        public void send(Packet<?> packet, ChannelFutureListener listener) {
            if (packet instanceof ClientboundKeepAlivePacket) super.send(packet, listener);
        }

        public void handlePlayerInput(ServerboundPlayerInputPacket packet) {}
        public void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {}
        public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {}
        public void handleAcceptPlayerLoad(ServerboundPlayerLoadedPacket packet) {}
        public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {}
        public void handleBundleItemSelectedPacket(ServerboundSelectBundleItemPacket packet) {}
        public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {}
        public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {}
        public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {}
        public void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {}
        public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {}
        public void handlePickItemFromBlock(ServerboundPickItemFromBlockPacket packet) {}
        public void handlePickItemFromEntity(ServerboundPickItemFromEntityPacket packet) {}
        public void handleRenameItem(ServerboundRenameItemPacket packet) {}
        public void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {}
        public void handleSetGameRule(ServerboundSetGameRulePacket packet) {}
        public void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {}
        public void handleSetTestBlock(ServerboundSetTestBlockPacket packet) {}
        public void handleTestInstanceBlockAction(ServerboundTestInstanceBlockActionPacket packet) {}
        public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {}
        public void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {}
        public void handleSelectTrade(ServerboundSelectTradePacket packet) {}
        public void handleEditBook(ServerboundEditBookPacket packet) {}
        public void handleEntityTagQuery(ServerboundEntityTagQueryPacket packet) {}
        public void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket packet) {}
        public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQueryPacket packet) {}
        public void handleMovePlayer(ServerboundMovePlayerPacket packet) {}
        public void handlePlayerAction(ServerboundPlayerActionPacket packet) {}
        public void handleUseItemOn(ServerboundUseItemOnPacket packet) {}
        public void handleUseItem(ServerboundUseItemPacket packet) {}
        public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {}
        public void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {}
        public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {}
        public void handleChat(ServerboundChatPacket packet) {}
        public void handleChatCommand(ServerboundChatCommandPacket packet) {}
        public void handleSignedChatCommand(ServerboundChatCommandSignedPacket packet) {}
        public void handleChatAck(ServerboundChatAckPacket packet) {}
        public void handleAnimate(ServerboundSwingPacket packet) {}
        public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {}
        public void handlePingRequest(ServerboundPingRequestPacket packet) {}
        public void handleAttack(ServerboundAttackPacket packet) {}
        public void handleInteract(ServerboundInteractPacket packet) {}
        public void handleSpectateEntity(ServerboundSpectateEntityPacket packet) {}
        public void handleClientCommand(ServerboundClientCommandPacket packet) {}
        public void handleContainerClose(ServerboundContainerClosePacket packet, InventoryCloseEvent.Reason reason) {}
        public void handleContainerClick(ServerboundContainerClickPacket packet) {}
        public void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {}
        public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {}
        public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {}
        public void handleSignUpdate(ServerboundSignUpdatePacket packet) {}
        public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {}
        public void handleClientInformation(ServerboundClientInformationPacket packet) {}
        public void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {}
        public void handleChangeGameMode(ServerboundChangeGameModePacket packet) {}
        public void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {}
        public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket packet) {}
        public void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket packet) {}
        public void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket packet) {}
        public void handleDebugSubscriptionRequest(ServerboundDebugSubscriptionRequestPacket packet) {}
        public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {}
        public void handleClientTickEnd(ServerboundClientTickEndPacket packet) {}
        public void handleResourcePackResponse(ServerboundResourcePackPacket packet) {}
        public void handleCookieResponse(ServerboundCookieResponsePacket packet) {}
    }
}
