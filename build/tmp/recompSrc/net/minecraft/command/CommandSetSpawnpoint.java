package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetSpawnpoint extends CommandBase
{
    private static final String __OBFID = "CL_00001026";

    public String getCommandName()
    {
        return "spawnpoint";
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
        return "commands.spawnpoint.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        EntityPlayerMP entityplayermp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);

        if (args.length == 4)
        {
            if (entityplayermp.worldObj != null)
            {
                byte b0 = 1;
                int i = 30000000;
                int i1 = b0 + 1;
                int j = parseIntBounded(sender, args[b0], -i, i);
                int k = parseIntBounded(sender, args[i1++], 0, 256);
                int l = parseIntBounded(sender, args[i1++], -i, i);
                entityplayermp.setSpawnChunk(new ChunkCoordinates(j, k, l), true);
                notifyOperators(sender, this, "commands.spawnpoint.success", new Object[] {entityplayermp.getCommandSenderName(), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(l)});
            }
        }
        else
        {
            if (args.length > 1)
            {
                throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = entityplayermp.getCommandSenderPosition();
            entityplayermp.setSpawnChunk(chunkcoordinates, true);
            notifyOperators(sender, this, "commands.spawnpoint.success", new Object[] {entityplayermp.getCommandSenderName(), Integer.valueOf(chunkcoordinates.posX), Integer.valueOf(chunkcoordinates.posY), Integer.valueOf(chunkcoordinates.posZ)});
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}