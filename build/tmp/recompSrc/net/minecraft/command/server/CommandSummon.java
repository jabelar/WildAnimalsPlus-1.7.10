package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase
{
    private static final String __OBFID = "CL_00001158";

    public String getCommandName()
    {
        return "summon";
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
        return "commands.summon.usage";
    }

    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.summon.usage", new Object[0]);
        }
        else
        {
            String s = args[0];
            double d0 = (double)sender.getCommandSenderPosition().posX + 0.5D;
            double d1 = (double)sender.getCommandSenderPosition().posY;
            double d2 = (double)sender.getCommandSenderPosition().posZ + 0.5D;

            if (args.length >= 4)
            {
                d0 = clamp_coord(sender, d0, args[1]);
                d1 = clamp_coord(sender, d1, args[2]);
                d2 = clamp_coord(sender, d2, args[3]);
            }

            World world = sender.getEntityWorld();

            if (!world.blockExists((int)d0, (int)d1, (int)d2))
            {
                notifyOperators(sender, this, "commands.summon.outOfWorld", new Object[0]);
            }
            else
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (args.length >= 5)
                {
                    IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 4);

                    try
                    {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(ichatcomponent.getUnformattedText());

                        if (!(nbtbase instanceof NBTTagCompound))
                        {
                            notifyOperators(sender, this, "commands.summon.tagError", new Object[] {"Not a valid tag"});
                            return;
                        }

                        nbttagcompound = (NBTTagCompound)nbtbase;
                        flag = true;
                    }
                    catch (NBTException nbtexception)
                    {
                        notifyOperators(sender, this, "commands.summon.tagError", new Object[] {nbtexception.getMessage()});
                        return;
                    }
                }

                nbttagcompound.setString("id", s);
                Entity entity1 = EntityList.createEntityFromNBT(nbttagcompound, world);

                if (entity1 == null)
                {
                    notifyOperators(sender, this, "commands.summon.failed", new Object[0]);
                }
                else
                {
                    entity1.setLocationAndAngles(d0, d1, d2, entity1.rotationYaw, entity1.rotationPitch);

                    if (!flag && entity1 instanceof EntityLiving)
                    {
                        ((EntityLiving)entity1).onSpawnWithEgg((IEntityLivingData)null);
                    }

                    world.spawnEntityInWorld(entity1);
                    Entity entity2 = entity1;

                    for (NBTTagCompound nbttagcompound1 = nbttagcompound; entity2 != null && nbttagcompound1.hasKey("Riding", 10); nbttagcompound1 = nbttagcompound1.getCompoundTag("Riding"))
                    {
                        Entity entity = EntityList.createEntityFromNBT(nbttagcompound1.getCompoundTag("Riding"), world);

                        if (entity != null)
                        {
                            entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);
                            world.spawnEntityInWorld(entity);
                            entity2.mountEntity(entity);
                        }

                        entity2 = entity;
                    }

                    notifyOperators(sender, this, "commands.summon.success", new Object[0]);
                }
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.func_147182_d()) : null;
    }

    protected String[] func_147182_d()
    {
        return (String[])EntityList.func_151515_b().toArray(new String[0]);
    }
}