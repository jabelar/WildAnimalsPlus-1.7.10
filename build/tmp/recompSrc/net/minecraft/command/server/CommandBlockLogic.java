package net.minecraft.command.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender
{
    /** The formatting for the timestamp on commands run. */
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");
    /** The number of successful commands run. (used for redstone output) */
    private int successCount;
    private boolean trackOutput = true;
    /** The previously run command. */
    private IChatComponent lastOutput = null;
    /** The command stored in the command block. */
    private String commandStored = "";
    /** The custom name of the command block. (defaults to "@") */
    private String customName = "@";
    private static final String __OBFID = "CL_00000128";

    /**
     * returns the successCount int.
     */
    public int getSuccessCount()
    {
        return this.successCount;
    }

    /**
     * Returns the lastOutput.
     */
    public IChatComponent getLastOutput()
    {
        return this.lastOutput;
    }

    /**
     * Stores data to NBT format.
     */
    public void writeDataToNBT(NBTTagCompound p_145758_1_)
    {
        p_145758_1_.setString("Command", this.commandStored);
        p_145758_1_.setInteger("SuccessCount", this.successCount);
        p_145758_1_.setString("CustomName", this.customName);

        if (this.lastOutput != null)
        {
            p_145758_1_.setString("LastOutput", IChatComponent.Serializer.componentToJson(this.lastOutput));
        }

        p_145758_1_.setBoolean("TrackOutput", this.trackOutput);
    }

    /**
     * Reads NBT formatting and stored data into variables.
     */
    public void readDataFromNBT(NBTTagCompound p_145759_1_)
    {
        this.commandStored = p_145759_1_.getString("Command");
        this.successCount = p_145759_1_.getInteger("SuccessCount");

        if (p_145759_1_.hasKey("CustomName", 8))
        {
            this.customName = p_145759_1_.getString("CustomName");
        }

        if (p_145759_1_.hasKey("LastOutput", 8))
        {
            this.lastOutput = IChatComponent.Serializer.jsonToComponent(p_145759_1_.getString("LastOutput"));
        }

        if (p_145759_1_.hasKey("TrackOutput", 1))
        {
            this.trackOutput = p_145759_1_.getBoolean("TrackOutput");
        }
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(int permissionLevel, String command)
    {
        return permissionLevel <= 2;
    }

    /**
     * Sets the command.
     */
    public void setCommand(String p_145752_1_)
    {
        this.commandStored = p_145752_1_;
    }

    /**
     * Returns the customName of the command block.
     */
    public String getCustomName()
    {
        return this.commandStored;
    }

    public void func_145755_a(World p_145755_1_)
    {
        if (p_145755_1_.isRemote)
        {
            this.successCount = 0;
        }

        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver != null && minecraftserver.isCommandBlockEnabled())
        {
            ICommandManager icommandmanager = minecraftserver.getCommandManager();
            this.successCount = icommandmanager.executeCommand(this, this.commandStored);
        }
        else
        {
            this.successCount = 0;
        }
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        return this.customName;
    }

    public IChatComponent getFormattedCommandSenderName()
    {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void func_145754_b(String p_145754_1_)
    {
        this.customName = p_145754_1_;
    }

    /**
     * Notifies this sender of some sort of information.  This is for messages intended to display to the user.  Used
     * for typical output (like "you asked for whether or not this game rule is set, so here's your answer"), warnings
     * (like "I fetched this block for you by ID, but I'd like you to know that every time you do this, I die a little
     * inside"), and errors (like "it's not called iron_pixacke, silly").
     */
    public void addChatMessage(IChatComponent message)
    {
        if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote)
        {
            this.lastOutput = (new ChatComponentText("[" + timestampFormat.format(new Date()) + "] ")).appendSibling(message);
            this.func_145756_e();
        }
    }

    public abstract void func_145756_e();

    @SideOnly(Side.CLIENT)
    public abstract int func_145751_f();

    @SideOnly(Side.CLIENT)
    public abstract void func_145757_a(ByteBuf p_145757_1_);

    public void func_145750_b(IChatComponent p_145750_1_)
    {
        this.lastOutput = p_145750_1_;
    }
}