package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager
{
    public static final File FILE_PLAYERBANS = new File("banned-players.json");
    public static final File FILE_IPBANS = new File("banned-ips.json");
    public static final File FILE_OPS = new File("ops.json");
    public static final File FILE_WHITELIST = new File("whitelist.json");
    private static final Logger logger = LogManager.getLogger();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;
    /** A list of player entities that exist on this server. */
    public final List playerEntityList = new ArrayList();
    private final UserListBans bannedPlayers;
    private final BanList bannedIPs;
    /** A set containing the OPs. */
    private final UserListOps ops;
    /** The Set of all whitelisted players. */
    private final UserListWhitelist whiteListedPlayers;
    private final Map playerStatFiles;
    /** Reference to the PlayerNBTManager object. */
    private IPlayerFileData playerNBTManagerObj;
    /** Server setting to only allow OPs and whitelisted players to join the server. */
    private boolean whiteListEnforced;
    /** The maximum number of players that can be connected at a time. */
    protected int maxPlayers;
    private int viewDistance;
    private WorldSettings.GameType gameType;
    /** True if all players are allowed to use commands (cheats). */
    private boolean commandsAllowedForAll;
    /** index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players */
    private int playerPingIndex;
    private static final String __OBFID = "CL_00001423";

    public ServerConfigurationManager(MinecraftServer server)
    {
        this.bannedPlayers = new UserListBans(FILE_PLAYERBANS);
        this.bannedIPs = new BanList(FILE_IPBANS);
        this.ops = new UserListOps(FILE_OPS);
        this.whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
        this.playerStatFiles = Maps.newHashMap();
        this.mcServer = server;
        this.bannedPlayers.setLanServer(false);
        this.bannedIPs.setLanServer(false);
        this.maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP player, NetHandlerPlayServer nethandlerplayserver)
    {
        GameProfile gameprofile = player.getGameProfile();
        PlayerProfileCache playerprofilecache = this.mcServer.getPlayerProfileCache();
        GameProfile gameprofile1 = playerprofilecache.func_152652_a(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
        playerprofilecache.func_152649_a(gameprofile);
        NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(player);

        World playerWorld = this.mcServer.worldServerForDimension(player.dimension);
        if (playerWorld==null)
        {
            player.dimension=0;
            playerWorld=this.mcServer.worldServerForDimension(0);
            ChunkCoordinates spawnPoint = playerWorld.provider.getRandomizedSpawnPoint();
            player.setPosition(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ);
        }

        player.setWorld(playerWorld);
        player.theItemInWorldManager.setWorld((WorldServer)player.worldObj);
        String s1 = "local";

        if (netManager.getRemoteAddress() != null)
        {
            s1 = netManager.getRemoteAddress().toString();
        }

        logger.info(player.getCommandSenderName() + "[" + s1 + "] logged in with entity id " + player.getEntityId() + " at (" + player.posX + ", " + player.posY + ", " + player.posZ + ")");
        WorldServer worldserver = this.mcServer.worldServerForDimension(player.dimension);
        ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
        this.func_72381_a(player, (EntityPlayerMP)null, worldserver);
        player.playerNetServerHandler = nethandlerplayserver;
        nethandlerplayserver.sendPacket(new S01PacketJoinGame(player.getEntityId(), player.theItemInWorldManager.getGameType(), worldserver.getWorldInfo().isHardcoreModeEnabled(), worldserver.provider.dimensionId, worldserver.difficultySetting, this.getMaxPlayers(), worldserver.getWorldInfo().getTerrainType()));
        nethandlerplayserver.sendPacket(new S3FPacketCustomPayload("MC|Brand", this.getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
        nethandlerplayserver.sendPacket(new S05PacketSpawnPosition(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ));
        nethandlerplayserver.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
        nethandlerplayserver.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));
        player.getStatFile().func_150877_d();
        player.getStatFile().func_150884_b(player);
        this.func_96456_a((ServerScoreboard)worldserver.getScoreboard(), player);
        this.mcServer.refreshStatusNextTick();
        ChatComponentTranslation chatcomponenttranslation;

        if (!player.getCommandSenderName().equalsIgnoreCase(s))
        {
            chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", new Object[] {player.getFormattedCommandSenderName(), s});
        }
        else
        {
            chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined", new Object[] {player.getFormattedCommandSenderName()});
        }

        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.sendChatMsg(chatcomponenttranslation);
        this.playerLoggedIn(player);
        nethandlerplayserver.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        this.updateTimeAndWeatherForPlayer(player, worldserver);

        if (this.mcServer.getTexturePack().length() > 0)
        {
            player.requestTexturePackLoad(this.mcServer.getTexturePack());
        }

        Iterator iterator = player.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            nethandlerplayserver.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }

        player.addSelfToInternalCraftingInventory();

        FMLCommonHandler.instance().firePlayerLoggedIn(player);
        if (nbttagcompound != null && nbttagcompound.hasKey("Riding", 10))
        {
            Entity entity = EntityList.createEntityFromNBT(nbttagcompound.getCompoundTag("Riding"), worldserver);

            if (entity != null)
            {
                entity.forceSpawn = true;
                worldserver.spawnEntityInWorld(entity);
                player.mountEntity(entity);
                entity.forceSpawn = false;
            }
        }
    }

    protected void func_96456_a(ServerScoreboard scoreboardIn, EntityPlayerMP player)
    {
        HashSet hashset = new HashSet();
        Iterator iterator = scoreboardIn.getTeams().iterator();

        while (iterator.hasNext())
        {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)iterator.next();
            player.playerNetServerHandler.sendPacket(new S3EPacketTeams(scoreplayerteam, 0));
        }

        for (int i = 0; i < 3; ++i)
        {
            ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);

            if (scoreobjective != null && !hashset.contains(scoreobjective))
            {
                List list = scoreboardIn.func_96550_d(scoreobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext())
                {
                    Packet packet = (Packet)iterator1.next();
                    player.playerNetServerHandler.sendPacket(packet);
                }

                hashset.add(scoreobjective);
            }
        }
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerManager(WorldServer[] p_72364_1_)
    {
        this.playerNBTManagerObj = p_72364_1_[0].getSaveHandler().getPlayerNBTManager();
    }

    public void func_72375_a(EntityPlayerMP player, WorldServer worldIn)
    {
        WorldServer worldserver1 = player.getServerForPlayer();

        if (worldIn != null)
        {
            worldIn.getPlayerManager().removePlayer(player);
        }

        worldserver1.getPlayerManager().addPlayer(player);
        worldserver1.theChunkProviderServer.loadChunk((int)player.posX >> 4, (int)player.posZ >> 4);
    }

    public int getEntityViewDistance()
    {
        return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP player)
    {
        NBTTagCompound nbttagcompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        NBTTagCompound nbttagcompound1;

        if (player.getCommandSenderName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null)
        {
            player.readFromNBT(nbttagcompound);
            nbttagcompound1 = nbttagcompound;
            logger.debug("loading single player");
            net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(player, this.playerNBTManagerObj, player.getUniqueID().toString());
        }
        else
        {
            nbttagcompound1 = this.playerNBTManagerObj.readPlayerData(player);
        }

        return nbttagcompound1;
    }

    public NBTTagCompound getPlayerNBT(EntityPlayerMP player)
    {
        // Hacky method to allow loading the NBT for a player prior to login
        NBTTagCompound nbttagcompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        if (player.getCommandSenderName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null)
        {
            return nbttagcompound;
        }
        else
        {
            return ((SaveHandler)this.playerNBTManagerObj).getPlayerNBT(player);
        }
    }
    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP player)
    {
        if (player.playerNetServerHandler == null) return;

        this.playerNBTManagerObj.writePlayerData(player);
        StatisticsFile statisticsfile = (StatisticsFile)this.playerStatFiles.get(player.getUniqueID());

        if (statisticsfile != null)
        {
            statisticsfile.func_150883_b();
        }
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP player)
    {
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(player.getCommandSenderName(), true, 1000));
        this.playerEntityList.add(player);
        WorldServer worldserver = this.mcServer.worldServerForDimension(player.dimension);
        ChunkIOExecutor.adjustPoolSize(this.getCurrentPlayerCount());
        worldserver.spawnEntityInWorld(player);
        this.func_72375_a(player, (WorldServer)null);

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP)this.playerEntityList.get(i);
            player.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(entityplayermp1.getCommandSenderName(), true, entityplayermp1.ping));
        }
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
     */
    public void serverUpdateMountedMovingPlayer(EntityPlayerMP player)
    {
        player.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(player);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void playerLoggedOut(EntityPlayerMP player)
    {
        FMLCommonHandler.instance().firePlayerLoggedOut(player);
        player.triggerAchievement(StatList.leaveGameStat);
        this.writePlayerData(player);
        WorldServer worldserver = player.getServerForPlayer();

        if (player.ridingEntity != null)
        {
            worldserver.removePlayerEntityDangerously(player.ridingEntity);
            logger.debug("removing player mount");
        }

        worldserver.removeEntity(player);
        worldserver.getPlayerManager().removePlayer(player);
        this.playerEntityList.remove(player);
        this.playerStatFiles.remove(player.getUniqueID());
        net.minecraftforge.common.chunkio.ChunkIOExecutor.adjustPoolSize(this.getCurrentPlayerCount());
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(player.getCommandSenderName(), false, 9999));
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress address, GameProfile profile)
    {
        String s;

        if (this.bannedPlayers.isBanned(profile))
        {
            UserListBansEntry userlistbansentry = (UserListBansEntry)this.bannedPlayers.getEntry(profile);
            s = "You are banned from this server!\nReason: " + userlistbansentry.getBanReason();

            if (userlistbansentry.getBanEndDate() != null)
            {
                s = s + "\nYour ban will be removed on " + dateFormat.format(userlistbansentry.getBanEndDate());
            }

            return s;
        }
        else if (!this.canJoin(profile))
        {
            return "You are not white-listed on this server!";
        }
        else if (this.bannedIPs.isBanned(address))
        {
            IPBanEntry ipbanentry = this.bannedIPs.getBanEntry(address);
            s = "Your IP address is banned from this server!\nReason: " + ipbanentry.getBanReason();

            if (ipbanentry.getBanEndDate() != null)
            {
                s = s + "\nYour ban will be removed on " + dateFormat.format(ipbanentry.getBanEndDate());
            }

            return s;
        }
        else
        {
            return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
        }
    }

    /**
     * also checks for multiple logins across servers
     */
    public EntityPlayerMP createPlayerForUser(GameProfile profile)
    {
        UUID uuid = EntityPlayer.getUUID(profile);
        ArrayList arraylist = Lists.newArrayList();
        EntityPlayerMP entityplayermp;

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            entityplayermp = (EntityPlayerMP)this.playerEntityList.get(i);

            if (entityplayermp.getUniqueID().equals(uuid))
            {
                arraylist.add(entityplayermp);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext())
        {
            entityplayermp = (EntityPlayerMP)iterator.next();
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        Object object;

        if (this.mcServer.isDemo())
        {
            object = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
        }
        else
        {
            object = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, (ItemInWorldManager)object);
    }

    /**
     * Called on respawn
     */
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP player, int dimension, boolean conqueredEnd)
    {
        World world = mcServer.worldServerForDimension(dimension);
        if (world == null)
        {
            dimension = 0;
        }
        else if (!world.provider.canRespawnHere())
        {
            dimension = world.provider.getRespawnDimension(player);
        }

        player.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(player);
        player.getServerForPlayer().getEntityTracker().untrackEntity(player);
        player.getServerForPlayer().getPlayerManager().removePlayer(player);
        this.playerEntityList.remove(player);
        this.mcServer.worldServerForDimension(player.dimension).removePlayerEntityDangerously(player);
        ChunkCoordinates chunkcoordinates = player.getBedLocation(dimension);
        boolean flag1 = player.isSpawnForced(dimension);
        player.dimension = dimension;
        Object object;

        if (this.mcServer.isDemo())
        {
            object = new DemoWorldManager(this.mcServer.worldServerForDimension(player.dimension));
        }
        else
        {
            object = new ItemInWorldManager(this.mcServer.worldServerForDimension(player.dimension));
        }

        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(player.dimension), player.getGameProfile(), (ItemInWorldManager)object);
        entityplayermp1.playerNetServerHandler = player.playerNetServerHandler;
        entityplayermp1.clonePlayer(player, conqueredEnd);
        entityplayermp1.dimension = dimension;
        entityplayermp1.setEntityId(player.getEntityId());
        WorldServer worldserver = this.mcServer.worldServerForDimension(player.dimension);
        this.func_72381_a(entityplayermp1, player, worldserver);
        ChunkCoordinates chunkcoordinates1;

        if (chunkcoordinates != null)
        {
            chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(player.dimension), chunkcoordinates, flag1);

            if (chunkcoordinates1 != null)
            {
                entityplayermp1.setLocationAndAngles((double)((float)chunkcoordinates1.posX + 0.5F), (double)((float)chunkcoordinates1.posY + 0.1F), (double)((float)chunkcoordinates1.posZ + 0.5F), 0.0F, 0.0F);
                entityplayermp1.setSpawnChunk(chunkcoordinates, flag1);
            }
            else
            {
                entityplayermp1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);

        while (!worldserver.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).isEmpty())
        {
            entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
        }

        entityplayermp1.playerNetServerHandler.sendPacket(new S07PacketRespawn(entityplayermp1.dimension, entityplayermp1.worldObj.difficultySetting, entityplayermp1.worldObj.getWorldInfo().getTerrainType(), entityplayermp1.theItemInWorldManager.getGameType()));
        chunkcoordinates1 = worldserver.getSpawnPoint();
        entityplayermp1.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
        entityplayermp1.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(chunkcoordinates1.posX, chunkcoordinates1.posY, chunkcoordinates1.posZ));
        entityplayermp1.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityplayermp1.experience, entityplayermp1.experienceTotal, entityplayermp1.experienceLevel));
        this.updateTimeAndWeatherForPlayer(entityplayermp1, worldserver);
        worldserver.getPlayerManager().addPlayer(entityplayermp1);
        worldserver.spawnEntityInWorld(entityplayermp1);
        this.playerEntityList.add(entityplayermp1);
        entityplayermp1.addSelfToInternalCraftingInventory();
        entityplayermp1.setHealth(entityplayermp1.getHealth());
        FMLCommonHandler.instance().firePlayerRespawnEvent(entityplayermp1);
        return entityplayermp1;
    }

    /**
     * moves provided player from overworld to nether or vice versa
     */
    public void transferPlayerToDimension(EntityPlayerMP player, int dimension)
    {
        transferPlayerToDimension(player, dimension, mcServer.worldServerForDimension(dimension).getDefaultTeleporter());
    }

    public void transferPlayerToDimension(EntityPlayerMP player, int dimension, Teleporter teleporter)
    {
        int j = player.dimension;
        WorldServer worldserver = this.mcServer.worldServerForDimension(player.dimension);
        player.dimension = dimension;
        WorldServer worldserver1 = this.mcServer.worldServerForDimension(player.dimension);
        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, worldserver1.difficultySetting, worldserver1.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType())); // Forge: Use new dimensions information
        worldserver.removePlayerEntityDangerously(player);
        player.isDead = false;
        this.transferEntityToWorld(player, j, worldserver, worldserver1, teleporter);
        this.func_72375_a(player, worldserver);
        player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        player.theItemInWorldManager.setWorld(worldserver1);
        this.updateTimeAndWeatherForPlayer(player, worldserver1);
        this.syncPlayerInventory(player);
        Iterator iterator = player.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, j, dimension);
    }

    /**
     * Transfers an entity from a world to another world.
     */
    public void transferEntityToWorld(Entity entityIn, int p_82448_2_, WorldServer p_82448_3_, WorldServer p_82448_4_)
    {
        transferEntityToWorld(entityIn, p_82448_2_, p_82448_3_, p_82448_4_, p_82448_4_.getDefaultTeleporter());
    }

    public void transferEntityToWorld(Entity entityIn, int p_82448_2_, WorldServer p_82448_3_, WorldServer p_82448_4_, Teleporter teleporter)
    {
        WorldProvider pOld = p_82448_3_.provider;
        WorldProvider pNew = p_82448_4_.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = entityIn.posX * moveFactor;
        double d1 = entityIn.posZ * moveFactor;
        double d3 = entityIn.posX;
        double d4 = entityIn.posY;
        double d5 = entityIn.posZ;
        float f = entityIn.rotationYaw;
        p_82448_3_.theProfiler.startSection("moving");

        /*
        if (par1Entity.dimension == -1)
        {
            d0 /= d2;
            d1 /= d2;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        else if (par1Entity.dimension == 0)
        {
            d0 *= d2;
            d1 *= d2;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        */
        if (entityIn.dimension == 1)
        {
            ChunkCoordinates chunkcoordinates;

            if (p_82448_2_ == 1)
            {
                chunkcoordinates = p_82448_4_.getSpawnPoint();
            }
            else
            {
                chunkcoordinates = p_82448_4_.getEntrancePortalLocation();
            }

            d0 = (double)chunkcoordinates.posX;
            entityIn.posY = (double)chunkcoordinates.posY;
            d1 = (double)chunkcoordinates.posZ;
            entityIn.setLocationAndAngles(d0, entityIn.posY, d1, 90.0F, 0.0F);

            if (entityIn.isEntityAlive())
            {
                p_82448_3_.updateEntityWithOptionalForce(entityIn, false);
            }
        }

        p_82448_3_.theProfiler.endSection();

        if (p_82448_2_ != 1)
        {
            p_82448_3_.theProfiler.startSection("placing");
            d0 = (double)MathHelper.clamp_int((int)d0, -29999872, 29999872);
            d1 = (double)MathHelper.clamp_int((int)d1, -29999872, 29999872);

            if (entityIn.isEntityAlive())
            {
                entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
                teleporter.placeInPortal(entityIn, d3, d4, d5, f);
                p_82448_4_.spawnEntityInWorld(entityIn);
                p_82448_4_.updateEntityWithOptionalForce(entityIn, false);
            }

            p_82448_3_.theProfiler.endSection();
        }

        entityIn.setWorld(p_82448_4_);
    }

    /**
     * self explanitory
     */
    public void onTick()
    {
        if (++this.playerPingIndex > 600)
        {
            this.playerPingIndex = 0;
        }

        if (this.playerPingIndex < this.playerEntityList.size())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(this.playerPingIndex);
            this.sendPacketToAllPlayers(new S38PacketPlayerListItem(entityplayermp.getCommandSenderName(), true, entityplayermp.ping));
        }
    }

    public void sendPacketToAllPlayers(Packet packetIn)
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.sendPacket(packetIn);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension)
    {
        for (int j = 0; j < this.playerEntityList.size(); ++j)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(j);

            if (entityplayermp.dimension == dimension)
            {
                entityplayermp.playerNetServerHandler.sendPacket(packetIn);
            }
        }
    }

    public String getPlayerNamesString(boolean includeUuid)
    {
        String s = "";
        ArrayList arraylist = Lists.newArrayList(this.playerEntityList);

        for (int i = 0; i < arraylist.size(); ++i)
        {
            if (i > 0)
            {
                s = s + ", ";
            }

            s = s + ((EntityPlayerMP)arraylist.get(i)).getCommandSenderName();

            if (includeUuid)
            {
                s = s + " (" + ((EntityPlayerMP)arraylist.get(i)).getUniqueID().toString() + ")";
            }
        }

        return s;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        String[] astring = new String[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            astring[i] = ((EntityPlayerMP)this.playerEntityList.get(i)).getCommandSenderName();
        }

        return astring;
    }

    public GameProfile[] getAllProfiles()
    {
        GameProfile[] agameprofile = new GameProfile[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            agameprofile[i] = ((EntityPlayerMP)this.playerEntityList.get(i)).getGameProfile();
        }

        return agameprofile;
    }

    public UserListBans getBannedPlayers()
    {
        return this.bannedPlayers;
    }

    public BanList getBannedIPs()
    {
        return this.bannedIPs;
    }

    public void addOp(GameProfile profile)
    {
        this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel()));
    }

    public void removeOp(GameProfile profile)
    {
        this.ops.removeEntry(profile);
    }

    public boolean canJoin(GameProfile profile)
    {
        return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
    }

    public boolean canSendCommands(GameProfile profile)
    {
        return this.ops.hasEntry(profile) || this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName()) || this.commandsAllowedForAll;
    }

    public EntityPlayerMP getPlayerByUsername(String username)
    {
        Iterator iterator = this.playerEntityList.iterator();
        EntityPlayerMP entityplayermp;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entityplayermp = (EntityPlayerMP)iterator.next();
        }
        while (!entityplayermp.getCommandSenderName().equalsIgnoreCase(username));

        return entityplayermp;
    }

    /**
     * Find all players in a specified range and narrowing down by other parameters
     */
    public List findPlayers(ChunkCoordinates coordinates, int minRadius, int maxRadius, int maxAmount, int gameMode, int minXp, int maxXp, Map scoreboardData, String username, String teamName, World worldIn)
    {
        if (this.playerEntityList.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            Object object = new ArrayList();
            boolean flag = maxAmount < 0;
            boolean flag1 = username != null && username.startsWith("!");
            boolean flag2 = teamName != null && teamName.startsWith("!");
            int k1 = minRadius * minRadius;
            int l1 = maxRadius * maxRadius;
            maxAmount = MathHelper.abs_int(maxAmount);

            if (flag1)
            {
                username = username.substring(1);
            }

            if (flag2)
            {
                teamName = teamName.substring(1);
            }

            for (int i2 = 0; i2 < this.playerEntityList.size(); ++i2)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(i2);

                if ((worldIn == null || entityplayermp.worldObj == worldIn) && (username == null || flag1 != username.equalsIgnoreCase(entityplayermp.getCommandSenderName())))
                {
                    if (teamName != null)
                    {
                        Team team = entityplayermp.getTeam();
                        String s2 = team == null ? "" : team.getRegisteredName();

                        if (flag2 == teamName.equalsIgnoreCase(s2))
                        {
                            continue;
                        }
                    }

                    if (coordinates != null && (minRadius > 0 || maxRadius > 0))
                    {
                        float f = coordinates.getDistanceSquaredToChunkCoordinates(entityplayermp.getCommandSenderPosition());

                        if (minRadius > 0 && f < (float)k1 || maxRadius > 0 && f > (float)l1)
                        {
                            continue;
                        }
                    }

                    if (this.matchesScoreboardCriteria(entityplayermp, scoreboardData) && (gameMode == WorldSettings.GameType.NOT_SET.getID() || gameMode == entityplayermp.theItemInWorldManager.getGameType().getID()) && (minXp <= 0 || entityplayermp.experienceLevel >= minXp) && entityplayermp.experienceLevel <= maxXp)
                    {
                        ((List)object).add(entityplayermp);
                    }
                }
            }

            if (coordinates != null)
            {
                Collections.sort((List)object, new PlayerPositionComparator(coordinates));
            }

            if (flag)
            {
                Collections.reverse((List)object);
            }

            if (maxAmount > 0)
            {
                object = ((List)object).subList(0, Math.min(maxAmount, ((List)object).size()));
            }

            return (List)object;
        }
    }

    private boolean matchesScoreboardCriteria(EntityPlayer player, Map scoreboardCriteria)
    {
        if (scoreboardCriteria != null && scoreboardCriteria.size() != 0)
        {
            Iterator iterator = scoreboardCriteria.entrySet().iterator();
            Entry entry;
            boolean flag;
            int i;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                entry = (Entry)iterator.next();
                String s = (String)entry.getKey();
                flag = false;

                if (s.endsWith("_min") && s.length() > 4)
                {
                    flag = true;
                    s = s.substring(0, s.length() - 4);
                }

                Scoreboard scoreboard = player.getWorldScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjective(s);

                if (scoreobjective == null)
                {
                    return false;
                }

                Score score = player.getWorldScoreboard().getValueFromObjective(player.getCommandSenderName(), scoreobjective);
                i = score.getScorePoints();

                if (i < ((Integer)entry.getValue()).intValue() && flag)
                {
                    return false;
                }
            }
            while (i <= ((Integer)entry.getValue()).intValue() || flag);

            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * params: x,y,z,r,dimension. The packet is sent to all players within r radius of x,y,z (r^2>x^2+y^2+z^2)
     */
    public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn)
    {
        this.sendToAllNearExcept((EntityPlayer)null, x, y, z, radius, dimension, packetIn);
    }

    /**
     * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer, but all other players within the
     * search radius
     */
    public void sendToAllNearExcept(EntityPlayer p_148543_1_, double x, double y, double z, double radius, int dimension, Packet p_148543_11_)
    {
        for (int j = 0; j < this.playerEntityList.size(); ++j)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(j);

            if (entityplayermp != p_148543_1_ && entityplayermp.dimension == dimension)
            {
                double d4 = x - entityplayermp.posX;
                double d5 = y - entityplayermp.posY;
                double d6 = z - entityplayermp.posZ;

                if (d4 * d4 + d5 * d5 + d6 * d6 < radius * radius)
                {
                    entityplayermp.playerNetServerHandler.sendPacket(p_148543_11_);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData()
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(i));
        }
    }

    public void addWhitelistedPlayer(GameProfile profile)
    {
        this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
    }

    public void removePlayerFromWhitelist(GameProfile profile)
    {
        this.whiteListedPlayers.removeEntry(profile);
    }

    public UserListWhitelist getWhitelistedPlayers()
    {
        return this.whiteListedPlayers;
    }

    public String[] getWhitelistedPlayerNames()
    {
        return this.whiteListedPlayers.getKeys();
    }

    public UserListOps getOppedPlayers()
    {
        return this.ops;
    }

    public String[] getOppedPlayerNames()
    {
        return this.ops.getKeys();
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList() {}

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void updateTimeAndWeatherForPlayer(EntityPlayerMP player, WorldServer worldIn)
    {
        player.playerNetServerHandler.sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getGameRuleBooleanValue("doDaylightCycle")));

        if (worldIn.isRaining())
        {
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getWeightedThunderStrength(1.0F)));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void syncPlayerInventory(EntityPlayerMP player)
    {
        player.sendContainerToPlayer(player.inventoryContainer);
        player.setPlayerHealthUpdated();
        player.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.playerEntityList.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
    }

    public void setWhiteListEnabled(boolean whitelistEnabled)
    {
        this.whiteListEnforced = whitelistEnabled;
    }

    public List getPlayersMatchingAddress(String address)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.playerEntityList.iterator();

        while (iterator.hasNext())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();

            if (entityplayermp.getPlayerIP().equals(address))
            {
                arraylist.add(entityplayermp);
            }
        }

        return arraylist;
    }

    /**
     * Gets the View Distance.
     */
    public int getViewDistance()
    {
        return this.viewDistance;
    }

    public MinecraftServer getServerInstance()
    {
        return this.mcServer;
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void func_152604_a(WorldSettings.GameType p_152604_1_)
    {
        this.gameType = p_152604_1_;
    }

    private void func_72381_a(EntityPlayerMP p_72381_1_, EntityPlayerMP p_72381_2_, World p_72381_3_)
    {
        if (p_72381_2_ != null)
        {
            p_72381_1_.theItemInWorldManager.setGameType(p_72381_2_.theItemInWorldManager.getGameType());
        }
        else if (this.gameType != null)
        {
            p_72381_1_.theItemInWorldManager.setGameType(this.gameType);
        }

        p_72381_1_.theItemInWorldManager.initializeGameType(p_72381_3_.getWorldInfo().getGameType());
    }

    /**
     * Sets whether all players are allowed to use commands (cheats) on the server.
     */
    @SideOnly(Side.CLIENT)
    public void setCommandsAllowedForAll(boolean p_72387_1_)
    {
        this.commandsAllowedForAll = p_72387_1_;
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers()
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    public void sendChatMsgImpl(IChatComponent component, boolean isChat)
    {
        this.mcServer.addChatMessage(component);
        this.sendPacketToAllPlayers(new S02PacketChat(component, isChat));
    }

    /**
     * Sends the given string to every player as chat message.
     */
    public void sendChatMsg(IChatComponent component)
    {
        this.sendChatMsgImpl(component, true);
    }

    public StatisticsFile getPlayerStatsFile(EntityPlayer player)
    {
        UUID uuid = player.getUniqueID();
        StatisticsFile statisticsfile = uuid == null ? null : (StatisticsFile)this.playerStatFiles.get(uuid);

        if (statisticsfile == null)
        {
            File file1 = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
            File file2 = new File(file1, uuid.toString() + ".json");

            if (!file2.exists())
            {
                File file3 = new File(file1, player.getCommandSenderName() + ".json");

                if (file3.exists() && file3.isFile())
                {
                    file3.renameTo(file2);
                }
            }

            statisticsfile = new StatisticsFile(this.mcServer, file2);
            statisticsfile.func_150882_a();
            this.playerStatFiles.put(uuid, statisticsfile);
        }

        return statisticsfile;
    }

    public void setViewDistance(int distance)
    {
        this.viewDistance = distance;

        if (this.mcServer.worldServers != null)
        {
            WorldServer[] aworldserver = this.mcServer.worldServers;
            int j = aworldserver.length;

            for (int k = 0; k < j; ++k)
            {
                WorldServer worldserver = aworldserver[k];

                if (worldserver != null)
                {
                    worldserver.getPlayerManager().func_152622_a(distance);
                }
            }
        }
    }

    @SideOnly(Side.SERVER)
    public boolean isWhiteListEnabled()
    {
        return this.whiteListEnforced;
    }
}