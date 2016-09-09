package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase
{
    private static final String __OBFID = "CL_00000398";

    public String getCommandName()
    {
        return "xp";
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
        return "commands.xp.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.xp.usage", new Object[0]);
        }
        else
        {
            String s = args[0];
            boolean flag = s.endsWith("l") || s.endsWith("L");

            if (flag && s.length() > 1)
            {
                s = s.substring(0, s.length() - 1);
            }

            int i = parseInt(sender, s);
            boolean flag1 = i < 0;

            if (flag1)
            {
                i *= -1;
            }

            EntityPlayerMP entityplayermp;

            if (args.length > 1)
            {
                entityplayermp = getPlayer(sender, args[1]);
            }
            else
            {
                entityplayermp = getCommandSenderAsPlayer(sender);
            }

            if (flag)
            {
                if (flag1)
                {
                    entityplayermp.addExperienceLevel(-i);
                    notifyOperators(sender, this, "commands.xp.success.negative.levels", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
                }
                else
                {
                    entityplayermp.addExperienceLevel(i);
                    notifyOperators(sender, this, "commands.xp.success.levels", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
                }
            }
            else
            {
                if (flag1)
                {
                    throw new WrongUsageException("commands.xp.failure.widthdrawXp", new Object[0]);
                }

                entityplayermp.addExperience(i);
                notifyOperators(sender, this, "commands.xp.success", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
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