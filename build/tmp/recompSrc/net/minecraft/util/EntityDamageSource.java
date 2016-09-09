package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource
{
    protected Entity damageSourceEntity;
    private static final String __OBFID = "CL_00001522";

    public EntityDamageSource(String p_i1567_1_, Entity p_i1567_2_)
    {
        super(p_i1567_1_);
        this.damageSourceEntity = p_i1567_2_;
    }

    public Entity getEntity()
    {
        return this.damageSourceEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public IChatComponent getDeathMessage(EntityLivingBase p_151519_1_)
    {
        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] {p_151519_1_.getFormattedCommandSenderName(), this.damageSourceEntity.getFormattedCommandSenderName(), itemstack.func_151000_E()}): new ChatComponentTranslation(s, new Object[] {p_151519_1_.getFormattedCommandSenderName(), this.damageSourceEntity.getFormattedCommandSenderName()});
    }

    /**
     * Return whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public boolean isDifficultyScaled()
    {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
    }
}