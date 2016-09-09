package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase
{
    private static final String __OBFID = "CL_00000323";

    public String getCommandName()
    {
        return "effect";
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
        return "commands.effect.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.effect.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp = getPlayer(sender, args[0]);

            if (args[1].equals("clear"))
            {
                if (entityplayermp.getActivePotionEffects().isEmpty())
                {
                    throw new CommandException("commands.effect.failure.notActive.all", new Object[] {entityplayermp.getCommandSenderName()});
                }

                entityplayermp.clearActivePotions();
                notifyOperators(sender, this, "commands.effect.success.removed.all", new Object[] {entityplayermp.getCommandSenderName()});
            }
            else
            {
                int i = parseIntWithMin(sender, args[1], 1);
                int j = 600;
                int k = 30;
                int l = 0;

                if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null)
                {
                    throw new NumberInvalidException("commands.effect.notFound", new Object[] {Integer.valueOf(i)});
                }

                if (args.length >= 3)
                {
                    k = parseIntBounded(sender, args[2], 0, 1000000);

                    if (Potion.potionTypes[i].isInstant())
                    {
                        j = k;
                    }
                    else
                    {
                        j = k * 20;
                    }
                }
                else if (Potion.potionTypes[i].isInstant())
                {
                    j = 1;
                }

                if (args.length >= 4)
                {
                    l = parseIntBounded(sender, args[3], 0, 255);
                }

                if (k == 0)
                {
                    if (!entityplayermp.isPotionActive(i))
                    {
                        throw new CommandException("commands.effect.failure.notActive", new Object[] {new ChatComponentTranslation(Potion.potionTypes[i].getName(), new Object[0]), entityplayermp.getCommandSenderName()});
                    }

                    entityplayermp.removePotionEffect(i);
                    notifyOperators(sender, this, "commands.effect.success.removed", new Object[] {new ChatComponentTranslation(Potion.potionTypes[i].getName(), new Object[0]), entityplayermp.getCommandSenderName()});
                }
                else
                {
                    PotionEffect potioneffect = new PotionEffect(i, j, l);
                    entityplayermp.addPotionEffect(potioneffect);
                    notifyOperators(sender, this, "commands.effect.success", new Object[] {new ChatComponentTranslation(potioneffect.getEffectName(), new Object[0]), Integer.valueOf(i), Integer.valueOf(l), entityplayermp.getCommandSenderName(), Integer.valueOf(k)});
                }
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
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
        return index == 0;
    }
}