package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetDefaultSpawnpoint extends CommandBase
{
    private static final String __OBFID = "CL_00000973";

    public String getCommandName()
    {
        return "setworldspawn";
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
        return "commands.setworldspawn.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 3)
        {
            if (sender.getEntityWorld() == null)
            {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            byte b0 = 0;
            int l = b0 + 1;
            int i = parseIntBounded(sender, args[b0], -30000000, 30000000);
            int j = parseIntBounded(sender, args[l++], 0, 256);
            int k = parseIntBounded(sender, args[l++], -30000000, 30000000);
            sender.getEntityWorld().setSpawnLocation(i, j, k);
            notifyOperators(sender, this, "commands.setworldspawn.success", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
        }
        else
        {
            if (args.length != 0)
            {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = getCommandSenderAsPlayer(sender).getCommandSenderPosition();
            sender.getEntityWorld().setSpawnLocation(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ);
            notifyOperators(sender, this, "commands.setworldspawn.success", new Object[] {Integer.valueOf(chunkcoordinates.posX), Integer.valueOf(chunkcoordinates.posY), Integer.valueOf(chunkcoordinates.posZ)});
        }
    }
}