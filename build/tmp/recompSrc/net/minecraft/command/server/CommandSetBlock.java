package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase
{
    private static final String __OBFID = "CL_00000949";

    public String getCommandName()
    {
        return "setblock";
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
        return "commands.setblock.usage";
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
            Block block = CommandBase.getBlockByText(sender, args[3]);
            int l = 0;

            if (args.length >= 5)
            {
                l = parseIntBounded(sender, args[4], 0, 15);
            }

            World world = sender.getEntityWorld();

            if (!world.blockExists(i, j, k))
            {
                throw new CommandException("commands.setblock.outOfWorld", new Object[0]);
            }
            else
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (args.length >= 7 && block.hasTileEntity())
                {
                    String s = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();

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

                if (args.length >= 6)
                {
                    if (args[5].equals("destroy"))
                    {
                        world.breakBlock(i, j, k, true);
                    }
                    else if (args[5].equals("keep") && !world.isAirBlock(i, j, k))
                    {
                        throw new CommandException("commands.setblock.noChange", new Object[0]);
                    }
                }

                if (!world.setBlock(i, j, k, block, l, 3))
                {
                    throw new CommandException("commands.setblock.noChange", new Object[0]);
                }
                else
                {
                    if (flag)
                    {
                        TileEntity tileentity = world.getTileEntity(i, j, k);

                        if (tileentity != null)
                        {
                            nbttagcompound.setInteger("x", i);
                            nbttagcompound.setInteger("y", j);
                            nbttagcompound.setInteger("z", k);
                            tileentity.readFromNBT(nbttagcompound);
                        }
                    }

                    notifyOperators(sender, this, "commands.setblock.success", new Object[0]);
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.setblock.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 4 ? getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys()) : (args.length == 6 ? getListOfStringsMatchingLastWord(args, new String[] {"replace", "destroy", "keep"}): null);
    }
}