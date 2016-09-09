package net.minecraft.command.server;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase
{
    private static final String __OBFID = "CL_00001181";

    public String getCommandName()
    {
        return "testforblock";
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
        return "commands.testforblock.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length >= 4)
        {
            int i = sender.getCommandSenderPosition().posX;
            int j = sender.getCommandSenderPosition().posY;
            int k = sender.getCommandSenderPosition().posZ;
            i = MathHelper.floor_double(clamp_coord(sender, (double)i, args[0]));
            j = MathHelper.floor_double(clamp_coord(sender, (double)j, args[1]));
            k = MathHelper.floor_double(clamp_coord(sender, (double)k, args[2]));
            Block block = Block.getBlockFromName(args[3]);

            if (block == null)
            {
                throw new NumberInvalidException("commands.setblock.notFound", new Object[] {args[3]});
            }
            else
            {
                int l = -1;

                if (args.length >= 5)
                {
                    l = parseIntBounded(sender, args[4], -1, 15);
                }

                World world = sender.getEntityWorld();

                if (!world.blockExists(i, j, k))
                {
                    throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
                }
                else
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    boolean flag = false;

                    if (args.length >= 6 && block.hasTileEntity())
                    {
                        String s = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

                        try
                        {
                            NBTBase nbtbase = JsonToNBT.func_150315_a(s);

                            if (!(nbtbase instanceof NBTTagCompound))
                            {
                                throw new CommandException("commands.setblock.tagError", new Object[] {"Not a valid tag"});
                            }

                            nbttagcompound = (NBTTagCompound)nbtbase;
                            flag = true;
                        }
                        catch (NBTException nbtexception)
                        {
                            throw new CommandException("commands.setblock.tagError", new Object[] {nbtexception.getMessage()});
                        }
                    }

                    Block block1 = world.getBlock(i, j, k);

                    if (block1 != block)
                    {
                        throw new CommandException("commands.testforblock.failed.tile", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), block1.getLocalizedName(), block.getLocalizedName()});
                    }
                    else
                    {
                        if (l > -1)
                        {
                            int i1 = world.getBlockMetadata(i, j, k);

                            if (i1 != l)
                            {
                                throw new CommandException("commands.testforblock.failed.data", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i1), Integer.valueOf(l)});
                            }
                        }

                        if (flag)
                        {
                            TileEntity tileentity = world.getTileEntity(i, j, k);

                            if (tileentity == null)
                            {
                                throw new CommandException("commands.testforblock.failed.tileEntity", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
                            }

                            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                            tileentity.writeToNBT(nbttagcompound1);

                            if (!this.func_147181_a(nbttagcompound, nbttagcompound1))
                            {
                                throw new CommandException("commands.testforblock.failed.nbt", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
                            }
                        }

                        sender.addChatMessage(new ChatComponentTranslation("commands.testforblock.success", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)}));
                    }
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
        }
    }

    public boolean func_147181_a(NBTBase p_147181_1_, NBTBase p_147181_2_)
    {
        if (p_147181_1_ == p_147181_2_)
        {
            return true;
        }
        else if (p_147181_1_ == null)
        {
            return true;
        }
        else if (p_147181_2_ == null)
        {
            return false;
        }
        else if (!p_147181_1_.getClass().equals(p_147181_2_.getClass()))
        {
            return false;
        }
        else if (p_147181_1_ instanceof NBTTagCompound)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_147181_1_;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_147181_2_;
            Iterator iterator = nbttagcompound.getKeySet().iterator();
            String s;
            NBTBase nbtbase2;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                s = (String)iterator.next();
                nbtbase2 = nbttagcompound.getTag(s);
            }
            while (this.func_147181_a(nbtbase2, nbttagcompound1.getTag(s)));

            return false;
        }
        else
        {
            return p_147181_1_.equals(p_147181_2_);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 4 ? getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys()) : null;
    }
}