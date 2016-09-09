package net.minecraft.command.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class CommandAchievement extends CommandBase
{
    private static final String __OBFID = "CL_00000113";

    public String getCommandName()
    {
        return "achievement";
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
        return "commands.achievement.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length >= 2)
        {
            StatBase statbase = StatList.getOneShotStat(args[1]);

            if (statbase == null && !args[1].equals("*"))
            {
                throw new CommandException("commands.achievement.unknownAchievement", new Object[] {args[1]});
            }

            EntityPlayerMP entityplayermp;

            if (args.length >= 3)
            {
                entityplayermp = getPlayer(sender, args[2]);
            }
            else
            {
                entityplayermp = getCommandSenderAsPlayer(sender);
            }

            if (args[0].equalsIgnoreCase("give"))
            {
                if (statbase == null)
                {
                    Iterator iterator = AchievementList.achievementList.iterator();

                    while (iterator.hasNext())
                    {
                        Achievement achievement = (Achievement)iterator.next();
                        entityplayermp.triggerAchievement(achievement);
                    }

                    notifyOperators(sender, this, "commands.achievement.give.success.all", new Object[] {entityplayermp.getCommandSenderName()});
                }
                else
                {
                    if (statbase instanceof Achievement)
                    {
                        Achievement achievement2 = (Achievement)statbase;
                        ArrayList arraylist;

                        for (arraylist = Lists.newArrayList(); achievement2.parentAchievement != null && !entityplayermp.getStatFile().hasAchievementUnlocked(achievement2.parentAchievement); achievement2 = achievement2.parentAchievement)
                        {
                            arraylist.add(achievement2.parentAchievement);
                        }

                        Iterator iterator1 = Lists.reverse(arraylist).iterator();

                        while (iterator1.hasNext())
                        {
                            Achievement achievement1 = (Achievement)iterator1.next();
                            entityplayermp.triggerAchievement(achievement1);
                        }
                    }

                    entityplayermp.triggerAchievement(statbase);
                    notifyOperators(sender, this, "commands.achievement.give.success.one", new Object[] {entityplayermp.getCommandSenderName(), statbase.func_150955_j()});
                }

                return;
            }
        }

        throw new WrongUsageException("commands.achievement.usage", new Object[0]);
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            /**
             * Returns a List of strings (chosen from the given strings) which the last word in the given string array
             * is a beginning-match for. (Tab completion).
             */
            return getListOfStringsMatchingLastWord(args, new String[] {"give"});
        }
        else if (args.length != 2)
        {
            return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
        }
        else
        {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = StatList.allStats.iterator();

            while (iterator.hasNext())
            {
                StatBase statbase = (StatBase)iterator.next();
                arraylist.add(statbase.statId);
            }

            /**
             * Returns a List of strings (chosen from the given string iterable) which the last word in the given string
             * array is a beginning-match for. (Tab completion).
             */
            return getListOfStringsFromIterableMatchingLastWord(args, arraylist);
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 2;
    }
}