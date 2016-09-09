package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEnchantmentTable extends TileEntity
{
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float field_145932_k;
    public float field_145929_l;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation;
    public float bookRotationPrev;
    public float field_145924_q;
    private static Random field_145923_r = new Random();
    private String field_145922_s;
    private static final String __OBFID = "CL_00000354";

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        if (this.func_145921_b())
        {
            compound.setString("CustomName", this.field_145922_s);
        }
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8))
        {
            this.field_145922_s = compound.getString("CustomName");
        }
    }

    public void updateEntity()
    {
        super.updateEntity();
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double)((float)this.xCoord + 0.5F), (double)((float)this.yCoord + 0.5F), (double)((float)this.zCoord + 0.5F), 3.0D);

        if (entityplayer != null)
        {
            double d0 = entityplayer.posX - (double)((float)this.xCoord + 0.5F);
            double d1 = entityplayer.posZ - (double)((float)this.zCoord + 0.5F);
            this.field_145924_q = (float)Math.atan2(d1, d0);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || field_145923_r.nextInt(40) == 0)
            {
                float f1 = this.field_145932_k;

                do
                {
                    this.field_145932_k += (float)(field_145923_r.nextInt(4) - field_145923_r.nextInt(4));
                }
                while (f1 == this.field_145932_k);
            }
        }
        else
        {
            this.field_145924_q += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation >= (float)Math.PI)
        {
            this.bookRotation -= ((float)Math.PI * 2F);
        }

        while (this.bookRotation < -(float)Math.PI)
        {
            this.bookRotation += ((float)Math.PI * 2F);
        }

        while (this.field_145924_q >= (float)Math.PI)
        {
            this.field_145924_q -= ((float)Math.PI * 2F);
        }

        while (this.field_145924_q < -(float)Math.PI)
        {
            this.field_145924_q += ((float)Math.PI * 2F);
        }

        float f2;

        for (f2 = this.field_145924_q - this.bookRotation; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (f2 < -(float)Math.PI)
        {
            f2 += ((float)Math.PI * 2F);
        }

        this.bookRotation += f2 * 0.4F;

        if (this.bookSpread < 0.0F)
        {
            this.bookSpread = 0.0F;
        }

        if (this.bookSpread > 1.0F)
        {
            this.bookSpread = 1.0F;
        }

        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.field_145932_k - this.pageFlip) * 0.4F;
        float f3 = 0.2F;

        if (f < -f3)
        {
            f = -f3;
        }

        if (f > f3)
        {
            f = f3;
        }

        this.field_145929_l += (f - this.field_145929_l) * 0.9F;
        this.pageFlip += this.field_145929_l;
    }

    public String func_145919_a()
    {
        return this.func_145921_b() ? this.field_145922_s : "container.enchant";
    }

    public boolean func_145921_b()
    {
        return this.field_145922_s != null && this.field_145922_s.length() > 0;
    }

    public void func_145920_a(String p_145920_1_)
    {
        this.field_145922_s = p_145920_1_;
    }
}