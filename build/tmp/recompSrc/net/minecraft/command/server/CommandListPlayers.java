package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListPlayers extends CommandBase
{
    private static final String __OBFID = "CL_00000615";

    public String getCommandName()
    {
        return "list";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.players.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        sender.addChatMessage(new ChatComponentTranslation("commands.players.list", new Object[] {Integer.valueOf(MinecraftServer.getServer().getCurrentPlayerCount()), Integer.valueOf(MinecraftServer.getServer().getMaxPlayers())}));
        sender.addChatMessage(new ChatComponentText(MinecraftServer.getServer().getConfigurationManager().getPlayerNamesString(args.length > 0 && "uuids".equalsIgnoreCase(args[0]))));
    }
}