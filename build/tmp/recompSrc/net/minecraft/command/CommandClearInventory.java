package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

public class CommandClearInventory extends CommandBase
{
    private static final String __OBFID = "CL_00000218";

    public String getCommandName()
    {
        return "clear";
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.clear.usage";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        EntityPlayerMP entityplayermp = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
        Item item = args.length >= 2 ? getItemByText(sender, args[1]) : null;
        int i = args.length >= 3 ? parseIntWithMin(sender, args[2], 0) : -1;

        if (args.length >= 2 && item == null)
        {
            throw new CommandException("commands.clear.failure", new Object[] {entityplayermp.getCommandSenderName()});
        }
        else
        {
            int j = entityplayermp.inventory.clearInventory(item, i);
            entityplayermp.inventoryContainer.detectAndSendChanges();

            if (!entityplayermp.capabilities.isCreativeMode)
            {
                entityplayermp.updateHeldItem();
            }

            if (j == 0)
            {
                throw new CommandException("commands.clear.failure", new Object[] {entityplayermp.getCommandSenderName()});
            }
            else
            {
                notifyOperators(sender, this, "commands.clear.success", new Object[] {entityplayermp.getCommandSenderName(), Integer.valueOf(j)});
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.func_147209_d()) : (args.length == 2 ? getListOfStringsFromIterableMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
    }

    protected String[] func_147209_d()
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