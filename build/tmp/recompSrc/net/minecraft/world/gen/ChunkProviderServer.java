package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.GameRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();
    private Set droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap());
    /** a dummy chunk, returned in place of an actual chunk. */
    private Chunk dummyChunk;
    /** chunk generator object. Calls to load nonexistent chunks are forwarded to this object. */
    public IChunkProvider serverChunkGenerator;
    public IChunkLoader chunkLoader;
    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the dummy if
     * possible
     */
    public boolean chunkLoadOverride = true;
    /** map of chunk Id's to Chunk instances */
    public LongHashMap id2ChunkMap = new LongHashMap();
    public List loadedChunks = new ArrayList();
    public WorldServer worldObj;
    private Set<Long> loadingChunks = com.google.common.collect.Sets.newHashSet();
    private static final String __OBFID = "CL_00001436";

    public ChunkProviderServer(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_)
    {
        this.dummyChunk = new EmptyChunk(p_i1520_1_, 0, 0);
        this.worldObj = p_i1520_1_;
        this.chunkLoader = p_i1520_2_;
        this.serverChunkGenerator = p_i1520_3_;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int p_73149_1_, int p_73149_2_)
    {
        return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(p_73149_1_, p_73149_2_));
    }

    public List func_152380_a()
    {
        return this.loadedChunks;
    }

    public void dropChunk(int p_73241_1_, int p_73241_2_)
    {
        if (this.worldObj.provider.canRespawnHere() && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId))
        {
            ChunkCoordinates chunkcoordinates = this.worldObj.getSpawnPoint();
            int k = p_73241_1_ * 16 + 8 - chunkcoordinates.posX;
            int l = p_73241_2_ * 16 + 8 - chunkcoordinates.posZ;
            short short1 = 128;

            if (k < -short1 || k > short1 || l < -short1 || l > short1)
            {
                this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_)));
            }
        }
        else
        {
            this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_73241_1_, p_73241_2_)));
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    public void unloadAllChunks()
    {
        Iterator iterator = this.loadedChunks.iterator();

        while (iterator.hasNext())
        {
            Chunk chunk = (Chunk)iterator.next();
            this.dropChunk(chunk.xPosition, chunk.zPosition);
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_)
    {
        return loadChunk(p_73158_1_, p_73158_2_, null);
    }

    public Chunk loadChunk(int par1, int par2, Runnable runnable)
    {
        long k = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
        this.droppedChunksSet.remove(Long.valueOf(k));
        Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(k);
        AnvilChunkLoader loader = null;

        if (this.chunkLoader instanceof AnvilChunkLoader)
        {
            loader = (AnvilChunkLoader) this.chunkLoader;
        }

        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(this.worldObj, par1, par2))
        {
            if (runnable != null)
            {
                ChunkIOExecutor.queueChunkLoad(this.worldObj, loader, this, par1, par2, runnable);
                return null;
            }
            else
            {
                chunk = ChunkIOExecutor.syncChunkLoad(this.worldObj, loader, this, par1, par2);
            }
        }
        else if (chunk == null)
        {
            chunk = this.originalLoadChunk(par1, par2);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null)
        {
            runnable.run();
        }

        return chunk;
    }

    public Chunk originalLoadChunk(int p_73158_1_, int p_73158_2_)
    {
        long k = ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_);
        this.droppedChunksSet.remove(Long.valueOf(k));
        Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(k);

        if (chunk == null)
        {
            boolean added = loadingChunks.add(k);
            if (!added)
            {
                cpw.mods.fml.common.FMLLog.bigWarning("There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.", p_73158_1_, p_73158_2_, worldObj.provider.dimensionId);
            }
            chunk = ForgeChunkManager.fetchDormantChunk(k, this.worldObj);
            if (chunk == null)
            {
                chunk = this.loadChunkFromFile(p_73158_1_, p_73158_2_);
            }

            if (chunk == null)
            {
                if (this.serverChunkGenerator == null)
                {
                    chunk = this.dummyChunk;
                }
                else
                {
                    try
                    {
                        chunk = this.serverChunkGenerator.provideChunk(p_73158_1_, p_73158_2_);
                    }
                    catch (Throwable throwable)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[] {Integer.valueOf(p_73158_1_), Integer.valueOf(p_73158_2_)}));
                        crashreportcategory.addCrashSection("Position hash", Long.valueOf(k));
                        crashreportcategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.id2ChunkMap.add(k, chunk);
            this.loadedChunks.add(chunk);
            loadingChunks.remove(k);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this, p_73158_1_, p_73158_2_);
        }

        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int p_73154_1_, int p_73154_2_)
    {
        Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(p_73154_1_, p_73154_2_));
        return chunk == null ? (!this.worldObj.findingSpawnPoint && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(p_73154_1_, p_73154_2_)) : chunk;
    }

    private Chunk loadChunkFromFile(int p_73239_1_, int p_73239_2_)
    {
        if (this.chunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, p_73239_1_, p_73239_2_);

                if (chunk != null)
                {
                    chunk.lastSaveTime = this.worldObj.getTotalWorldTime();

                    if (this.serverChunkGenerator != null)
                    {
                        this.serverChunkGenerator.recreateStructures(p_73239_1_, p_73239_2_);
                    }
                }

                return chunk;
            }
            catch (Exception exception)
            {
                logger.error("Couldn\'t load chunk", exception);
                return null;
            }
        }
    }

    private void saveChunkExtraData(Chunk p_73243_1_)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                this.chunkLoader.saveExtraChunkData(this.worldObj, p_73243_1_);
            }
            catch (Exception exception)
            {
                logger.error("Couldn\'t save entities", exception);
            }
        }
    }

    private void saveChunkData(Chunk p_73242_1_)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                p_73242_1_.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.chunkLoader.saveChunk(this.worldObj, p_73242_1_);
            }
            catch (IOException ioexception)
            {
                logger.error("Couldn\'t save chunk", ioexception);
            }
            catch (MinecraftException minecraftexception)
            {
                logger.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", minecraftexception);
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
    {
        Chunk chunk = this.provideChunk(p_73153_2_, p_73153_3_);

        if (!chunk.isTerrainPopulated)
        {
            chunk.func_150809_p();

            if (this.serverChunkGenerator != null)
            {
                this.serverChunkGenerator.populate(p_73153_1_, p_73153_2_, p_73153_3_);
                GameRegistry.generateWorld(p_73153_2_, p_73153_3_, worldObj, serverChunkGenerator, p_73153_1_);
                chunk.setChunkModified();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
    {
        int i = 0;
        ArrayList arraylist = Lists.newArrayList(this.loadedChunks);

        for (int j = 0; j < arraylist.size(); ++j)
        {
            Chunk chunk = (Chunk)arraylist.get(j);

            if (p_73151_1_)
            {
                this.saveChunkExtraData(chunk);
            }

            if (chunk.needsSaving(p_73151_1_))
            {
                this.saveChunkData(chunk);
                chunk.isModified = false;
                ++i;

                if (i == 24 && !p_73151_1_)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
        if (this.chunkLoader != null)
        {
            this.chunkLoader.saveExtraData();
        }
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        if (!this.worldObj.disableLevelSaving)
        {
            for (ChunkCoordIntPair forced : this.worldObj.getPersistentChunks().keySet())
            {
                this.droppedChunksSet.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
            }

            for (int i = 0; i < 100; ++i)
            {
                if (!this.droppedChunksSet.isEmpty())
                {
                    Long olong = (Long)this.droppedChunksSet.iterator().next();
                    Chunk chunk = (Chunk)this.id2ChunkMap.getValueByKey(olong.longValue());

                    if (chunk != null)
                    {
                        chunk.onChunkUnload();
                        this.saveChunkData(chunk);
                        this.saveChunkExtraData(chunk);
                        this.loadedChunks.remove(chunk);
                        ForgeChunkManager.putDormantChunk(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk);
                        if(loadedChunks.size() == 0 && ForgeChunkManager.getPersistentChunksFor(this.worldObj).size() == 0 && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)){
                            DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
                            return serverChunkGenerator.unloadQueuedChunks();
                        }
                    }

                    this.droppedChunksSet.remove(olong);
                    this.id2ChunkMap.remove(olong.longValue());
                }
            }

            if (this.chunkLoader != null)
            {
                this.chunkLoader.chunkTick();
            }
        }

        return this.serverChunkGenerator.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return !this.worldObj.disableLevelSaving;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
    {
        return this.serverChunkGenerator.getPossibleCreatures(p_73155_1_, p_73155_2_, p_73155_3_, p_73155_4_);
    }

    public ChunkPosition findClosestStructure(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return this.serverChunkGenerator.findClosestStructure(p_147416_1_, p_147416_2_, p_147416_3_, p_147416_4_, p_147416_5_);
    }

    public int getLoadedChunkCount()
    {
        return this.id2ChunkMap.getNumHashElements();
    }

    public void recreateStructures(int p_82695_1_, int p_82695_2_) {}
}