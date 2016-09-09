package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

public class CommandEnchant extends CommandBase
{
    private static final String __OBFID = "CL_00000377";

    public String getCommandName()
    {
        return "enchant";
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
        return "commands.enchant.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.enchant.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp = getPlayer(sender, args[0]);
            int i = parseIntBounded(sender, args[1], 0, Enchantment.enchantmentsList.length - 1);
            int j = 1;
            ItemStack itemstack = entityplayermp.getCurrentEquippedItem();

            if (itemstack == null)
            {
                throw new CommandException("commands.enchant.noItem", new Object[0]);
            }
            else
            {
                Enchantment enchantment = Enchantment.enchantmentsList[i];

                if (enchantment == null)
                {
                    throw new NumberInvalidException("commands.enchant.notFound", new Object[] {Integer.valueOf(i)});
                }
                else if (!enchantment.canApply(itemstack))
                {
                    throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
                }
                else
                {
                    if (args.length >= 3)
                    {
                        j = parseIntBounded(sender, args[2], enchantment.getMinLevel(), enchantment.getMaxLevel());
                    }

                    if (itemstack.hasTagCompound())
                    {
                        NBTTagList nbttaglist = itemstack.getEnchantmentTagList();

                        if (nbttaglist != null)
                        {
                            for (int k = 0; k < nbttaglist.tagCount(); ++k)
                            {
                                short short1 = nbttaglist.getCompoundTagAt(k).getShort("id");

                                if (Enchantment.enchantmentsList[short1] != null)
                                {
                                    Enchantment enchantment1 = Enchantment.enchantmentsList[short1];

                                    if (!enchantment1.canApplyTogether(enchantment) || !enchantment.canApplyTogether(enchantment1)) //Forge BugFix: Let Both enchantments veto being together
                                    {
                                        throw new CommandException("commands.enchant.cantCombine", new Object[] {enchantment.getTranslatedName(j), enchantment1.getTranslatedName(nbttaglist.getCompoundTagAt(k).getShort("lvl"))});
                                    }
                                }
                            }
                        }
                    }

                    itemstack.addEnchantment(enchantment, j);
                    notifyOperators(sender, this, "commands.enchant.success", new Object[0]);
                }
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : null;
    }

    protected String[] getListOfPlayers()
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