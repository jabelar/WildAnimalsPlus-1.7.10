package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedOnlineScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger logger = LogManager.getLogger();
    /**
     * The NetworkManager instance used to communicate with the server (used only by handlePlayerPosLook to update
     * positioning and handleJoinGame to inform the server of the client distribution/mods)
     */
    private final NetworkManager netManager;
    /** Reference to the Minecraft instance, which many handler methods operate on */
    private Minecraft gameController;
    /** Reference to the current ClientWorld instance, which many handler methods operate on */
    private WorldClient clientWorldController;
    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of S08PacketPlayerPosLook,
     * reset upon respawning
     */
    private boolean doneLoadingTerrain;
    /** Origin of the central MapStorage serving as a public reference for WorldClient. Not used in this class */
    public MapStorage mapStorageOrigin = new MapStorage((ISaveHandler)null);
    /** A mapping from player names to their respective GuiPlayerInfo (specifies the clients response time to the server) */
    private Map playerInfoMap = new HashMap();
    /** An ArrayList of GuiPlayerInfo (includes all the players' GuiPlayerInfo on the current server) */
    public List playerInfoList = new ArrayList();
    public int currentServerMaxPlayers = 20;
    /**
     * Seems to be either null (integrated server) or an instance of either GuiMultiplayer (when connecting to a server)
     * or GuiScreenReamlsTOS (when connecting to MCO server)
     */
    private GuiScreen guiScreenServer;
    private boolean field_147308_k = false;
    /**
     * Just an ordinary random number generator, used to randomize audio pitch of item/orb pickup and randomize both
     * particlespawn offset and velocity
     */
    private Random avRandomizer = new Random();
    private static final String __OBFID = "CL_00000878";

    public NetHandlerPlayClient(Minecraft p_i45061_1_, GuiScreen p_i45061_2_, NetworkManager p_i45061_3_)
    {
        this.gameController = p_i45061_1_;
        this.guiScreenServer = p_i45061_2_;
        this.netManager = p_i45061_3_;
    }

    /**
     * Clears the WorldClient instance associated with this NetHandlerPlayClient
     */
    public void cleanup()
    {
        this.clientWorldController = null;
    }

    /**
     * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive packets and in NetHandlerLoginServer
     * for a login-timeout
     */
    public void onNetworkTick() {}

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    public void handleJoinGame(S01PacketJoinGame packetIn)
    {
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.func_149198_e(), false, packetIn.func_149195_d(), packetIn.func_149196_i()), NetworkDispatcher.get(getNetworkManager()).getOverrideDimension(packetIn), packetIn.func_149192_g(), this.gameController.mcProfiler);
        this.clientWorldController.isRemote = true;
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.thePlayer.dimension = packetIn.func_149194_f();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        this.gameController.thePlayer.setEntityId(packetIn.func_149197_c());
        this.currentServerMaxPlayers = packetIn.func_149193_h();
        this.gameController.playerController.setGameType(packetIn.func_149198_e());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.scheduleOutboundPacket(new C17PacketCustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)), new GenericFutureListener[0]);
    }

    /**
     * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
     */
    public void handleSpawnObject(S0EPacketSpawnObject packetIn)
    {
        double d0 = (double)packetIn.func_148997_d() / 32.0D;
        double d1 = (double)packetIn.func_148998_e() / 32.0D;
        double d2 = (double)packetIn.func_148994_f() / 32.0D;
        Object object = null;

        if (packetIn.func_148993_l() == 10)
        {
            object = EntityMinecart.createMinecart(this.clientWorldController, d0, d1, d2, packetIn.func_149009_m());
        }
        else if (packetIn.func_148993_l() == 90)
        {
            Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149009_m());

            if (entity instanceof EntityPlayer)
            {
                object = new EntityFishHook(this.clientWorldController, d0, d1, d2, (EntityPlayer)entity);
            }

            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 60)
        {
            object = new EntityArrow(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 61)
        {
            object = new EntitySnowball(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 71)
        {
            object = new EntityItemFrame(this.clientWorldController, (int)d0, (int)d1, (int)d2, packetIn.func_149009_m());
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 77)
        {
            object = new EntityLeashKnot(this.clientWorldController, (int)d0, (int)d1, (int)d2);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 65)
        {
            object = new EntityEnderPearl(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 72)
        {
            object = new EntityEnderEye(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 76)
        {
            object = new EntityFireworkRocket(this.clientWorldController, d0, d1, d2, (ItemStack)null);
        }
        else if (packetIn.func_148993_l() == 63)
        {
            object = new EntityLargeFireball(this.clientWorldController, d0, d1, d2, (double)packetIn.func_149010_g() / 8000.0D, (double)packetIn.func_149004_h() / 8000.0D, (double)packetIn.func_148999_i() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 64)
        {
            object = new EntitySmallFireball(this.clientWorldController, d0, d1, d2, (double)packetIn.func_149010_g() / 8000.0D, (double)packetIn.func_149004_h() / 8000.0D, (double)packetIn.func_148999_i() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 66)
        {
            object = new EntityWitherSkull(this.clientWorldController, d0, d1, d2, (double)packetIn.func_149010_g() / 8000.0D, (double)packetIn.func_149004_h() / 8000.0D, (double)packetIn.func_148999_i() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 62)
        {
            object = new EntityEgg(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 73)
        {
            object = new EntityPotion(this.clientWorldController, d0, d1, d2, packetIn.func_149009_m());
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 75)
        {
            object = new EntityExpBottle(this.clientWorldController, d0, d1, d2);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.func_148993_l() == 1)
        {
            object = new EntityBoat(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 50)
        {
            object = new EntityTNTPrimed(this.clientWorldController, d0, d1, d2, (EntityLivingBase)null);
        }
        else if (packetIn.func_148993_l() == 51)
        {
            object = new EntityEnderCrystal(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 2)
        {
            object = new EntityItem(this.clientWorldController, d0, d1, d2);
        }
        else if (packetIn.func_148993_l() == 70)
        {
            object = new EntityFallingBlock(this.clientWorldController, d0, d1, d2, Block.getBlockById(packetIn.func_149009_m() & 65535), packetIn.func_149009_m() >> 16);
            packetIn.func_149002_g(0);
        }

        if (object != null)
        {
            ((Entity)object).serverPosX = packetIn.func_148997_d();
            ((Entity)object).serverPosY = packetIn.func_148998_e();
            ((Entity)object).serverPosZ = packetIn.func_148994_f();
            ((Entity)object).rotationPitch = (float)(packetIn.func_149008_j() * 360) / 256.0F;
            ((Entity)object).rotationYaw = (float)(packetIn.func_149006_k() * 360) / 256.0F;
            Entity[] aentity = ((Entity)object).getParts();

            if (aentity != null)
            {
                int i = packetIn.func_149001_c() - ((Entity)object).getEntityId();

                for (int j = 0; j < aentity.length; ++j)
                {
                    aentity[j].setEntityId(aentity[j].getEntityId() + i);
                }
            }

            ((Entity)object).setEntityId(packetIn.func_149001_c());
            this.clientWorldController.addEntityToWorld(packetIn.func_149001_c(), (Entity)object);

            if (packetIn.func_149009_m() > 0)
            {
                if (packetIn.func_148993_l() == 60)
                {
                    Entity entity1 = this.clientWorldController.getEntityByID(packetIn.func_149009_m());

                    if (entity1 instanceof EntityLivingBase)
                    {
                        EntityArrow entityarrow = (EntityArrow)object;
                        entityarrow.shootingEntity = entity1;
                    }
                }

                ((Entity)object).setVelocity((double)packetIn.func_149010_g() / 8000.0D, (double)packetIn.func_149004_h() / 8000.0D, (double)packetIn.func_148999_i() / 8000.0D);
            }
        }
    }

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn)
    {
        EntityXPOrb entityxporb = new EntityXPOrb(this.clientWorldController, (double)packetIn.func_148984_d() / 32.0D, (double)packetIn.func_148983_e() / 32.0D, (double)packetIn.func_148982_f() / 32.0D, packetIn.func_148986_g());
        // FORGE: BugFix MC-12013 Wrong XP orb clientside spawn position
        entityxporb.serverPosX = packetIn.func_148984_d();
        entityxporb.serverPosY = packetIn.func_148983_e();
        entityxporb.serverPosZ = packetIn.func_148982_f();
        entityxporb.rotationYaw = 0.0F;
        entityxporb.rotationPitch = 0.0F;
        entityxporb.setEntityId(packetIn.func_148985_c());
        this.clientWorldController.addEntityToWorld(packetIn.func_148985_c(), entityxporb);
    }

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn)
    {
        double d0 = (double)packetIn.func_149051_d() / 32.0D;
        double d1 = (double)packetIn.func_149050_e() / 32.0D;
        double d2 = (double)packetIn.func_149049_f() / 32.0D;
        EntityLightningBolt entitylightningbolt = null;

        if (packetIn.func_149053_g() == 1)
        {
            entitylightningbolt = new EntityLightningBolt(this.clientWorldController, d0, d1, d2);
        }

        if (entitylightningbolt != null)
        {
            entitylightningbolt.serverPosX = packetIn.func_149051_d();
            entitylightningbolt.serverPosY = packetIn.func_149050_e();
            entitylightningbolt.serverPosZ = packetIn.func_149049_f();
            entitylightningbolt.rotationYaw = 0.0F;
            entitylightningbolt.rotationPitch = 0.0F;
            entitylightningbolt.setEntityId(packetIn.func_149052_c());
            this.clientWorldController.addWeatherEffect(entitylightningbolt);
        }
    }

    /**
     * Handles the spawning of a painting object
     */
    public void handleSpawnPainting(S10PacketSpawnPainting packetIn)
    {
        EntityPainting entitypainting = new EntityPainting(this.clientWorldController, packetIn.func_148964_d(), packetIn.func_148963_e(), packetIn.func_148962_f(), packetIn.func_148966_g(), packetIn.func_148961_h());
        this.clientWorldController.addEntityToWorld(packetIn.func_148965_c(), entitypainting);
    }

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149412_c());

        if (entity != null)
        {
            entity.setVelocity((double)packetIn.func_149411_d() / 8000.0D, (double)packetIn.func_149410_e() / 8000.0D, (double)packetIn.func_149409_f() / 8000.0D);
        }
    }

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149375_d());

        if (entity != null && packetIn.func_149376_c() != null)
        {
            entity.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
        }
    }

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn)
    {
        double d0 = (double)packetIn.func_148942_f() / 32.0D;
        double d1 = (double)packetIn.func_148949_g() / 32.0D;
        double d2 = (double)packetIn.func_148946_h() / 32.0D;
        float f = (float)(packetIn.func_148941_i() * 360) / 256.0F;
        float f1 = (float)(packetIn.func_148945_j() * 360) / 256.0F;
        GameProfile gameprofile = packetIn.func_148948_e();
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.gameController.theWorld, packetIn.func_148948_e());
        entityotherplayermp.prevPosX = entityotherplayermp.lastTickPosX = (double)(entityotherplayermp.serverPosX = packetIn.func_148942_f());
        entityotherplayermp.prevPosY = entityotherplayermp.lastTickPosY = (double)(entityotherplayermp.serverPosY = packetIn.func_148949_g());
        entityotherplayermp.prevPosZ = entityotherplayermp.lastTickPosZ = (double)(entityotherplayermp.serverPosZ = packetIn.func_148946_h());
        int i = packetIn.func_148947_k();

        if (i == 0)
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = null;
        }
        else
        {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = new ItemStack(Item.getItemById(i), 1, 0);
        }

        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.clientWorldController.addEntityToWorld(packetIn.func_148943_d(), entityotherplayermp);
        List list = packetIn.func_148944_c();

        if (list != null)
        {
            entityotherplayermp.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    public void handleEntityTeleport(S18PacketEntityTeleport packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149451_c());

        if (entity != null)
        {
            entity.serverPosX = packetIn.func_149449_d();
            entity.serverPosY = packetIn.func_149448_e();
            entity.serverPosZ = packetIn.func_149446_f();
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D + 0.015625D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = (float)(packetIn.func_149450_g() * 360) / 256.0F;
            float f1 = (float)(packetIn.func_149447_h() * 360) / 256.0F;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    public void handleHeldItemChange(S09PacketHeldItemChange packetIn)
    {
        if (packetIn.func_149385_c() >= 0 && packetIn.func_149385_c() < InventoryPlayer.getHotbarSize())
        {
            this.gameController.thePlayer.inventory.currentItem = packetIn.func_149385_c();
        }
    }

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    public void handleEntityMovement(S14PacketEntity packetIn)
    {
        Entity entity = packetIn.func_149065_a(this.clientWorldController);

        if (entity != null)
        {
            entity.serverPosX += packetIn.func_149062_c();
            entity.serverPosY += packetIn.func_149061_d();
            entity.serverPosZ += packetIn.func_149064_e();
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = packetIn.func_149060_h() ? (float)(packetIn.func_149066_f() * 360) / 256.0F : entity.rotationYaw;
            float f1 = packetIn.func_149060_h() ? (float)(packetIn.func_149063_g() * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3);
        }
    }

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn)
    {
        Entity entity = packetIn.func_149381_a(this.clientWorldController);

        if (entity != null)
        {
            float f = (float)(packetIn.func_149380_c() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    public void handleDestroyEntities(S13PacketDestroyEntities packetIn)
    {
        for (int i = 0; i < packetIn.func_149098_c().length; ++i)
        {
            this.clientWorldController.removeEntityFromWorld(packetIn.func_149098_c()[i]);
        }
    }

    /**
     * Handles changes in player positioning and rotation such as when travelling to a new dimension, (re)spawning,
     * mounting horses etc. Seems to immediately reply to the server with the clients post-processing perspective on the
     * player positioning
     */
    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;
        double d0 = packetIn.func_148932_c();
        double d1 = packetIn.func_148928_d();
        double d2 = packetIn.func_148933_e();
        float f = packetIn.func_148931_f();
        float f1 = packetIn.func_148930_g();
        entityclientplayermp.yOffset2 = 0.0F;
        entityclientplayermp.motionX = entityclientplayermp.motionY = entityclientplayermp.motionZ = 0.0D;
        entityclientplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.netManager.scheduleOutboundPacket(new C03PacketPlayer.C06PacketPlayerPosLook(entityclientplayermp.posX, entityclientplayermp.boundingBox.minY, entityclientplayermp.posY, entityclientplayermp.posZ, packetIn.func_148931_f(), packetIn.func_148930_g(), packetIn.func_148929_h()), new GenericFutureListener[0]);

        if (!this.doneLoadingTerrain)
        {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketChunkData
     */
    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn)
    {
        int i = packetIn.func_148920_c().chunkXPos * 16;
        int j = packetIn.func_148920_c().chunkZPos * 16;

        if (packetIn.func_148921_d() != null)
        {
            DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(packetIn.func_148921_d()));

            try
            {
                for (int k = 0; k < packetIn.func_148922_e(); ++k)
                {
                    short short1 = datainputstream.readShort();
                    short short2 = datainputstream.readShort();
                    int l = short2 >> 4 & 4095;
                    int i1 = short2 & 15;
                    int j1 = short1 >> 12 & 15;
                    int k1 = short1 >> 8 & 15;
                    int l1 = short1 & 255;
                    this.clientWorldController.func_147492_c(j1 + i, l1, k1 + j, Block.getBlockById(l), i1);
                }
            }
            catch (IOException ioexception)
            {
                ;
            }
        }
    }

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    public void handleChunkData(S21PacketChunkData packetIn)
    {
        if (packetIn.func_149274_i())
        {
            if (packetIn.func_149276_g() == 0)
            {
                this.clientWorldController.doPreChunk(packetIn.func_149273_e(), packetIn.func_149271_f(), false);
                return;
            }

            this.clientWorldController.doPreChunk(packetIn.func_149273_e(), packetIn.func_149271_f(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packetIn.func_149273_e() << 4, 0, packetIn.func_149271_f() << 4, (packetIn.func_149273_e() << 4) + 15, 256, (packetIn.func_149271_f() << 4) + 15);
        Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(packetIn.func_149273_e(), packetIn.func_149271_f());
        chunk.fillChunk(packetIn.func_149272_d(), packetIn.func_149276_g(), packetIn.func_149270_h(), packetIn.func_149274_i());
        this.clientWorldController.markBlockRangeForRenderUpdate(packetIn.func_149273_e() << 4, 0, packetIn.func_149271_f() << 4, (packetIn.func_149273_e() << 4) + 15, 256, (packetIn.func_149271_f() << 4) + 15);

        if (!packetIn.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface))
        {
            chunk.resetRelightChecks();
        }
    }

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    public void handleBlockChange(S23PacketBlockChange packetIn)
    {
        this.clientWorldController.func_147492_c(packetIn.func_148879_d(), packetIn.func_148878_e(), packetIn.func_148877_f(), packetIn.func_148880_c(), packetIn.func_148881_g());
    }

    /**
     * Closes the network channel
     */
    public void handleDisconnect(S40PacketDisconnect packetIn)
    {
        this.netManager.closeChannel(packetIn.func_149165_c());
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
        this.gameController.loadWorld((WorldClient)null);

        if (this.guiScreenServer != null)
        {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy)
            {
                this.gameController.displayGuiScreen((new DisconnectedOnlineScreen(((GuiScreenRealmsProxy)this.guiScreenServer).func_154321_a(), "disconnect.lost", reason)).getProxy());
            }
            else
            {
                this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
            }
        }
        else
        {
            this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
        }
    }

    public void addToSendQueue(Packet p_147297_1_)
    {
        this.netManager.scheduleOutboundPacket(p_147297_1_, new GenericFutureListener[0]);
    }

    public void handleCollectItem(S0DPacketCollectItem packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149354_c());
        Object object = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.func_149353_d());

        if (object == null)
        {
            object = this.gameController.thePlayer;
        }

        if (entity != null)
        {
            if (entity instanceof EntityXPOrb)
            {
                this.clientWorldController.playSoundAtEntity(entity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.clientWorldController.playSoundAtEntity(entity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.gameController.theWorld, entity, (Entity)object, -0.5F));
            this.clientWorldController.removeEntityFromWorld(packetIn.func_149354_c());
        }
    }

    /**
     * Prints a chatmessage in the chat GUI
     */
    public void handleChat(S02PacketChat packetIn)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(packetIn.func_148915_c());
        if (!MinecraftForge.EVENT_BUS.post(event) && event.message != null)
        {
            this.gameController.ingameGUI.getChatGUI().printChatMessage(event.message);
        }
    }

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    public void handleAnimation(S0BPacketAnimation packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_148978_c());

        if (entity != null)
        {
            if (packetIn.func_148977_d() == 0)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
                entitylivingbase.swingItem();
            }
            else if (packetIn.func_148977_d() == 1)
            {
                entity.performHurtAnimation();
            }
            else if (packetIn.func_148977_d() == 2)
            {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                entityplayer.wakeUpPlayer(false, false, false);
            }
            else if (packetIn.func_148977_d() == 4)
            {
                this.gameController.effectRenderer.addEffect(new EntityCrit2FX(this.gameController.theWorld, entity));
            }
            else if (packetIn.func_148977_d() == 5)
            {
                EntityCrit2FX entitycrit2fx = new EntityCrit2FX(this.gameController.theWorld, entity, "magicCrit");
                this.gameController.effectRenderer.addEffect(entitycrit2fx);
            }
        }
    }

    /**
     * Retrieves the player identified by the packet, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    public void handleUseBed(S0APacketUseBed packetIn)
    {
        packetIn.getPlayer(this.clientWorldController).sleepInBedAt(packetIn.getX(), packetIn.getY(), packetIn.getZ());
    }

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the packet
     */
    public void handleSpawnMob(S0FPacketSpawnMob packetIn)
    {
        double d0 = (double)packetIn.func_149023_f() / 32.0D;
        double d1 = (double)packetIn.func_149034_g() / 32.0D;
        double d2 = (double)packetIn.func_149029_h() / 32.0D;
        float f = (float)(packetIn.func_149028_l() * 360) / 256.0F;
        float f1 = (float)(packetIn.func_149030_m() * 360) / 256.0F;
        EntityLivingBase entitylivingbase = (EntityLivingBase)EntityList.createEntityByID(packetIn.func_149025_e(), this.gameController.theWorld);
        if (entitylivingbase == null)
        {
            cpw.mods.fml.common.FMLLog.info("Server attempted to spawn an unknown entity using ID: {0} at ({1}, {2}, {3}) Skipping!", packetIn.func_149025_e(), d0, d1, d2);
            return;
        }
        entitylivingbase.serverPosX = packetIn.func_149023_f();
        entitylivingbase.serverPosY = packetIn.func_149034_g();
        entitylivingbase.serverPosZ = packetIn.func_149029_h();
        entitylivingbase.rotationYawHead = (float)(packetIn.func_149032_n() * 360) / 256.0F;
        Entity[] aentity = entitylivingbase.getParts();

        if (aentity != null)
        {
            int i = packetIn.func_149024_d() - entitylivingbase.getEntityId();

            for (int j = 0; j < aentity.length; ++j)
            {
                aentity[j].setEntityId(aentity[j].getEntityId() + i);
            }
        }

        entitylivingbase.setEntityId(packetIn.func_149024_d());
        entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
        entitylivingbase.motionX = (double)((float)packetIn.func_149026_i() / 8000.0F);
        entitylivingbase.motionY = (double)((float)packetIn.func_149033_j() / 8000.0F);
        entitylivingbase.motionZ = (double)((float)packetIn.func_149031_k() / 8000.0F);
        this.clientWorldController.addEntityToWorld(packetIn.func_149024_d(), entitylivingbase);
        List list = packetIn.func_149027_c();

        if (list != null)
        {
            entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleTimeUpdate(S03PacketTimeUpdate packetIn)
    {
        this.gameController.theWorld.func_82738_a(packetIn.func_149366_c());
        this.gameController.theWorld.setWorldTime(packetIn.func_149365_d());
    }

    public void handleSpawnPosition(S05PacketSpawnPosition packetIn)
    {
        this.gameController.thePlayer.setSpawnChunk(new ChunkCoordinates(packetIn.func_149360_c(), packetIn.func_149359_d(), packetIn.func_149358_e()), true);
        this.gameController.theWorld.getWorldInfo().setSpawnPosition(packetIn.func_149360_c(), packetIn.func_149359_d(), packetIn.func_149358_e());
    }

    public void handleEntityAttach(S1BPacketEntityAttach packetIn)
    {
        Object object = this.clientWorldController.getEntityByID(packetIn.func_149403_d());
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149402_e());

        if (packetIn.func_149404_c() == 0)
        {
            boolean flag = false;

            if (packetIn.func_149403_d() == this.gameController.thePlayer.getEntityId())
            {
                object = this.gameController.thePlayer;

                if (entity instanceof EntityBoat)
                {
                    ((EntityBoat)entity).setIsBoatEmpty(false);
                }

                flag = ((Entity)object).ridingEntity == null && entity != null;
            }
            else if (entity instanceof EntityBoat)
            {
                ((EntityBoat)entity).setIsBoatEmpty(true);
            }

            if (object == null)
            {
                return;
            }

            ((Entity)object).mountEntity(entity);

            if (flag)
            {
                GameSettings gamesettings = this.gameController.gameSettings;
                this.gameController.ingameGUI.setRecordPlaying(I18n.format("mount.onboard", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindSneak.getKeyCode())}), false);
            }
        }
        else if (packetIn.func_149404_c() == 1 && object != null && object instanceof EntityLiving)
        {
            if (entity != null)
            {
                ((EntityLiving)object).setLeashedToEntity(entity, false);
            }
            else
            {
                ((EntityLiving)object).clearLeashed(false, false);
            }
        }
    }

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    public void handleEntityStatus(S19PacketEntityStatus packetIn)
    {
        Entity entity = packetIn.func_149161_a(this.clientWorldController);

        if (entity != null)
        {
            entity.handleHealthUpdate(packetIn.func_149160_c());
        }
    }

    public void handleUpdateHealth(S06PacketUpdateHealth packetIn)
    {
        this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
        this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    public void handleSetExperience(S1FPacketSetExperience packetIn)
    {
        this.gameController.thePlayer.setXPStats(packetIn.func_149397_c(), packetIn.func_149396_d(), packetIn.func_149395_e());
    }

    public void handleRespawn(S07PacketRespawn packetIn)
    {
        if (packetIn.func_149082_c() != this.gameController.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.func_149083_e(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.func_149080_f()), packetIn.func_149082_c(), packetIn.func_149081_d(), this.gameController.mcProfiler);
            this.clientWorldController.setWorldScoreboard(scoreboard);
            this.clientWorldController.isRemote = true;
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.thePlayer.dimension = packetIn.func_149082_c();
            this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.gameController.setDimensionAndSpawnPlayer(packetIn.func_149082_c());
        this.gameController.playerController.setGameType(packetIn.func_149083_e());
    }

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    public void handleExplosion(S27PacketExplosion packetIn)
    {
        Explosion explosion = new Explosion(this.gameController.theWorld, (Entity)null, packetIn.func_149148_f(), packetIn.func_149143_g(), packetIn.func_149145_h(), packetIn.func_149146_i());
        explosion.affectedBlockPositions = packetIn.func_149150_j();
        explosion.doExplosionB(true);
        this.gameController.thePlayer.motionX += (double)packetIn.func_149149_c();
        this.gameController.thePlayer.motionY += (double)packetIn.func_149144_d();
        this.gameController.thePlayer.motionZ += (double)packetIn.func_149147_e();
    }

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    public void handleOpenWindow(S2DPacketOpenWindow packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;

        switch (packetIn.func_148899_d())
        {
            case 0:
                entityclientplayermp.displayGUIChest(new InventoryBasic(packetIn.func_148902_e(), packetIn.func_148900_g(), packetIn.func_148898_f()));
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 1:
                entityclientplayermp.displayGUIWorkbench(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 2:
                TileEntityFurnace tileentityfurnace = new TileEntityFurnace();

                if (packetIn.func_148900_g())
                {
                    tileentityfurnace.setCustomInventoryName(packetIn.func_148902_e());
                }

                entityclientplayermp.func_146101_a(tileentityfurnace);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 3:
                TileEntityDispenser tileentitydispenser = new TileEntityDispenser();

                if (packetIn.func_148900_g())
                {
                    tileentitydispenser.func_146018_a(packetIn.func_148902_e());
                }

                entityclientplayermp.func_146102_a(tileentitydispenser);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 4:
                entityclientplayermp.displayGUIEnchantment(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), packetIn.func_148900_g() ? packetIn.func_148902_e() : null);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 5:
                TileEntityBrewingStand tileentitybrewingstand = new TileEntityBrewingStand();

                if (packetIn.func_148900_g())
                {
                    tileentitybrewingstand.func_145937_a(packetIn.func_148902_e());
                }

                entityclientplayermp.func_146098_a(tileentitybrewingstand);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 6:
                entityclientplayermp.displayGUIMerchant(new NpcMerchant(entityclientplayermp), packetIn.func_148900_g() ? packetIn.func_148902_e() : null);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 7:
                TileEntityBeacon tileentitybeacon = new TileEntityBeacon();
                entityclientplayermp.func_146104_a(tileentitybeacon);

                if (packetIn.func_148900_g())
                {
                    tileentitybeacon.func_145999_a(packetIn.func_148902_e());
                }

                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 8:
                entityclientplayermp.displayGUIAnvil(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 9:
                TileEntityHopper tileentityhopper = new TileEntityHopper();

                if (packetIn.func_148900_g())
                {
                    tileentityhopper.func_145886_a(packetIn.func_148902_e());
                }

                entityclientplayermp.func_146093_a(tileentityhopper);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 10:
                TileEntityDropper tileentitydropper = new TileEntityDropper();

                if (packetIn.func_148900_g())
                {
                    tileentitydropper.func_146018_a(packetIn.func_148902_e());
                }

                entityclientplayermp.func_146102_a(tileentitydropper);
                entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                break;
            case 11:
                Entity entity = this.clientWorldController.getEntityByID(packetIn.func_148897_h());

                if (entity != null && entity instanceof EntityHorse)
                {
                    entityclientplayermp.displayGUIHorse((EntityHorse)entity, new AnimalChest(packetIn.func_148902_e(), packetIn.func_148900_g(), packetIn.func_148898_f()));
                    entityclientplayermp.openContainer.windowId = packetIn.func_148901_c();
                }
        }
    }

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    public void handleSetSlot(S2FPacketSetSlot packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;

        if (packetIn.func_149175_c() == -1)
        {
            entityclientplayermp.inventory.setItemStack(packetIn.func_149174_e());
        }
        else
        {
            boolean flag = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative)this.gameController.currentScreen;
                flag = guicontainercreative.func_147056_g() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (packetIn.func_149175_c() == 0 && packetIn.func_149173_d() >= 36 && packetIn.func_149173_d() < 45)
            {
                ItemStack itemstack = entityclientplayermp.inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();

                if (packetIn.func_149174_e() != null && (itemstack == null || itemstack.stackSize < packetIn.func_149174_e().stackSize))
                {
                    packetIn.func_149174_e().animationsToGo = 5;
                }

                entityclientplayermp.inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
            else if (packetIn.func_149175_c() == entityclientplayermp.openContainer.windowId && (packetIn.func_149175_c() != 0 || !flag))
            {
                entityclientplayermp.openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
        }
    }

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn)
    {
        Container container = null;
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;

        if (packetIn.func_148889_c() == 0)
        {
            container = entityclientplayermp.inventoryContainer;
        }
        else if (packetIn.func_148889_c() == entityclientplayermp.openContainer.windowId)
        {
            container = entityclientplayermp.openContainer;
        }

        if (container != null && !packetIn.func_148888_e())
        {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.func_148889_c(), packetIn.func_148890_d(), true));
        }
    }

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    public void handleWindowItems(S30PacketWindowItems packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;

        if (packetIn.func_148911_c() == 0)
        {
            entityclientplayermp.inventoryContainer.putStacksInSlots(packetIn.func_148910_d());
        }
        else if (packetIn.func_148911_c() == entityclientplayermp.openContainer.windowId)
        {
            entityclientplayermp.openContainer.putStacksInSlots(packetIn.func_148910_d());
        }
    }

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn)
    {
        Object object = this.clientWorldController.getTileEntity(packetIn.func_149129_c(), packetIn.func_149128_d(), packetIn.func_149127_e());

        if (object == null)
        {
            object = new TileEntitySign();
            ((TileEntity)object).setWorldObj(this.clientWorldController);
            ((TileEntity)object).xCoord = packetIn.func_149129_c();
            ((TileEntity)object).yCoord = packetIn.func_149128_d();
            ((TileEntity)object).zCoord = packetIn.func_149127_e();
        }

        this.gameController.thePlayer.displayGUIEditSign((TileEntity)object);
    }

    /**
     * Updates a specified sign with the specified text lines
     */
    public void handleUpdateSign(S33PacketUpdateSign packetIn)
    {
        boolean flag = false;

        if (this.gameController.theWorld.blockExists(packetIn.func_149346_c(), packetIn.func_149345_d(), packetIn.func_149344_e()))
        {
            TileEntity tileentity = this.gameController.theWorld.getTileEntity(packetIn.func_149346_c(), packetIn.func_149345_d(), packetIn.func_149344_e());

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (tileentitysign.getIsEditable())
                {
                    for (int i = 0; i < 4; ++i)
                    {
                        tileentitysign.signText[i] = packetIn.func_149347_f()[i];
                    }

                    tileentitysign.markDirty();
                }

                flag = true;
            }
        }

        if (!flag && this.gameController.thePlayer != null)
        {
            this.gameController.thePlayer.addChatMessage(new ChatComponentText("Unable to locate sign at " + packetIn.func_149346_c() + ", " + packetIn.func_149345_d() + ", " + packetIn.func_149344_e()));
        }
    }

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn)
    {
        if (this.gameController.theWorld.blockExists(packetIn.getX(), packetIn.getY(), packetIn.getZ()))
        {
            TileEntity tileentity = this.gameController.theWorld.getTileEntity(packetIn.getX(), packetIn.getY(), packetIn.getZ());

            if (tileentity != null)
            {
                if (packetIn.getTileEntityType() == 1 && tileentity instanceof TileEntityMobSpawner)
                {
                    tileentity.readFromNBT(packetIn.getNbtCompound());
                }
                else if (packetIn.getTileEntityType() == 2 && tileentity instanceof TileEntityCommandBlock)
                {
                    tileentity.readFromNBT(packetIn.getNbtCompound());
                }
                else if (packetIn.getTileEntityType() == 3 && tileentity instanceof TileEntityBeacon)
                {
                    tileentity.readFromNBT(packetIn.getNbtCompound());
                }
                else if (packetIn.getTileEntityType() == 4 && tileentity instanceof TileEntitySkull)
                {
                    tileentity.readFromNBT(packetIn.getNbtCompound());
                }
                else if (packetIn.getTileEntityType() == 5 && tileentity instanceof TileEntityFlowerPot)
                {
                    tileentity.readFromNBT(packetIn.getNbtCompound());
                }
                else
                {
                    tileentity.onDataPacket(netManager, packetIn);
                }
            }
        }
    }

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    public void handleWindowProperty(S31PacketWindowProperty packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;

        if (entityclientplayermp.openContainer != null && entityclientplayermp.openContainer.windowId == packetIn.func_149182_c())
        {
            entityclientplayermp.openContainer.updateProgressBar(packetIn.func_149181_d(), packetIn.func_149180_e());
        }
    }

    public void handleEntityEquipment(S04PacketEntityEquipment packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149389_d());

        if (entity != null)
        {
            entity.setCurrentItemOrArmor(packetIn.func_149388_e(), packetIn.func_149390_c());
        }
    }

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    public void handleCloseWindow(S2EPacketCloseWindow packetIn)
    {
        this.gameController.thePlayer.closeScreenNoPacket();
    }

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    public void handleBlockAction(S24PacketBlockAction packetIn)
    {
        this.gameController.theWorld.addBlockEvent(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn)
    {
        this.gameController.theWorld.destroyBlockInWorldPartially(packetIn.func_148845_c(), packetIn.func_148844_d(), packetIn.func_148843_e(), packetIn.func_148842_f(), packetIn.func_148846_g());
    }

    public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn)
    {
        for (int i = 0; i < packetIn.func_149254_d(); ++i)
        {
            int j = packetIn.func_149255_a(i);
            int k = packetIn.func_149253_b(i);
            this.clientWorldController.doPreChunk(j, k, true);
            this.clientWorldController.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
            Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(j, k);
            chunk.fillChunk(packetIn.func_149256_c(i), packetIn.func_149252_e()[i], packetIn.func_149257_f()[i], true);
            this.clientWorldController.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);

            if (!(this.clientWorldController.provider instanceof WorldProviderSurface))
            {
                chunk.resetRelightChecks();
            }
        }
    }

    public void handleChangeGameState(S2BPacketChangeGameState packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;
        int i = packetIn.func_149138_c();
        float f = packetIn.func_149137_d();
        int j = MathHelper.floor_float(f + 0.5F);

        if (i >= 0 && i < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[i] != null)
        {
            entityclientplayermp.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[i], new Object[0]));
        }

        if (i == 1)
        {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        }
        else if (i == 2)
        {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        }
        else if (i == 3)
        {
            this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(j));
        }
        else if (i == 4)
        {
            this.gameController.displayGuiScreen(new GuiWinGame());
        }
        else if (i == 5)
        {
            GameSettings gamesettings = this.gameController.gameSettings;

            if (f == 0.0F)
            {
                this.gameController.displayGuiScreen(new GuiScreenDemo());
            }
            else if (f == 101.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.movement", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindForward.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindLeft.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindBack.getKeyCode()), GameSettings.getKeyDisplayString(gamesettings.keyBindRight.getKeyCode())}));
            }
            else if (f == 102.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindJump.getKeyCode())}));
            }
            else if (f == 103.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(gamesettings.keyBindInventory.getKeyCode())}));
            }
        }
        else if (i == 6)
        {
            this.clientWorldController.playSound(entityclientplayermp.posX, entityclientplayermp.posY + (double)entityclientplayermp.getEyeHeight(), entityclientplayermp.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
        else if (i == 7)
        {
            this.clientWorldController.setRainStrength(f);
        }
        else if (i == 8)
        {
            this.clientWorldController.setThunderStrength(f);
        }
    }

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    public void handleMaps(S34PacketMaps packetIn)
    {
        MapData mapdata = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
        mapdata.updateMPMapData(packetIn.getData());
        this.gameController.entityRenderer.getMapItemRenderer().func_148246_a(mapdata);
    }

    public void handleEffect(S28PacketEffect packetIn)
    {
        if (packetIn.isSoundServerwide())
        {
            this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getPosX(), packetIn.getPosY(), packetIn.getPosZ(), packetIn.getSoundData());
        }
        else
        {
            this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getPosX(), packetIn.getPosY(), packetIn.getPosZ(), packetIn.getSoundData());
        }
    }

    /**
     * Updates the players statistics or achievements
     */
    public void handleStatistics(S37PacketStatistics packetIn)
    {
        boolean flag = false;
        StatBase statbase;
        int i;

        for (Iterator iterator = packetIn.func_148974_c().entrySet().iterator(); iterator.hasNext(); this.gameController.thePlayer.getStatFileWriter().func_150873_a(this.gameController.thePlayer, statbase, i))
        {
            Entry entry = (Entry)iterator.next();
            statbase = (StatBase)entry.getKey();
            i = ((Integer)entry.getValue()).intValue();

            if (statbase.isAchievement() && i > 0)
            {
                if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().writeStat(statbase) == 0)
                {
                    Achievement achievement = (Achievement)statbase;
                    this.gameController.guiAchievement.displayAchievement(achievement);
                    this.gameController.getTwitchStream().func_152911_a(new MetadataAchievement(achievement), 0L);

                    if (statbase == AchievementList.openInventory)
                    {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                flag = true;
            }
        }

        if (!this.field_147308_k && !flag && this.gameController.gameSettings.showInventoryAchievementHint)
        {
            this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
        }

        this.field_147308_k = true;

        if (this.gameController.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter)this.gameController.currentScreen).doneLoading();
        }
    }

    public void handleEntityEffect(S1DPacketEntityEffect packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149426_d());

        if (entity instanceof EntityLivingBase)
        {
            PotionEffect potioneffect = new PotionEffect(packetIn.func_149427_e(), packetIn.func_149425_g(), packetIn.func_149428_f());
            potioneffect.setPotionDurationMax(packetIn.func_149429_c());
            ((EntityLivingBase)entity).addPotionEffect(potioneffect);
        }
    }

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149076_c());

        if (entity instanceof EntityLivingBase)
        {
            ((EntityLivingBase)entity).removePotionEffectClient(packetIn.func_149075_d());
        }
    }

    public void handlePlayerListItem(S38PacketPlayerListItem packetIn)
    {
        GuiPlayerInfo guiplayerinfo = (GuiPlayerInfo)this.playerInfoMap.get(packetIn.func_149122_c());

        if (guiplayerinfo == null && packetIn.func_149121_d())
        {
            guiplayerinfo = new GuiPlayerInfo(packetIn.func_149122_c());
            this.playerInfoMap.put(packetIn.func_149122_c(), guiplayerinfo);
            this.playerInfoList.add(guiplayerinfo);
        }

        if (guiplayerinfo != null && !packetIn.func_149121_d())
        {
            this.playerInfoMap.remove(packetIn.func_149122_c());
            this.playerInfoList.remove(guiplayerinfo);
        }

        if (guiplayerinfo != null && packetIn.func_149121_d())
        {
            guiplayerinfo.responseTime = packetIn.func_149120_e();
        }
    }

    public void handleKeepAlive(S00PacketKeepAlive packetIn)
    {
        this.addToSendQueue(new C00PacketKeepAlive(packetIn.func_149134_c()));
    }

    /**
     * Allows validation of the connection state transition. Parameters: from, to (connection state). Typically throws
     * IllegalStateException or UnsupportedOperationException if validation fails
     */
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState)
    {
        throw new IllegalStateException("Unexpected protocol change!");
    }

    public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn)
    {
        EntityClientPlayerMP entityclientplayermp = this.gameController.thePlayer;
        entityclientplayermp.capabilities.isFlying = packetIn.isFlying();
        entityclientplayermp.capabilities.isCreativeMode = packetIn.isCreativeMode();
        entityclientplayermp.capabilities.disableDamage = packetIn.isInvulnerable();
        entityclientplayermp.capabilities.allowFlying = packetIn.isAllowFlying();
        entityclientplayermp.capabilities.setFlySpeed(packetIn.getFlySpeed());
        entityclientplayermp.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
    }

    /**
     * Displays the available command-completion options the server knows of
     */
    public void handleTabComplete(S3APacketTabComplete packetIn)
    {
        String[] astring = packetIn.func_149630_c();

        if (this.gameController.currentScreen instanceof GuiChat)
        {
            GuiChat guichat = (GuiChat)this.gameController.currentScreen;
            guichat.onAutocompleteResponse(astring);
        }
    }

    public void handleSoundEffect(S29PacketSoundEffect packetIn)
    {
        this.gameController.theWorld.playSound(packetIn.func_149207_d(), packetIn.func_149211_e(), packetIn.func_149210_f(), packetIn.func_149212_c(), packetIn.func_149208_g(), packetIn.func_149209_h(), false);
    }

    /**
     * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    public void handleCustomPayload(S3FPacketCustomPayload packetIn)
    {
        if ("MC|TrList".equals(packetIn.func_149169_c()))
        {
            ByteBuf bytebuf = Unpooled.wrappedBuffer(packetIn.func_149168_d());

            try
            {
                int i = bytebuf.readInt();
                GuiScreen guiscreen = this.gameController.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant && i == this.gameController.thePlayer.openContainer.windowId)
                {
                    IMerchant imerchant = ((GuiMerchant)guiscreen).getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.func_151390_b(new PacketBuffer(bytebuf));
                    imerchant.setRecipes(merchantrecipelist);
                }
            }
            catch (IOException ioexception)
            {
                logger.error("Couldn\'t load trade info", ioexception);
            }
            finally
            {
                bytebuf.release();
            }
        }
        else if ("MC|Brand".equals(packetIn.func_149169_c()))
        {
            this.gameController.thePlayer.setClientBrand(new String(packetIn.func_149168_d(), Charsets.UTF_8));
        }
        else if ("MC|RPack".equals(packetIn.func_149169_c()))
        {
            final String s = new String(packetIn.func_149168_d(), Charsets.UTF_8);

            if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED)
            {
                this.gameController.getResourcePackRepository().obtainResourcePack(s);
            }
            else if (this.gameController.getCurrentServerData() == null || this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.PROMPT)
            {
                this.gameController.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                {
                    private static final String __OBFID = "CL_00000879";
                    public void confirmClicked(boolean result, int id)
                    {
                        NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();

                        if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null)
                        {
                            NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
                            ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                        }

                        if (result)
                        {
                            NetHandlerPlayClient.this.gameController.getResourcePackRepository().obtainResourcePack(s);
                        }

                        NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                    }
                }, I18n.format("multiplayer.texturePrompt.line1", new Object[0]), I18n.format("multiplayer.texturePrompt.line2", new Object[0]), 0));
            }
        }
    }

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective scoreobjective;

        if (packetIn.func_149338_e() == 0)
        {
            scoreobjective = scoreboard.addScoreObjective(packetIn.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
            scoreobjective.setDisplayName(packetIn.func_149337_d());
        }
        else
        {
            scoreobjective = scoreboard.getObjective(packetIn.func_149339_c());

            if (packetIn.func_149338_e() == 1)
            {
                scoreboard.func_96519_k(scoreobjective);
            }
            else if (packetIn.func_149338_e() == 2)
            {
                scoreobjective.setDisplayName(packetIn.func_149337_d());
            }
        }
    }

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    public void handleUpdateScore(S3CPacketUpdateScore packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.func_149321_d());

        if (packetIn.func_149322_f() == 0)
        {
            Score score = scoreboard.getValueFromObjective(packetIn.func_149324_c(), scoreobjective);
            score.setScorePoints(packetIn.func_149323_e());
        }
        else if (packetIn.func_149322_f() == 1)
        {
            scoreboard.func_96515_c(packetIn.func_149324_c());
        }
    }

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packetIn.func_149370_d().length() == 0)
        {
            scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), (ScoreObjective)null);
        }
        else
        {
            ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.func_149370_d());
            scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), scoreobjective);
        }
    }

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    public void handleTeams(S3EPacketTeams packetIn)
    {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (packetIn.func_149307_h() == 0)
        {
            scoreplayerteam = scoreboard.createTeam(packetIn.func_149312_c());
        }
        else
        {
            scoreplayerteam = scoreboard.getTeam(packetIn.func_149312_c());
        }

        if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 2)
        {
            scoreplayerteam.setTeamName(packetIn.func_149306_d());
            scoreplayerteam.setNamePrefix(packetIn.func_149311_e());
            scoreplayerteam.setNameSuffix(packetIn.func_149309_f());
            scoreplayerteam.func_98298_a(packetIn.func_149308_i());
        }

        Iterator iterator;
        String s;

        if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 3)
        {
            iterator = packetIn.func_149310_g().iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.func_151392_a(s, packetIn.func_149312_c());
            }
        }

        if (packetIn.func_149307_h() == 4)
        {
            iterator = packetIn.func_149310_g().iterator();

            while (iterator.hasNext())
            {
                s = (String)iterator.next();
                scoreboard.removePlayerFromTeam(s, scoreplayerteam);
            }
        }

        if (packetIn.func_149307_h() == 1)
        {
            scoreboard.removeTeam(scoreplayerteam);
        }
    }

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    public void handleParticles(S2APacketParticles packetIn)
    {
        if (packetIn.func_149222_k() == 0)
        {
            double d0 = (double)(packetIn.func_149227_j() * packetIn.func_149221_g());
            double d2 = (double)(packetIn.func_149227_j() * packetIn.func_149224_h());
            double d4 = (double)(packetIn.func_149227_j() * packetIn.func_149223_i());
            this.clientWorldController.spawnParticle(packetIn.func_149228_c(), packetIn.func_149220_d(), packetIn.func_149226_e(), packetIn.func_149225_f(), d0, d2, d4);
        }
        else
        {
            for (int i = 0; i < packetIn.func_149222_k(); ++i)
            {
                double d1 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149221_g();
                double d3 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149224_h();
                double d5 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149223_i();
                double d6 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149227_j();
                double d7 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149227_j();
                double d8 = this.avRandomizer.nextGaussian() * (double)packetIn.func_149227_j();
                this.clientWorldController.spawnParticle(packetIn.func_149228_c(), packetIn.func_149220_d() + d1, packetIn.func_149226_e() + d3, packetIn.func_149225_f() + d5, d6, d7, d8);
            }
        }
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    public void handleEntityProperties(S20PacketEntityProperties packetIn)
    {
        Entity entity = this.clientWorldController.getEntityByID(packetIn.func_149442_c());

        if (entity != null)
        {
            if (!(entity instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            }
            else
            {
                BaseAttributeMap baseattributemap = ((EntityLivingBase)entity).getAttributeMap();
                Iterator iterator = packetIn.func_149441_d().iterator();

                while (iterator.hasNext())
                {
                    S20PacketEntityProperties.Snapshot snapshot = (S20PacketEntityProperties.Snapshot)iterator.next();
                    IAttributeInstance iattributeinstance = baseattributemap.getAttributeInstanceByName(snapshot.func_151409_a());

                    if (iattributeinstance == null)
                    {
                        iattributeinstance = baseattributemap.registerAttribute(new RangedAttribute(snapshot.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    iattributeinstance.setBaseValue(snapshot.func_151410_b());
                    iattributeinstance.removeAllModifiers();
                    Iterator iterator1 = snapshot.func_151408_c().iterator();

                    while (iterator1.hasNext())
                    {
                        AttributeModifier attributemodifier = (AttributeModifier)iterator1.next();
                        iattributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    /**
     * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
     */
    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }
}