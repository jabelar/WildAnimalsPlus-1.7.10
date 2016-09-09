package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase
{
    private static final String __OBFID = "CL_00000570";

    public String getCommandName()
    {
        return "kill";
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
        return "commands.kill.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        EntityPlayerMP entityplayermp = getCommandSenderAsPlayer(sender);
        entityplayermp.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        sender.addChatMessage(new ChatComponentTranslation("commands.kill.success", new Object[0]));
    }
}