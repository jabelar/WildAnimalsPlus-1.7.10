package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTeleport extends CommandBase
{
    private static final String __OBFID = "CL_00001180";

    public String getCommandName()
    {
        return "tp";
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
        return "commands.tp.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp;

            if (args.length != 2 && args.length != 4)
            {
                entityplayermp = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entityplayermp = getPlayer(sender, args[0]);

                if (entityplayermp == null)
                {
                    throw new PlayerNotFoundException();
                }
            }

            if (args.length != 3 && args.length != 4)
            {
                if (args.length == 1 || args.length == 2)
                {
                    EntityPlayerMP entityplayermp1 = getPlayer(sender, args[args.length - 1]);

                    if (entityplayermp1 == null)
                    {
                        throw new PlayerNotFoundException();
                    }

                    if (entityplayermp1.worldObj != entityplayermp.worldObj)
                    {
                        notifyOperators(sender, this, "commands.tp.notSameDimension", new Object[0]);
                        return;
                    }

                    entityplayermp.mountEntity((Entity)null);
                    entityplayermp.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
                    notifyOperators(sender, this, "commands.tp.success", new Object[] {entityplayermp.getCommandSenderName(), entityplayermp1.getCommandSenderName()});
                }
            }
            else if (entityplayermp.worldObj != null)
            {
                int i = args.length - 3;
                double d0 = clamp_coord(sender, entityplayermp.posX, args[i++]);
                double d1 = clamp_double(sender, entityplayermp.posY, args[i++], 0, 0);
                double d2 = clamp_coord(sender, entityplayermp.posZ, args[i++]);
                entityplayermp.mountEntity((Entity)null);
                entityplayermp.setPositionAndUpdate(d0, d1, d2);
                notifyOperators(sender, this, "commands.tp.success.coordinates", new Object[] {entityplayermp.getCommandSenderName(), Double.valueOf(d0), Double.valueOf(d1), Double.valueOf(d2)});
            }
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