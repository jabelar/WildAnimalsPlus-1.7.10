package com.blogspot.jabelarminecraft.wildanimals.proxy;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.blogspot.jabelarminecraft.wildanimals.WildAnimals;
import com.blogspot.jabelarminecraft.wildanimals.WildAnimalsEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.WildAnimalsFMLEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.WildAnimalsOreGenEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.WildAnimalsTerrainGenEventHandler;
import com.blogspot.jabelarminecraft.wildanimals.commands.CommandConjure;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingJaguar;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingLion;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingLynx;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityManEatingTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.bigcats.EntityTiger;
import com.blogspot.jabelarminecraft.wildanimals.entities.birdsofprey.EntityBirdOfPrey;
import com.blogspot.jabelarminecraft.wildanimals.entities.eggs.EntityItemWildAnimalsEgg;
import com.blogspot.jabelarminecraft.wildanimals.entities.eggs.EntityWildAnimalsEgg;
import com.blogspot.jabelarminecraft.wildanimals.entities.herdanimals.EntityElephant;
import com.blogspot.jabelarminecraft.wildanimals.entities.serpents.EntitySerpent;
import com.blogspot.jabelarminecraft.wildanimals.items.ItemWildAnimalSpawnEggThrowable;
import com.blogspot.jabelarminecraft.wildanimals.networking.ServerPacketHandler;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy 
{
    
    protected int modEntityID = 0;
    protected Configuration config;
    
    // fluids
    public Fluid testFluid;
     
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
    { 
        // load configuration before doing anything else
        // got config tutorial from http://www.minecraftforge.net/wiki/How_to_make_an_advanced_configuration_file
        initConfig(event);

        // register stuff
        registerBlocks();
        registerItems();
        registerTileEntities();
        registerModEntities();
        registerEntitySpawns();
        registerFuelHandlers();
    }
    
    public void fmlLifeCycleEvent(FMLInitializationEvent event)
    {
        // register custom event listeners
        registerEventListeners();
 
        // register networking channel 
        registerNetworkingChannel();
        
        // register server packet handler
        registerServerPacketHandler();
        
        // register recipes here to allow use of items from other mods
        registerRecipes();
    }
    
    public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
    {
        // can do some inter-mod stuff here
    }
    
    protected void initConfig(FMLPreInitializationEvent event)
    {
        // might need to use suggestedConfigFile (event.getSuggestedConfigFile) location to publish
        WildAnimals.configFile = event.getSuggestedConfigurationFile();
        // DEBUG
        System.out.println(WildAnimals.MODNAME+" config path = "+WildAnimals.configFile.getAbsolutePath());
        System.out.println("Config file exists = "+WildAnimals.configFile.canRead());
  
        WildAnimals.config = new Configuration(WildAnimals.configFile);
      	syncConfig();
    }
    
    public static void syncConfig() 
    {
    	WildAnimals.configBigCatsAreManEaters = WildAnimals.config.getBoolean("BigCatsAreManEaters", Configuration.CATEGORY_GENERAL, WildAnimals.configBigCatsAreManEaters, "An Boolean!");
    	WildAnimals.configIncludeSnakes = WildAnimals.config.getBoolean("IncludeSnakes", Configuration.CATEGORY_GENERAL, WildAnimals.configIncludeSnakes, "An Boolean!");
    	WildAnimals.configIncludeBigCats = WildAnimals.config.getBoolean("IncludeBigCats", Configuration.CATEGORY_GENERAL, WildAnimals.configIncludeBigCats, "An Boolean!");
    	WildAnimals.configIncludeHerdAnimals = WildAnimals.config.getBoolean("IncludeHerdAnimals", Configuration.CATEGORY_GENERAL, WildAnimals.configIncludeHerdAnimals, "An Boolean!");
 
        if(WildAnimals.config.hasChanged())
        {
        	WildAnimals.config.save();
        }
    }

    // register blocks
    public void registerBlocks()
    {
        //example: GameRegistry.registerBlock(blockTomato, "tomatoes");
     }

    // register fluids
    public void registerFluids()
    {
        // see tutorial at http://www.minecraftforge.net/wiki/Create_a_Fluid
        Fluid testFluid = new Fluid("testfluid");
        FluidRegistry.registerFluid(testFluid);
        testFluid.setLuminosity(0).setDensity(1000).setViscosity(1000).setGaseous(false) ;
     }
    
    // register items
    private void registerItems()
    {
        // DEBUG
        System.out.println("Registering items");

        // spawn eggs are registered during entity registration
        
        // example: GameRegistry.registerCustomItemStack(name, itemStack);
    }
    
    // register tileentities
    public void registerTileEntities()
    {
        // DEBUG
        System.out.println("Registering tile entities");
               
        // example: GameRegistry.registerTileEntity(TileEntityStove.class, "stove_tile_entity");
    }

    // register recipes
    public void registerRecipes()
    {
        // DEBUG
        System.out.println("Registering recipes");
               
        // examples:
        //        GameRegistry.addRecipe(recipe);
        //        GameRegistry.addShapedRecipe(output, params);
        //        GameRegistry.addShapelessRecipe(output, params);
        //        GameRegistry.addSmelting(input, output, xp);
    }

    // register entities
    // lots of conflicting tutorials on this, currently following: nly register mod id http://www.minecraftforum.net/topic/1417041-mod-entity-problem/page__st__140#entry18822284
    // another tut says to only register global id like http://www.minecraftforge.net/wiki/How_to_register_a_mob_entity#Registering_an_Entity
    // another tut says to use both: http://www.minecraftforum.net/topic/2389683-172-forge-add-new-block-item-entity-ai-creative-tab-language-localization-block-textures-side-textures/
     public void registerModEntities()
    {    
         // DEBUG
        System.out.println("Registering entities");

        // register generic spawn egg entity.  Make sure that the track changes flag is set true
        registerModEntityFastTracking(EntityWildAnimalsEgg.class, "Spawn Egg");
        registerModEntityFastTracking(EntityItemWildAnimalsEgg.class, "Spawn Egg Entity");
        
        // uses configuration file to control whether each entity type is registered, to allow user customization
        
        // Big cats
        if (WildAnimals.configIncludeBigCats)
        {
            if (WildAnimals.configBigCatsAreManEaters)
            {
                registerModEntityWithEgg(EntityManEatingTiger.class, "Tiger", 0xE18519, 0x000000);
                registerModEntityWithEgg(EntityManEatingLion.class, "Lion", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityManEatingLynx.class, "Lynx", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityManEatingJaguar.class, "Jaguar", 0xF4E003, 0x000000);
            }
            else
            {
                registerModEntityWithEgg(EntityTiger.class, "Tiger", 0xE18519, 0x000000);
                registerModEntityWithEgg(EntityLion.class, "Lion", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityLynx.class, "Lynx", 0xD9C292, 0xFFFFFF);
                registerModEntityWithEgg(EntityJaguar.class, "Jaguar", 0xF4E003, 0x000000);
            }
        }

        // Herd animals
        if (WildAnimals.configIncludeHerdAnimals)
        {
            registerModEntityWithEgg(EntityElephant.class, "Elephant", 0x888888, 0xAAAAAA);
        }
        
        // Serpents
        if (WildAnimals.configIncludeSnakes)
        {
            registerModEntityWithEgg(EntitySerpent.class, "Python", 0x3F5505, 0x4E6414);
        }
        
        // Birds of Prey
         registerModEntityWithEgg(EntityBirdOfPrey.class, "Eagle", 0xFFF2E3, 0x7D6C57);

    }
     
     public void registerModEntity(Class parEntityClass, String parEntityName)
     {
            EntityRegistry.registerModEntity(parEntityClass, parEntityName, ++modEntityID, WildAnimals.instance, 80, 3, false);
     }

     public void registerModEntityWithEgg(Class parEntityClass, String parEntityName, int parEggColor, int parEggSpotsColor)
     {
            EntityRegistry.registerModEntity(parEntityClass, parEntityName, ++modEntityID, WildAnimals.instance, 80, 3, false);
            registerSpawnEgg(parEntityName, parEggColor, parEggSpotsColor);
     }

     // can't use vanilla spawn eggs with entities registered with modEntityID, so use custom eggs.
     // name passed must match entity name string
     public void registerSpawnEgg(String parSpawnName, int parEggColor, int parEggSpotsColor)
     {
         Item itemSpawnEgg = new ItemWildAnimalSpawnEggThrowable(parSpawnName, parEggColor, parEggSpotsColor).setUnlocalizedName("spawn_egg_"+parSpawnName.toLowerCase()).setTextureName("wildanimals:spawn_egg");
         GameRegistry.registerItem(itemSpawnEgg, "spawnEgg"+parSpawnName);
     }
     
     // for fast moving entities and projectiles need registration with tracking flag set true
     public void registerModEntityFastTracking(Class parEntityClass, String parEntityName)
     {
            EntityRegistry.registerModEntity(parEntityClass, parEntityName, ++modEntityID, WildAnimals.instance, 80, 10, true);
     }
          
    public void registerEntitySpawns()
    {
        // register natural spawns for entities
        // EntityRegistry.addSpawn(MyEntity.class, spawnProbability, minSpawn, maxSpawn, enumCreatureType, [spawnBiome]);
        // See the constructor in BiomeGenBase.java to see the rarity of vanilla mobs; Sheep are probability 10 while Endermen are probability 1
        // minSpawn and maxSpawn are about how groups of the entity spawn
        // enumCreatureType represents the "rules" Minecraft uses to determine spawning, based on creature type. By default, you have three choices:
        //    EnumCreatureType.creature uses rules for animals: spawn everywhere it is light out.
        //    EnumCreatureType.monster uses rules for monsters: spawn everywhere it is dark out.
        //    EnumCreatureType.waterCreature uses rules for water creatures: spawn only in water.
        // [spawnBiome] is an optional parameter of type BiomeGenBase that limits the creature spawn to a single biome type. Without this parameter, it will spawn everywhere. 

         // DEBUG
        System.out.println("Registering natural spawns");
        
        // For the biome type you can use an list, but unfortunately the built-in biomeList contains
        // null entries and will crash, so you need to clean up that list.
        // Diesieben07 suggested the following code to remove the nulls and create list of all biomes
        BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()), Predicates.notNull()), BiomeGenBase.class);

        // savanna
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.creature, BiomeGenBase.savanna); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.savanna); //change the values to vary the spawn rarity, biome, etc.              

        // savannPlateau
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.creature, BiomeGenBase.savannaPlateau); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.savannaPlateau); //change the values to vary the spawn rarity, biome, etc.              

        // birch forest
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForest); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForest); //change the values to vary the spawn rarity, biome, etc.              

        // birch forest hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForestHills); //change the values to vary the spawn rarity, biome, etc.              

        // cold taiga
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaigaHills); //change the values to vary the spawn rarity, biome, etc.              
        
        // cold taiga hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaigaHills); //change the values to vary the spawn rarity, biome, etc.              

        // desert
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.creature, BiomeGenBase.desert); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.desert); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.desert); //change the values to vary the spawn rarity, biome, etc.              

        // desert hills
        EntityRegistry.addSpawn(EntityLion.class, 6, 1, 5, EnumCreatureType.creature, BiomeGenBase.desertHills); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.desertHills); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.desertHills); //change the values to vary the spawn rarity, biome, etc.              

        // extreme hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHills); //change the values to vary the spawn rarity, biome, etc.              

        // extreme hills edge
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsEdge); //change the values to vary the spawn rarity, biome, etc.              
        
        // extreme hills plus
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsPlus); //change the values to vary the spawn rarity, biome, etc.              

        // forest
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.forest); //change the values to vary the spawn rarity, biome, etc.              
           EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.forest); //change the values to vary the spawn rarity, biome, etc.              
        
        // forest hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.forestHills); //change the values to vary the spawn rarity, biome, etc.              
        
        // ice mountains
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.iceMountains); //change the values to vary the spawn rarity, biome, etc.              

        // mega taiga
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaiga); //change the values to vary the spawn rarity, biome, etc.              

        // mega taiga hills
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaigaHills); //change the values to vary the spawn rarity, biome, etc.              
        
        // mesa plateau
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.mesaPlateau); //change the values to vary the spawn rarity, biome, etc.              
        
        // mesa plateau F
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.mesaPlateau_F); //change the values to vary the spawn rarity, biome, etc.              
        
        // swamp
        EntityRegistry.addSpawn(EntityJaguar.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.swampland); //change the values to vary the spawn rarity, biome, etc.                  
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 4, EnumCreatureType.creature, BiomeGenBase.swampland); //change the values to vary the spawn rarity, biome, etc.              
        
        // jungle
        EntityRegistry.addSpawn(EntityJaguar.class, 6, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle); //change the values to vary the spawn rarity, biome, etc.              
           if (WildAnimals.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle); //change the values to vary the spawn rarity, biome, etc.              
        
        // jungle hills
        EntityRegistry.addSpawn(EntityJaguar.class, 3, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills); //change the values to vary the spawn rarity, biome, etc.              
           if (WildAnimals.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills); //change the values to vary the spawn rarity, biome, etc.              
        
        // jungle edge
        EntityRegistry.addSpawn(EntityJaguar.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityTiger.class, 1, 0, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge); //change the values to vary the spawn rarity, biome, etc.              
           if (WildAnimals.configBigCatsAreManEaters)
        {
            EntityRegistry.addSpawn(EntityManEatingTiger.class, 1, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge); //change the values to vary the spawn rarity, biome, etc.            
        }
        EntityRegistry.addSpawn(EntitySerpent.class, 10, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge); //change the values to vary the spawn rarity, biome, etc.              

        // plains
        EntityRegistry.addSpawn(EntityLion.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.plains); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntityElephant.class, 10, 1, 5, EnumCreatureType.creature, BiomeGenBase.plains); //change the values to vary the spawn rarity, biome, etc.              

        // beach
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.beach); //change the values to vary the spawn rarity, biome, etc.              

        // cold beach
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldBeach); //change the values to vary the spawn rarity, biome, etc.              
        
        // ice plains
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.icePlains); //change the values to vary the spawn rarity, biome, etc.              

        // roofed forest
        EntityRegistry.addSpawn(EntityLynx.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.roofedForest); //change the values to vary the spawn rarity, biome, etc.              
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.roofedForest); //change the values to vary the spawn rarity, biome, etc.              

        // stone beach
        EntityRegistry.addSpawn(EntitySerpent.class, 5, 1, 1, EnumCreatureType.creature, BiomeGenBase.stoneBeach); //change the values to vary the spawn rarity, biome, etc.              
    }
    
    public void registerFuelHandlers()
    {
         // DEBUG
        System.out.println("Registering fuel handlers");
        
        // example: GameRegistry.registerFuelHandler(handler);
    }
    
    public void registerEventListeners() 
    {
         // DEBUG
        System.out.println("Registering event listeners");

        MinecraftForge.EVENT_BUS.register(new WildAnimalsEventHandler());
        MinecraftForge.TERRAIN_GEN_BUS.register(new WildAnimalsTerrainGenEventHandler());
        MinecraftForge.ORE_GEN_BUS.register(new WildAnimalsOreGenEventHandler());        

        // some events, especially tick, is handled on FML bus
        FMLCommonHandler.instance().bus().register(new WildAnimalsFMLEventHandler());
    }

    public void sendMessageToPlayer(ChatComponentText msg) { }

    // Got this idea to use IGuiHandler interface from message from Noppes at http://www.minecraftforge.net/forum/index.php/topic,15403.0.html
    // With code example at https://dl.dropboxusercontent.com/u/3096920/NetworkExampleMod.zip

    public void registerNetworkingChannel()
    {
        WildAnimals.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(WildAnimals.NETWORK_CHANNEL_NAME);
        // when you want to send a packet elsewhere, use one of these methods (server or client):
        //     WildAnimals.channel.sendToServer(FMLProxyPacket);
        //     WildAnimals.channel.sendTo(FMLProxyPacket, EntityPlayerMP); for player-specific GUI interaction
        //     WildAnimals.channel.sendToAll(FMLProxyPacket); for all player sync like entities
        // and there are other sendToxxx methods to check out.
    }
    
    public void registerServerPacketHandler()
    {
        WildAnimals.channel.register(new ServerPacketHandler());
    }

	public void fmlLifeCycleEvent(FMLServerStartingEvent event) 
	{
        event.registerServerCommand(new CommandConjure());
	}

	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		// TODO Auto-generated method stub
		
	}
}