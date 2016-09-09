package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class AbstractClientPlayer extends EntityPlayer implements SkinManager.SkinAvailableCallback
{
    public static final ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
    private ResourceLocation locationSkin;
    private ResourceLocation locationCape;
    private static final String __OBFID = "CL_00000935";

    public AbstractClientPlayer(World p_i45074_1_, GameProfile p_i45074_2_)
    {
        super(p_i45074_1_, p_i45074_2_);
        String s = this.getCommandSenderName();

        if (!s.isEmpty())
        {
            SkinManager skinmanager = Minecraft.getMinecraft().getSkinManager();
            skinmanager.func_152790_a(p_i45074_2_, this, true);
        }
    }

    public boolean hasCape()
    {
        return this.locationCape != null;
    }

    public boolean hasSkin()
    {
        return this.locationSkin != null;
    }

    public ResourceLocation getLocationSkin()
    {
        return this.locationSkin == null ? locationStevePng : this.locationSkin;
    }

    public ResourceLocation getLocationCape()
    {
        return this.locationCape;
    }

    public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username)
    {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(resourceLocationIn);

        if (object == null)
        {
            object = new ThreadDownloadImageData((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] {StringUtils.stripControlCodes(username)}), locationStevePng, new ImageBufferDownload());
            texturemanager.loadTexture(resourceLocationIn, (ITextureObject)object);
        }

        return (ThreadDownloadImageData)object;
    }

    public static ResourceLocation getLocationSkin(String username)
    {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    /**
     * Called when the skinmanager completes downloading part of a skin. May be called twice if the player has both a
     * skin and a cape.
     *  
     * @param skinPart Either Type.SKIN or Type.CAPE
     */
    public void onSkinAvailable(Type skinPart, ResourceLocation skinLoc)
    {
        switch (AbstractClientPlayer.SwitchType.SKIN_PART_TYPES[skinPart.ordinal()])
        {
            case 1:
                this.locationSkin = skinLoc;
                break;
            case 2:
                this.locationCape = skinLoc;
        }
    }

    @SideOnly(Side.CLIENT)

    static final class SwitchType
        {
            static final int[] SKIN_PART_TYPES = new int[Type.values().length];
            private static final String __OBFID = "CL_00001832";

            static
            {
                try
                {
                    SKIN_PART_TYPES[Type.SKIN.ordinal()] = 1;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    SKIN_PART_TYPES[Type.CAPE.ordinal()] = 2;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}