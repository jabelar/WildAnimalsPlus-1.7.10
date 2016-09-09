package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity
{
    private int skullType;
    private int skullRotation;
    private GameProfile field_152110_j = null;
    private static final String __OBFID = "CL_00000364";

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte("SkullType", (byte)(this.skullType & 255));
        compound.setByte("Rot", (byte)(this.skullRotation & 255));

        if (this.field_152110_j != null)
        {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTUtil.writeGameProfileToNBT(nbttagcompound1, this.field_152110_j);
            compound.setTag("Owner", nbttagcompound1);
        }
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.skullType = compound.getByte("SkullType");
        this.skullRotation = compound.getByte("Rot");

        if (this.skullType == 3)
        {
            if (compound.hasKey("Owner", 10))
            {
                this.field_152110_j = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
            }
            else if (compound.hasKey("ExtraType", 8) && !StringUtils.isNullOrEmpty(compound.getString("ExtraType")))
            {
                this.field_152110_j = new GameProfile((UUID)null, compound.getString("ExtraType"));
                this.func_152109_d();
            }
        }
    }

    public GameProfile func_152108_a()
    {
        return this.field_152110_j;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 4, nbttagcompound);
    }

    public void func_152107_a(int p_152107_1_)
    {
        this.skullType = p_152107_1_;
        this.field_152110_j = null;
    }

    public void func_152106_a(GameProfile p_152106_1_)
    {
        this.skullType = 3;
        this.field_152110_j = p_152106_1_;
        this.func_152109_d();
    }

    private void func_152109_d()
    {
        if (this.field_152110_j != null && !StringUtils.isNullOrEmpty(this.field_152110_j.getName()))
        {
            if (!this.field_152110_j.isComplete() || !this.field_152110_j.getProperties().containsKey("textures"))
            {
                GameProfile gameprofile = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(this.field_152110_j.getName());

                if (gameprofile != null)
                {
                    Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);

                    if (property == null)
                    {
                        gameprofile = MinecraftServer.getServer().getMinecraftSessionService().fillProfileProperties(gameprofile, true);
                    }

                    this.field_152110_j = gameprofile;
                    this.markDirty();
                }
            }
        }
    }

    public int getSkullType()
    {
        return this.skullType;
    }

    public void setSkullRotation(int p_145903_1_)
    {
        this.skullRotation = p_145903_1_;
    }

    @SideOnly(Side.CLIENT)
    public int getSkullRotation()
    {
        return this.skullRotation;
    }
}