package net.minecraft.network;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class NetHandlerPlayServer implements INetHandlerPlayServer
{
    private static final Logger logger = LogManager.getLogger();
    public final NetworkManager netManager;
    private final MinecraftServer serverController;
    public EntityPlayerMP playerEntity;
    private int networkTickCount;
    /**
     * Used to keep track of how the player is floating while gamerules should prevent that. Surpassing 80 ticks means
     * kick
     */
    private int floatingTickCount;
    private boolean field_147366_g;
    private int field_147378_h;
    private long lastPingTime;
    private static Random field_147376_j = new Random();
    private long lastSentPingPacket;
    /**
     * Incremented by 20 each time a user sends a chat message, decreased by one every tick. Non-ops kicked when over
     * 200
     */
    private int chatSpamThresholdCount;
    private int itemDropThreshold;
    private IntHashMap field_147372_n = new IntHashMap();
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;
    private static final String __OBFID = "CL_00001452";

    public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP player)
    {
        this.serverController = server;
        this.netManager = networkManagerIn;
        networkManagerIn.setNetHandler(this);
        this.playerEntity = player;
        player.playerNetServerHandler = this;
    }

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive packets and in NetHandlerLoginServer
     * for a login-timeout
     */
    public void onNetworkTick()
    {
        this.field_147366_g = false;
        ++this.networkTickCount;
        this.serverController.theProfiler.startSection("keepAlive");

        if ((long)this.networkTickCount - this.lastSentPingPacket > 40L)
        {
            this.lastSentPingPacket = (long)this.networkTickCount;
            this.lastPingTime = this.currentTimeMillis();
            this.field_147378_h = (int)this.lastPingTime;
            this.sendPacket(new S00PacketKeepAlive(this.field_147378_h));
        }

        if (this.chatSpamThresholdCount > 0)
        {
            --this.chatSpamThresholdCount;
        }

        if (this.itemDropThreshold > 0)
        {
            --this.itemDropThreshold;
        }

        if (this.playerEntity.getLastActiveTime() > 0L && this.serverController.getMaxPlayerIdleMinutes() > 0 && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > (long)(this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60))
        {
            this.kickPlayerFromServer("You have been idle for too long!");
        }
    }

    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }

    /**
     * Kick a player from the server with a reason
     */
    public void kickPlayerFromServer(String reason)
    {
        final ChatComponentText chatcomponenttext = new ChatComponentText(reason);
        this.netManager.scheduleOutboundPacket(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener[] {new GenericFutureListener()
        {
            private static final String __OBFID = "CL_00001453";
            public void operationComplete(Future p_operationComplete_1_)
            {
                NetHandlerPlayServer.this.netManager.closeChannel(chatcomponenttext);
            }
        }
                                                                                                     });
        this.netManager.disableAutoRead();
    }

    /**
     * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
     * flying/sprinting
     */
    public void processInput(C0CPacketInput packetIn)
    {
        this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
    }

    /**
     * Processes clients perspective on player positioning and/or orientation
     */
    public void processPlayer(C03PacketPlayer packetIn)
    {
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        this.field_147366_g = true;

        if (!this.playerEntity.playerConqueredTheEnd)
        {
            double d0;

            if (!this.hasMoved)
            {
                d0 = packetIn.getPositionY() - this.lastPosY;

                if (packetIn.getPositionX() == this.lastPosX && d0 * d0 < 0.01D && packetIn.getPositionZ() == this.lastPosZ)
                {
                    this.hasMoved = true;
                }
            }

            if (this.hasMoved)
            {
                double d1;
                double d2;
                double d3;

                if (this.playerEntity.ridingEntity != null)
                {
                    float f4 = this.playerEntity.rotationYaw;
                    float f = this.playerEntity.rotationPitch;
                    this.playerEntity.ridingEntity.updateRiderPosition();
                    d1 = this.playerEntity.posX;
                    d2 = this.playerEntity.posY;
                    d3 = this.playerEntity.posZ;

                    if (packetIn.getRotating())
                    {
                        f4 = packetIn.getYaw();
                        f = packetIn.getPitch();
                    }

                    this.playerEntity.onGround = packetIn.func_149465_i();
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.yOffset2 = 0.0F;
                    this.playerEntity.setPositionAndRotation(d1, d2, d3, f4, f);

                    if (this.playerEntity.ridingEntity != null)
                    {
                        this.playerEntity.ridingEntity.updateRiderPosition();
                    }

                    if (!this.hasMoved) //Fixes teleportation kick while riding entities
                    {
                        return;
                    }

                    this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);

                    if (this.hasMoved)
                    {
                        this.lastPosX = this.playerEntity.posX;
                        this.lastPosY = this.playerEntity.posY;
                        this.lastPosZ = this.playerEntity.posZ;
                    }

                    worldserver.updateEntity(this.playerEntity);
                    return;
                }

                if (this.playerEntity.isPlayerSleeping())
                {
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    worldserver.updateEntity(this.playerEntity);
                    return;
                }

                d0 = this.playerEntity.posY;
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                d1 = this.playerEntity.posX;
                d2 = this.playerEntity.posY;
                d3 = this.playerEntity.posZ;
                float f1 = this.playerEntity.rotationYaw;
                float f2 = this.playerEntity.rotationPitch;

                if (packetIn.func_149466_j() && packetIn.getPositionY() == -999.0D && packetIn.getStance() == -999.0D)
                {
                    packetIn.func_149469_a(false);
                }

                double d4;

                if (packetIn.func_149466_j())
                {
                    d1 = packetIn.getPositionX();
                    d2 = packetIn.getPositionY();
                    d3 = packetIn.getPositionZ();
                    d4 = packetIn.getStance() - packetIn.getPositionY();

                    if (!this.playerEntity.isPlayerSleeping() && (d4 > 1.65D || d4 < 0.1D))
                    {
                        this.kickPlayerFromServer("Illegal stance");
                        logger.warn(this.playerEntity.getCommandSenderName() + " had an illegal stance: " + d4);
                        return;
                    }

                    if (Math.abs(packetIn.getPositionX()) > 3.2E7D || Math.abs(packetIn.getPositionZ()) > 3.2E7D)
                    {
                        this.kickPlayerFromServer("Illegal position");
                        return;
                    }
                }

                if (packetIn.getRotating())
                {
                    f1 = packetIn.getYaw();
                    f2 = packetIn.getPitch();
                }

                this.playerEntity.onUpdateEntity();
                this.playerEntity.yOffset2 = 0.0F;
                this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);

                if (!this.hasMoved)
                {
                    return;
                }

                d4 = d1 - this.playerEntity.posX;
                double d5 = d2 - this.playerEntity.posY;
                double d6 = d3 - this.playerEntity.posZ;
                //BUGFIX: min -> max, grabs the highest distance
                double d7 = Math.max(Math.abs(d4), Math.abs(this.playerEntity.motionX));
                double d8 = Math.max(Math.abs(d5), Math.abs(this.playerEntity.motionY));
                double d9 = Math.max(Math.abs(d6), Math.abs(this.playerEntity.motionZ));
                double d10 = d7 * d7 + d8 * d8 + d9 * d9;

                if (d10 > 100.0D && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getCommandSenderName())))
                {
                    logger.warn(this.playerEntity.getCommandSenderName() + " moved too quickly! " + d4 + "," + d5 + "," + d6 + " (" + d7 + ", " + d8 + ", " + d9 + ")");
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    return;
                }

                float f3 = 0.0625F;
                boolean flag = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)f3, (double)f3, (double)f3)).isEmpty();

                if (this.playerEntity.onGround && !packetIn.func_149465_i() && d5 > 0.0D)
                {
                    this.playerEntity.jump();
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.moveEntity(d4, d5, d6);
                this.playerEntity.onGround = packetIn.func_149465_i();
                this.playerEntity.addMovementStat(d4, d5, d6);
                double d11 = d5;
                d4 = d1 - this.playerEntity.posX;
                d5 = d2 - this.playerEntity.posY;

                if (d5 > -0.5D || d5 < 0.5D)
                {
                    d5 = 0.0D;
                }

                d6 = d3 - this.playerEntity.posZ;
                d10 = d4 * d4 + d5 * d5 + d6 * d6;
                boolean flag1 = false;

                if (d10 > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative())
                {
                    flag1 = true;
                    logger.warn(this.playerEntity.getCommandSenderName() + " moved wrongly!");
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.setPositionAndRotation(d1, d2, d3, f1, f2);
                boolean flag2 = worldserver.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract((double)f3, (double)f3, (double)f3)).isEmpty();

                if (flag && (flag1 || !flag2) && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.noClip)
                {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, f1, f2);
                    return;
                }

                AxisAlignedBB axisalignedbb = this.playerEntity.boundingBox.copy().expand((double)f3, (double)f3, (double)f3).addCoord(0.0D, -0.55D, 0.0D);

                if (!this.serverController.isFlightAllowed() && !this.playerEntity.theItemInWorldManager.isCreative() && !worldserver.checkBlockCollision(axisalignedbb) && !this.playerEntity.capabilities.allowFlying)
                {
                    if (d11 >= -0.03125D)
                    {
                        ++this.floatingTickCount;

                        if (this.floatingTickCount > 80)
                        {
                            logger.warn(this.playerEntity.getCommandSenderName() + " was kicked for floating too long!");
                            this.kickPlayerFromServer("Flying is not enabled on this server");
                            return;
                        }
                    }
                }
                else
                {
                    this.floatingTickCount = 0;
                }

                if (!this.hasMoved) //Fixes "Moved Too Fast" kick when being teleported while moving
                {
                    return;
                }

                this.playerEntity.onGround = packetIn.func_149465_i();
                this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                this.playerEntity.handleFalling(this.playerEntity.posY - d0, packetIn.func_149465_i());
            }
            else if (this.networkTickCount % 20 == 0)
            {
                this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
        }
    }

    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch)
    {
        this.hasMoved = false;
        this.lastPosX = x;
        this.lastPosY = y;
        this.lastPosZ = z;
        this.playerEntity.setPositionAndRotation(x, y, z, yaw, pitch);
        this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y + 1.6200000047683716D, z, yaw, pitch, false));
    }

    /**
     * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items?. (0:
     * initiated, 1: reinitiated, 2? , 3-4 drop item (respectively without or with player control), 5: stopped; x,y,z,
     * side clicked on;)
     */
    public void processPlayerDigging(C07PacketPlayerDigging packetIn)
    {
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        this.playerEntity.markPlayerActive();

        if (packetIn.getDiggedBlockStatus() == 4)
        {
            this.playerEntity.dropOneItem(false);
        }
        else if (packetIn.getDiggedBlockStatus() == 3)
        {
            this.playerEntity.dropOneItem(true);
        }
        else if (packetIn.getDiggedBlockStatus() == 5)
        {
            this.playerEntity.stopUsingItem();
        }
        else
        {
            boolean flag = false;

            if (packetIn.getDiggedBlockStatus() == 0)
            {
                flag = true;
            }

            if (packetIn.getDiggedBlockStatus() == 1)
            {
                flag = true;
            }

            if (packetIn.getDiggedBlockStatus() == 2)
            {
                flag = true;
            }

            int i = packetIn.getDiggedBlockX();
            int j = packetIn.getDiggedBlockY();
            int k = packetIn.getDiggedBlockZ();

            if (flag)
            {
                double d0 = this.playerEntity.posX - ((double)i + 0.5D);
                double d1 = this.playerEntity.posY - ((double)j + 0.5D) + 1.5D;
                double d2 = this.playerEntity.posZ - ((double)k + 0.5D);
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                double dist = playerEntity.theItemInWorldManager.getBlockReachDistance() + 1;
                dist *= dist;

                if (d3 > dist)
                {
                    return;
                }

                if (j >= this.serverController.getBuildLimit())
                {
                    return;
                }
            }

            if (packetIn.getDiggedBlockStatus() == 0)
            {
                if (!this.serverController.isBlockProtected(worldserver, i, j, k, this.playerEntity))
                {
                    this.playerEntity.theItemInWorldManager.onBlockClicked(i, j, k, packetIn.getDiggingBlockFace());
                }
                else
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(i, j, k, worldserver));
                }
            }
            else if (packetIn.getDiggedBlockStatus() == 2)
            {
                this.playerEntity.theItemInWorldManager.blockRemoving(i, j, k);

                if (worldserver.getBlock(i, j, k).getMaterial() != Material.air)
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(i, j, k, worldserver));
                }
            }
            else if (packetIn.getDiggedBlockStatus() == 1)
            {
                this.playerEntity.theItemInWorldManager.cancelDestroyingBlock(i, j, k);

                if (worldserver.getBlock(i, j, k).getMaterial() != Material.air)
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(i, j, k, worldserver));
                }
            }
        }
    }

    /**
     * Processes block placement and block activation (anvil, furnace, etc.)
     */
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn)
    {
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();
        boolean flag = false;
        boolean placeResult = true;
        int i = packetIn.getPlacedBlockX();
        int j = packetIn.getPlacedBlockY();
        int k = packetIn.getPlacedBlockZ();
        int l = packetIn.getPlacedBlockDirection();
        this.playerEntity.markPlayerActive();

        if (packetIn.getPlacedBlockDirection() == 255)
        {
            if (itemstack == null)
            {
                return;
            }

            PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(playerEntity, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, worldserver);
            if (event.useItem != Event.Result.DENY)
            {
                this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldserver, itemstack);
            }
        }
        else if (packetIn.getPlacedBlockY() >= this.serverController.getBuildLimit() - 1 && (packetIn.getPlacedBlockDirection() == 1 || packetIn.getPlacedBlockY() >= this.serverController.getBuildLimit()))
        {
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("build.tooHigh", new Object[] {Integer.valueOf(this.serverController.getBuildLimit())});
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(chatcomponenttranslation));
            flag = true;
        }
        else
        {
            double dist = playerEntity.theItemInWorldManager.getBlockReachDistance() + 1;
            dist *= dist;
            if (this.hasMoved && this.playerEntity.getDistanceSq((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D) < dist && !this.serverController.isBlockProtected(worldserver, i, j, k, this.playerEntity))
            {
                // record block place result so we can update client itemstack size if place event was cancelled.
                if (!this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, worldserver, itemstack, i, j, k, l, packetIn.getPlacedBlockOffsetX(), packetIn.getPlacedBlockOffsetY(), packetIn.getPlacedBlockOffsetZ()))
                {
                    placeResult = false;
                }
            }

            flag = true;
        }

        if (flag)
        {
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(i, j, k, worldserver));

            if (l == 0)
            {
                --j;
            }

            if (l == 1)
            {
                ++j;
            }

            if (l == 2)
            {
                --k;
            }

            if (l == 3)
            {
                ++k;
            }

            if (l == 4)
            {
                --i;
            }

            if (l == 5)
            {
                ++i;
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(i, j, k, worldserver));
        }

        itemstack = this.playerEntity.inventory.getCurrentItem();

        if (itemstack != null && itemstack.stackSize == 0)
        {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            itemstack = null;
        }

        if (itemstack == null || itemstack.getMaxItemUseDuration() == 0)
        {
            this.playerEntity.isChangingQuantityOnly = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot slot = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.getStack()) || !placeResult) // force client itemstack update if place event was cancelled
            {
                this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, slot.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
        logger.info(this.playerEntity.getCommandSenderName() + " lost connection: " + reason);
        this.serverController.refreshStatusNextTick();
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.left", new Object[] {this.playerEntity.getFormattedCommandSenderName()});
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.serverController.getConfigurationManager().sendChatMsg(chatcomponenttranslation);
        this.playerEntity.mountEntityAndWakeUp();
        this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);

        if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner()))
        {
            logger.info("Stopping singleplayer server as player logged out");
            this.serverController.initiateShutdown();
        }
    }

    public void sendPacket(final Packet packetIn)
    {
        if (packetIn instanceof S02PacketChat)
        {
            S02PacketChat s02packetchat = (S02PacketChat)packetIn;
            EntityPlayer.EnumChatVisibility enumchatvisibility = this.playerEntity.func_147096_v();

            if (enumchatvisibility == EntityPlayer.EnumChatVisibility.HIDDEN)
            {
                return;
            }

            if (enumchatvisibility == EntityPlayer.EnumChatVisibility.SYSTEM && !s02packetchat.isChat())
            {
                return;
            }
        }

        try
        {
            this.netManager.scheduleOutboundPacket(packetIn, new GenericFutureListener[0]);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Sending packet");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Packet being sent");
            crashreportcategory.addCrashSectionCallable("Packet class", new Callable()
            {
                private static final String __OBFID = "CL_00001454";
                public String call()
                {
                    return packetIn.getClass().getCanonicalName();
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Updates which quickbar slot is selected
     */
    public void processHeldItemChange(C09PacketHeldItemChange packetIn)
    {
        if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize())
        {
            this.playerEntity.inventory.currentItem = packetIn.getSlotId();
            this.playerEntity.markPlayerActive();
        }
        else
        {
            logger.warn(this.playerEntity.getCommandSenderName() + " tried to set an invalid carried item");
        }
    }

    /**
     * Process chat messages (broadcast back to clients) and commands (executes)
     */
    public void processChatMessage(C01PacketChatMessage packetIn)
    {
        if (this.playerEntity.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.cannotSend", new Object[0]);
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            this.sendPacket(new S02PacketChat(chatcomponenttranslation));
        }
        else
        {
            this.playerEntity.markPlayerActive();
            String s = packetIn.getMessage();
            s = StringUtils.normalizeSpace(s);

            for (int i = 0; i < s.length(); ++i)
            {
                if (!ChatAllowedCharacters.isAllowedCharacter(s.charAt(i)))
                {
                    this.kickPlayerFromServer("Illegal characters in chat");
                    return;
                }
            }

            if (s.startsWith("/"))
            {
                this.handleSlashCommand(s);
            }
            else
            {
                ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("chat.type.text", new Object[] {this.playerEntity.getFormattedCommandSenderName(), ForgeHooks.newChatWithLinks(s)}); // Fixes chat links
                chatcomponenttranslation1 = ForgeHooks.onServerChatEvent(this, s, chatcomponenttranslation1);
                if (chatcomponenttranslation1 == null) return;
                this.serverController.getConfigurationManager().sendChatMsgImpl(chatcomponenttranslation1, false);
            }

            this.chatSpamThresholdCount += 20;

            if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile()))
            {
                this.kickPlayerFromServer("disconnect.spam");
            }
        }
    }

    /**
     * Handle commands that start with a /
     */
    private void handleSlashCommand(String command)
    {
        this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
    }

    /**
     * Processes the player swinging its held item
     */
    public void processAnimation(C0APacketAnimation packetIn)
    {
        this.playerEntity.markPlayerActive();

        if (packetIn.getType() == 1)
        {
            this.playerEntity.swingItem();
        }
    }

    /**
     * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
     * height of the horse the player is riding
     */
    public void processEntityAction(C0BPacketEntityAction packetIn)
    {
        this.playerEntity.markPlayerActive();

        if (packetIn.func_149513_d() == 1)
        {
            this.playerEntity.setSneaking(true);
        }
        else if (packetIn.func_149513_d() == 2)
        {
            this.playerEntity.setSneaking(false);
        }
        else if (packetIn.func_149513_d() == 4)
        {
            this.playerEntity.setSprinting(true);
        }
        else if (packetIn.func_149513_d() == 5)
        {
            this.playerEntity.setSprinting(false);
        }
        else if (packetIn.func_149513_d() == 3)
        {
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
        }
        else if (packetIn.func_149513_d() == 6)
        {
            if (this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse)
            {
                ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.func_149512_e());
            }
        }
        else if (packetIn.func_149513_d() == 7 && this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse)
        {
            ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
        }
    }

    /**
     * Processes interactions ((un)leashing, opening command block GUI) and attacks on an entity with players currently
     * equipped item
     */
    public void processUseEntity(C02PacketUseEntity packetIn)
    {
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        Entity entity = packetIn.getEntityFromWorld(worldserver);
        this.playerEntity.markPlayerActive();

        if (entity != null)
        {
            boolean flag = this.playerEntity.canEntityBeSeen(entity);
            double d0 = 36.0D;

            if (!flag)
            {
                d0 = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(entity) < d0)
            {
                if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT)
                {
                    this.playerEntity.interactWith(entity);
                }
                else if (packetIn.getAction() == C02PacketUseEntity.Action.ATTACK)
                {
                    if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == this.playerEntity)
                    {
                        this.kickPlayerFromServer("Attempting to attack an invalid entity");
                        this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(entity);
                }
            }
        }
    }

    /**
     * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or
     * acquiring 'open inventory' achievement
     */
    public void processClientStatus(C16PacketClientStatus packetIn)
    {
        this.playerEntity.markPlayerActive();
        C16PacketClientStatus.EnumState enumstate = packetIn.getStatus();

        switch (NetHandlerPlayServer.SwitchEnumState.field_151290_a[enumstate.ordinal()])
        {
            case 1:
                if (this.playerEntity.playerConqueredTheEnd)
                {
                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
                }
                else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
                {
                    if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner()))
                    {
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                        this.serverController.deleteWorldAndStopServer();
                    }
                    else
                    {
                        UserListBansEntry userlistbansentry = new UserListBansEntry(this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore");
                        this.serverController.getConfigurationManager().getBannedPlayers().addEntry(userlistbansentry);
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    }
                }
                else
                {
                    if (this.playerEntity.getHealth() > 0.0F)
                    {
                        return;
                    }

                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, playerEntity.dimension, false);
                }

                break;
            case 2:
                this.playerEntity.getStatFile().func_150876_a(this.playerEntity);
                break;
            case 3:
                this.playerEntity.triggerAchievement(AchievementList.openInventory);
        }
    }

    /**
     * Processes the client closing windows (container)
     */
    public void processCloseWindow(C0DPacketCloseWindow packetIn)
    {
        this.playerEntity.closeContainer();
    }

    /**
     * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
     * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
     * the same open container/inventory
     */
    public void processClickWindow(C0EPacketClickWindow packetIn)
    {
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity))
        {
            ItemStack itemstack = this.playerEntity.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getMode(), this.playerEntity);

            if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), itemstack))
            {
                this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
                this.playerEntity.isChangingQuantityOnly = true;
                this.playerEntity.openContainer.detectAndSendChanges();
                this.playerEntity.updateHeldItem();
                this.playerEntity.isChangingQuantityOnly = false;
            }
            else
            {
                this.field_147372_n.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(packetIn.getActionNumber()));
                this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), false));
                this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
                ArrayList arraylist = new ArrayList();

                for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); ++i)
                {
                    arraylist.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(i)).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arraylist);
            }
        }
    }

    /**
     * Enchants the item identified by the packet given some convoluted conditions (matching window, which
     * should/shouldn't be in use?)
     */
    public void processEnchantItem(C11PacketEnchantItem packetIn)
    {
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity))
        {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Update the server with an ItemStack in a slot.
     */
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn)
    {
        if (this.playerEntity.theItemInWorldManager.isCreative())
        {
            boolean flag = packetIn.getSlotId() < 0;
            ItemStack itemstack = packetIn.getStack();
            boolean flag1 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
            boolean flag2 = itemstack == null || itemstack.getItem() != null;
            boolean flag3 = itemstack == null || itemstack.getMetadata() >= 0 && itemstack.stackSize <= 64 && itemstack.stackSize > 0;

            if (flag1 && flag2 && flag3)
            {
                if (itemstack == null)
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), (ItemStack)null);
                }
                else
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), itemstack);
                }

                this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
            }
            else if (flag && flag2 && flag3 && this.itemDropThreshold < 200)
            {
                this.itemDropThreshold += 20;
                EntityItem entityitem = this.playerEntity.dropPlayerItemWithRandomChoice(itemstack, true);

                if (entityitem != null)
                {
                    entityitem.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    /**
     * Received in response to the server requesting to confirm that the client-side open container matches the servers'
     * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
     * contents
     */
    public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn)
    {
        Short oshort = (Short)this.field_147372_n.lookup(this.playerEntity.openContainer.windowId);

        if (oshort != null && packetIn.getUid() == oshort.shortValue() && this.playerEntity.openContainer.windowId == packetIn.getId() && !this.playerEntity.openContainer.getCanCraft(this.playerEntity))
        {
            this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
        }
    }

    public void processUpdateSign(C12PacketUpdateSign packetIn)
    {
        this.playerEntity.markPlayerActive();
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);

        if (worldserver.blockExists(packetIn.getX(), packetIn.getY(), packetIn.getZ()))
        {
            TileEntity tileentity = worldserver.getTileEntity(packetIn.getX(), packetIn.getY(), packetIn.getZ());

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (!tileentitysign.getIsEditable() || tileentitysign.func_145911_b() != this.playerEntity)
                {
                    this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            int i;
            int j;

            for (j = 0; j < 4; ++j)
            {
                boolean flag = true;

                if (packetIn.getLines()[j].length() > 15)
                {
                    flag = false;
                }
                else
                {
                    for (i = 0; i < packetIn.getLines()[j].length(); ++i)
                    {
                        if (!ChatAllowedCharacters.isAllowedCharacter(packetIn.getLines()[j].charAt(i)))
                        {
                            flag = false;
                        }
                    }
                }

                if (!flag)
                {
                    packetIn.getLines()[j] = "!?";
                }
            }

            if (tileentity instanceof TileEntitySign)
            {
                j = packetIn.getX();
                int k = packetIn.getY();
                i = packetIn.getZ();
                TileEntitySign tileentitysign1 = (TileEntitySign)tileentity;
                System.arraycopy(packetIn.getLines(), 0, tileentitysign1.signText, 0, 4);
                tileentitysign1.markDirty();
                worldserver.markBlockForUpdate(j, k, i);
            }
        }
    }

    /**
     * Updates a players' ping statistics
     */
    public void processKeepAlive(C00PacketKeepAlive packetIn)
    {
        if (packetIn.getKey() == this.field_147378_h)
        {
            int i = (int)(this.currentTimeMillis() - this.lastPingTime);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + i) / 4;
        }
    }

    private long currentTimeMillis()
    {
        return System.nanoTime() / 1000000L;
    }

    /**
     * Processes a player starting/stopping flying
     */
    public void processPlayerAbilities(C13PacketPlayerAbilities packetIn)
    {
        this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
    }

    /**
     * Retrieves possible tab completions for the requested command string and sends them to the client
     */
    public void processTabComplete(C14PacketTabComplete packetIn)
    {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.serverController.getPossibleCompletions(this.playerEntity, packetIn.getMessage()).iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            arraylist.add(s);
        }

        this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[])arraylist.toArray(new String[arraylist.size()])));
    }

    /**
     * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
     * and whether to show the cape
     */
    public void processClientSettings(C15PacketClientSettings packetIn)
    {
        this.playerEntity.func_147100_a(packetIn);
    }

    /**
     * Synchronizes serverside and clientside book contents and signing
     */
    public void processVanilla250Packet(C17PacketCustomPayload packetIn)
    {
        PacketBuffer packetbuffer;
        ItemStack itemstack;
        ItemStack itemstack1;

        if ("MC|BEdit".equals(packetIn.getChannel()))
        {
            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

            try
            {
                itemstack = packetbuffer.readItemStackFromBuffer();

                if (itemstack != null)
                {
                    if (!ItemWritableBook.validBookPageTagContents(itemstack.getTagCompound()))
                    {
                        throw new IOException("Invalid book tag!");
                    }

                    itemstack1 = this.playerEntity.inventory.getCurrentItem();

                    if (itemstack1 == null)
                    {
                        return;
                    }

                    if (itemstack.getItem() == Items.writable_book && itemstack.getItem() == itemstack1.getItem())
                    {
                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                    }

                    return;
                }
            }
            catch (Exception exception4)
            {
                logger.error("Couldn\'t handle book info", exception4);
                return;
            }
            finally
            {
                packetbuffer.release();
            }

            return;
        }
        else if ("MC|BSign".equals(packetIn.getChannel()))
        {
            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

            try
            {
                itemstack = packetbuffer.readItemStackFromBuffer();

                if (itemstack == null)
                {
                    return;
                }

                if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.playerEntity.inventory.getCurrentItem();

                if (itemstack1 != null)
                {
                    if (itemstack.getItem() == Items.written_book && itemstack1.getItem() == Items.writable_book)
                    {
                        itemstack1.setTagInfo("author", new NBTTagString(this.playerEntity.getCommandSenderName()));
                        itemstack1.setTagInfo("title", new NBTTagString(itemstack.getTagCompound().getString("title")));
                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                        itemstack1.setItem(Items.written_book);
                    }

                    return;
                }
            }
            catch (Exception exception3)
            {
                logger.error("Couldn\'t sign book", exception3);
                return;
            }
            finally
            {
                packetbuffer.release();
            }

            return;
        }
        else
        {
            DataInputStream datainputstream;
            int i;

            if ("MC|TrSel".equals(packetIn.getChannel()))
            {
                try
                {
                    datainputstream = new DataInputStream(new ByteArrayInputStream(packetIn.getData()));
                    i = datainputstream.readInt();
                    Container container = this.playerEntity.openContainer;

                    if (container instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)container).setCurrentRecipeIndex(i);
                    }
                }
                catch (Exception exception2)
                {
                    logger.error("Couldn\'t select trade", exception2);
                }
            }
            else if ("MC|AdvCdm".equals(packetIn.getChannel()))
            {
                if (!this.serverController.isCommandBlockEnabled())
                {
                    this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
                }
                else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode)
                {
                    packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

                    try
                    {
                        byte b0 = packetbuffer.readByte();
                        CommandBlockLogic commandblocklogic = null;

                        if (b0 == 0)
                        {
                            TileEntity tileentity = this.playerEntity.worldObj.getTileEntity(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());

                            if (tileentity instanceof TileEntityCommandBlock)
                            {
                                commandblocklogic = ((TileEntityCommandBlock)tileentity).func_145993_a();
                            }
                        }
                        else if (b0 == 1)
                        {
                            Entity entity = this.playerEntity.worldObj.getEntityByID(packetbuffer.readInt());

                            if (entity instanceof EntityMinecartCommandBlock)
                            {
                                commandblocklogic = ((EntityMinecartCommandBlock)entity).func_145822_e();
                            }
                        }

                        String s1 = packetbuffer.readStringFromBuffer(packetbuffer.readableBytes());

                        if (commandblocklogic != null)
                        {
                            commandblocklogic.setCommand(s1);
                            commandblocklogic.func_145756_e();
                            this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", new Object[] {s1}));
                        }
                    }
                    catch (Exception exception1)
                    {
                        logger.error("Couldn\'t set command block", exception1);
                    }
                    finally
                    {
                        packetbuffer.release();
                    }
                }
                else
                {
                    this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
                }
            }
            else if ("MC|Beacon".equals(packetIn.getChannel()))
            {
                if (this.playerEntity.openContainer instanceof ContainerBeacon)
                {
                    try
                    {
                        datainputstream = new DataInputStream(new ByteArrayInputStream(packetIn.getData()));
                        i = datainputstream.readInt();
                        int j = datainputstream.readInt();
                        ContainerBeacon containerbeacon = (ContainerBeacon)this.playerEntity.openContainer;
                        Slot slot = containerbeacon.getSlot(0);

                        if (slot.getHasStack())
                        {
                            slot.decrStackSize(1);
                            TileEntityBeacon tileentitybeacon = containerbeacon.func_148327_e();
                            tileentitybeacon.setPrimaryEffect(i);
                            tileentitybeacon.setSecondaryEffect(j);
                            tileentitybeacon.markDirty();
                        }
                    }
                    catch (Exception exception)
                    {
                        logger.error("Couldn\'t set beacon", exception);
                    }
                }
            }
            else if ("MC|ItemName".equals(packetIn.getChannel()) && this.playerEntity.openContainer instanceof ContainerRepair)
            {
                ContainerRepair containerrepair = (ContainerRepair)this.playerEntity.openContainer;

                if (packetIn.getData() != null && packetIn.getData().length >= 1)
                {
                    String s = ChatAllowedCharacters.filterAllowedCharacters(new String(packetIn.getData(), Charsets.UTF_8));

                    if (s.length() <= 30)
                    {
                        containerrepair.updateItemName(s);
                    }
                }
                else
                {
                    containerrepair.updateItemName("");
                }
            }
        }
    }

    /**
     * Allows validation of the connection state transition. Parameters: from, to (connection state). Typically throws
     * IllegalStateException or UnsupportedOperationException if validation fails
     */
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState)
    {
        if (newState != EnumConnectionState.PLAY)
        {
            throw new IllegalStateException("Unexpected change in protocol!");
        }
    }

    static final class SwitchEnumState
        {
            static final int[] field_151290_a = new int[C16PacketClientStatus.EnumState.values().length];
            private static final String __OBFID = "CL_00001455";

            static
            {
                try
                {
                    field_151290_a[C16PacketClientStatus.EnumState.PERFORM_RESPAWN.ordinal()] = 1;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    field_151290_a[C16PacketClientStatus.EnumState.REQUEST_STATS.ordinal()] = 2;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_151290_a[C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}