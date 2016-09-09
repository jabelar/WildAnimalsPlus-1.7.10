package net.minecraft.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood
{
    /** Indicates whether this fish is "cooked" or not. */
    private final boolean cooked;
    private static final String __OBFID = "CL_00000032";

    public ItemFishFood(boolean p_i45338_1_)
    {
        super(0, 0.0F, false);
        this.cooked = p_i45338_1_;
    }

    public int getHealAmount(ItemStack itemStackIn)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.getFishTypeForItemStack(itemStackIn);
        return this.cooked && fishtype.getCookable() ? fishtype.getCookedHealAmount() : fishtype.getUncookedHealAmount();
    }

    public float getSaturationModifier(ItemStack itemStackIn)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.getFishTypeForItemStack(itemStackIn);
        return this.cooked && fishtype.getCookable() ? fishtype.getCookedSaturationModifier() : fishtype.getUncookedSaturationModifier();
    }

    public String getPotionEffect(ItemStack p_150896_1_)
    {
        return ItemFishFood.FishType.getFishTypeForItemStack(p_150896_1_) == ItemFishFood.FishType.PUFFERFISH ? PotionHelper.field_151423_m : null;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        ItemFishFood.FishType[] afishtype = ItemFishFood.FishType.values();
        int i = afishtype.length;

        for (int j = 0; j < i; ++j)
        {
            ItemFishFood.FishType fishtype = afishtype[j];
            fishtype.registerIcon(register);
        }
    }

    protected void onFoodEaten(ItemStack p_77849_1_, World p_77849_2_, EntityPlayer p_77849_3_)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.getFishTypeForItemStack(p_77849_1_);

        if (fishtype == ItemFishFood.FishType.PUFFERFISH)
        {
            p_77849_3_.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
            p_77849_3_.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
            p_77849_3_.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
        }

        super.onFoodEaten(p_77849_1_, p_77849_2_, p_77849_3_);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.getFishTypeForItemDamage(p_77617_1_);
        return this.cooked && fishtype.getCookable() ? fishtype.getCookedIcon() : fishtype.getUncookedIcon();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        ItemFishFood.FishType[] afishtype = ItemFishFood.FishType.values();
        int i = afishtype.length;

        for (int j = 0; j < i; ++j)
        {
            ItemFishFood.FishType fishtype = afishtype[j];

            if (!this.cooked || fishtype.getCookable())
            {
                p_150895_3_.add(new ItemStack(this, 1, fishtype.getItemDamage()));
            }
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        ItemFishFood.FishType fishtype = ItemFishFood.FishType.getFishTypeForItemStack(stack);
        return this.getUnlocalizedName() + "." + fishtype.getUnlocalizedNamePart() + "." + (this.cooked && fishtype.getCookable() ? "cooked" : "raw");
    }

    public static enum FishType
    {
        COD(0, "cod", 2, 0.1F, 5, 0.6F),
        SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
        CLOWNFISH(2, "clownfish", 1, 0.1F),
        PUFFERFISH(3, "pufferfish", 1, 0.1F);
        /** Maps an item damage value for an ItemStack to the corresponding FishType value. */
        private static final Map itemDamageToFishTypeMap = Maps.newHashMap();
        /** The item damage value on an ItemStack that represents this fish type */
        private final int itemDamage;
        /**
         * The value that this fish type uses to replace "XYZ" in: "fish.XYZ.raw" / "fish.XYZ.cooked" for the
         * unlocalized name and "fish_XYZ_raw" / "fish_XYZ_cooked" for the icon string.
         */
        private final String unlocalizedNamePart;
        /** The icon for the uncooked version of this fish. */
        @SideOnly(Side.CLIENT)
        private IIcon uncookedIcon;
        /** The icon for the cooked version of this fish. */
        @SideOnly(Side.CLIENT)
        private IIcon cookedIcon;
        /** The amount that eating the uncooked version of this fish should heal the player. */
        private final int uncookedHealAmount;
        /** The saturation modifier to apply to the heal amount when the player eats the uncooked version of this fish. */
        private final float uncookedSaturationModifier;
        /** The amount that eating the cooked version of this fish should heal the player. */
        private final int cookedHealAmount;
        /** The saturation modifier to apply to the heal amount when the player eats the cooked version of this fish. */
        private final float cookedSaturationModifier;
        /** Indicates whether this type of fish has "raw" and "cooked" variants */
        private boolean cookable = false;

        private static final String __OBFID = "CL_00000033";

        private FishType(int p_i45336_3_, String p_i45336_4_, int p_i45336_5_, float p_i45336_6_, int p_i45336_7_, float p_i45336_8_)
        {
            this.itemDamage = p_i45336_3_;
            this.unlocalizedNamePart = p_i45336_4_;
            this.uncookedHealAmount = p_i45336_5_;
            this.uncookedSaturationModifier = p_i45336_6_;
            this.cookedHealAmount = p_i45336_7_;
            this.cookedSaturationModifier = p_i45336_8_;
            this.cookable = true;
        }

        private FishType(int p_i45337_3_, String p_i45337_4_, int p_i45337_5_, float p_i45337_6_)
        {
            this.itemDamage = p_i45337_3_;
            this.unlocalizedNamePart = p_i45337_4_;
            this.uncookedHealAmount = p_i45337_5_;
            this.uncookedSaturationModifier = p_i45337_6_;
            this.cookedHealAmount = 0;
            this.cookedSaturationModifier = 0.0F;
            this.cookable = false;
        }

        /**
         * Gets the item damage value on an ItemStack that represents this fish type
         */
        public int getItemDamage()
        {
            return this.itemDamage;
        }

        /**
         * Gets the value that this fish type uses to replace "XYZ" in: "fish.XYZ.raw" / "fish.XYZ.cooked" for the
         * unlocalized name and "fish_XYZ_raw" / "fish_XYZ_cooked" for the icon string.
         */
        public String getUnlocalizedNamePart()
        {
            return this.unlocalizedNamePart;
        }

        /**
         * Gets the amount that eating the uncooked version of this fish should heal the player.
         */
        public int getUncookedHealAmount()
        {
            return this.uncookedHealAmount;
        }

        /**
         * Gets the saturation modifier to apply to the heal amount when the player eats the uncooked version of this
         * fish.
         */
        public float getUncookedSaturationModifier()
        {
            return this.uncookedSaturationModifier;
        }

        /**
         * Gets the amount that eating the cooked version of this fish should heal the player.
         */
        public int getCookedHealAmount()
        {
            return this.cookedHealAmount;
        }

        /**
         * Gets the saturation modifier to apply to the heal amount when the player eats the cooked version of this
         * fish.
         */
        public float getCookedSaturationModifier()
        {
            return this.cookedSaturationModifier;
        }

        /**
         * Registers the icon for this fish type in the given IIconRegister.
         */
        @SideOnly(Side.CLIENT)
        public void registerIcon(IIconRegister p_150968_1_)
        {
            this.uncookedIcon = p_150968_1_.registerIcon("fish_" + this.unlocalizedNamePart + "_raw");

            if (this.cookable)
            {
                this.cookedIcon = p_150968_1_.registerIcon("fish_" + this.unlocalizedNamePart + "_cooked");
            }
        }

        /**
         * Gets the icon for the uncooked version of this fish.
         */
        @SideOnly(Side.CLIENT)
        public IIcon getUncookedIcon()
        {
            return this.uncookedIcon;
        }

        /**
         * Gets the icon for the cooked version of this fish.
         */
        @SideOnly(Side.CLIENT)
        public IIcon getCookedIcon()
        {
            return this.cookedIcon;
        }

        /**
         * Gets a value indicating whether this type of fish has "raw" and "cooked" variants.
         */
        public boolean getCookable()
        {
            return this.cookable;
        }

        /**
         * Gets the corresponding FishType value for the given item damage value of an ItemStack, defaulting to COD for
         * unrecognized damage values.
         */
        public static ItemFishFood.FishType getFishTypeForItemDamage(int p_150974_0_)
        {
            ItemFishFood.FishType fishtype = (ItemFishFood.FishType)itemDamageToFishTypeMap.get(Integer.valueOf(p_150974_0_));
            return fishtype == null ? COD : fishtype;
        }

        /**
         * Gets the FishType that corresponds to the given ItemStack, defaulting to COD if the given ItemStack does not
         * actually contain a fish.
         */
        public static ItemFishFood.FishType getFishTypeForItemStack(ItemStack p_150978_0_)
        {
            return p_150978_0_.getItem() instanceof ItemFishFood ? getFishTypeForItemDamage(p_150978_0_.getMetadata()) : COD;
        }

        static
        {
            ItemFishFood.FishType[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                ItemFishFood.FishType var3 = var0[var2];
                itemDamageToFishTypeMap.put(Integer.valueOf(var3.getItemDamage()), var3);
            }
        }
    }
}