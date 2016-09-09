package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandGameMode extends CommandBase
{
    private static final String __OBFID = "CL_00000448";

    public String getCommandName()
    {
        return "gamemode";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.gamemode.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 0)
        {
            WorldSettings.GameType gametype = this.getGameModeFromCommand(sender, args[0]);
            EntityPlayerMP entityplayermp = args.length >= 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
            entityplayermp.setGameType(gametype);
            entityplayermp.fallDistance = 0.0F;
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("gameMode." + gametype.getName(), new Object[0]);

            if (entityplayermp != sender)
            {
                notifyOperators(sender, this, 1, "commands.gamemode.success.other", new Object[] {entityplayermp.getCommandSenderName(), chatcomponenttranslation});
            }
            else
            {
                notifyOperators(sender, this, 1, "commands.gamemode.success.self", new Object[] {chatcomponenttranslation});
            }
        }
        else
        {
            throw new WrongUsageException("commands.gamemode.usage", new Object[0]);
        }
    }

    /**
     * Gets the Game Mode specified in the command.
     */
    protected WorldSettings.GameType getGameModeFromCommand(ICommandSender p_71539_1_, String p_71539_2_)
    {
        return !p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.SURVIVAL.getName()) && !p_71539_2_.equalsIgnoreCase("s") ? (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.CREATIVE.getName()) && !p_71539_2_.equalsIgnoreCase("c") ? (!p_71539_2_.equalsIgnoreCase(WorldSettings.GameType.ADVENTURE.getName()) && !p_71539_2_.equalsIgnoreCase("a") ? WorldSettings.getGameTypeById(parseIntBounded(p_71539_1_, p_71539_2_, 0, WorldSettings.GameType.values().length - 2)) : WorldSettings.GameType.ADVENTURE) : WorldSettings.GameType.CREATIVE) : WorldSettings.GameType.SURVIVAL;
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"survival", "creative", "adventure"}): (args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayerUsernames()) : null);
    }

    /**
     * Returns String array containing all player usernames in the server.
     */
    protected String[] getListOfPlayerUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }
}