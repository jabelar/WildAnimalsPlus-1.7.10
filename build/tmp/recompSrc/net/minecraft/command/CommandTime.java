package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase
{
    private static final String __OBFID = "CL_00001183";

    public String getCommandName()
    {
        return "time";
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
        return "commands.time.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            int i;

            if (args[0].equals("set"))
            {
                if (args[1].equals("day"))
                {
                    i = 1000;
                }
                else if (args[1].equals("night"))
                {
                    i = 13000;
                }
                else
                {
                    i = parseIntWithMin(sender, args[1], 0);
                }

                this.setTime(sender, i);
                notifyOperators(sender, this, "commands.time.set", new Object[] {Integer.valueOf(i)});
                return;
            }

            if (args[0].equals("add"))
            {
                i = parseIntWithMin(sender, args[1], 0);
                this.addTime(sender, i);
                notifyOperators(sender, this, "commands.time.added", new Object[] {Integer.valueOf(i)});
                return;
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"set", "add"}): (args.length == 2 && args[0].equals("set") ? getListOfStringsMatchingLastWord(args, new String[] {"day", "night"}): null);
    }

    /**
     * Set the time in the server object.
     */
    protected void setTime(ICommandSender p_71552_1_, int p_71552_2_)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            MinecraftServer.getServer().worldServers[j].setWorldTime((long)p_71552_2_);
        }
    }

    /**
     * Adds (or removes) time in the server object.
     */
    protected void addTime(ICommandSender p_71553_1_, int p_71553_2_)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            WorldServer worldserver = MinecraftServer.getServer().worldServers[j];
            worldserver.setWorldTime(worldserver.getWorldTime() + (long)p_71553_2_);
        }
    }
}