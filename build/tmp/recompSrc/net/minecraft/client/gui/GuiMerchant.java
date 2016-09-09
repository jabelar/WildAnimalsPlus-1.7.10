package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class GuiMerchant extends GuiContainer
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation field_147038_v = new ResourceLocation("textures/gui/container/villager.png");
    private IMerchant field_147037_w;
    private GuiMerchant.MerchantButton field_147043_x;
    private GuiMerchant.MerchantButton field_147042_y;
    private int field_147041_z;
    private String field_147040_A;
    private static final String __OBFID = "CL_00000762";

    public GuiMerchant(InventoryPlayer p_i1096_1_, IMerchant p_i1096_2_, World p_i1096_3_, String p_i1096_4_)
    {
        super(new ContainerMerchant(p_i1096_1_, p_i1096_2_, p_i1096_3_));
        this.field_147037_w = p_i1096_2_;
        this.field_147040_A = p_i1096_4_ != null && p_i1096_4_.length() >= 1 ? p_i1096_4_ : I18n.format("entity.Villager.name", new Object[0]);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.buttonList.add(this.field_147043_x = new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
        this.buttonList.add(this.field_147042_y = new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
        this.field_147043_x.enabled = false;
        this.field_147042_y.enabled = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.field_147040_A, this.xSize / 2 - this.fontRendererObj.getStringWidth(this.field_147040_A) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (merchantrecipelist != null)
        {
            this.field_147043_x.enabled = this.field_147041_z < merchantrecipelist.size() - 1;
            this.field_147042_y.enabled = this.field_147041_z > 0;
        }
    }

    protected void actionPerformed(GuiButton button)
    {
        boolean flag = false;

        if (button == this.field_147043_x)
        {
            ++this.field_147041_z;
            flag = true;
        }
        else if (button == this.field_147042_y)
        {
            --this.field_147041_z;
            flag = true;
        }

        if (flag)
        {
            ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.field_147041_z);
            ByteBuf bytebuf = Unpooled.buffer();

            try
            {
                bytebuf.writeInt(this.field_147041_z);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", bytebuf));
            }
            catch (Exception exception)
            {
                logger.error("Couldn\'t send trade info", exception);
            }
            finally
            {
                bytebuf.release();
            }
        }
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_147038_v);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int i1 = this.field_147041_z;
            MerchantRecipe merchantrecipe = (MerchantRecipe)merchantrecipelist.get(i1);

            if (merchantrecipe.isRecipeDisabled())
            {
                this.mc.getTextureManager().bindTexture(field_147038_v);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        MerchantRecipeList merchantrecipelist = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (merchantrecipelist != null && !merchantrecipelist.isEmpty())
        {
            int k = (this.width - this.xSize) / 2;
            int l = (this.height - this.ySize) / 2;
            int i1 = this.field_147041_z;
            MerchantRecipe merchantrecipe = (MerchantRecipe)merchantrecipelist.get(i1);
            GL11.glPushMatrix();
            ItemStack itemstack = merchantrecipe.getItemToBuy();
            ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
            ItemStack itemstack2 = merchantrecipe.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            itemRender.zLevel = 100.0F;
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, k + 36, l + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, k + 36, l + 24);

            if (itemstack1 != null)
            {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack1, k + 62, l + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack1, k + 62, l + 24);
            }

            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack2, k + 120, l + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack2, k + 120, l + 24);
            itemRender.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY))
            {
                this.renderToolTip(itemstack, mouseX, mouseY);
            }
            else if (itemstack1 != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY))
            {
                this.renderToolTip(itemstack1, mouseX, mouseY);
            }
            else if (this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY))
            {
                this.renderToolTip(itemstack2, mouseX, mouseY);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant getMerchant()
    {
        return this.field_147037_w;
    }

    @SideOnly(Side.CLIENT)
    static class MerchantButton extends GuiButton
        {
            private final boolean field_146157_o;
            private static final String __OBFID = "CL_00000763";

            public MerchantButton(int p_i1095_1_, int p_i1095_2_, int p_i1095_3_, boolean p_i1095_4_)
            {
                super(p_i1095_1_, p_i1095_2_, p_i1095_3_, 12, 19, "");
                this.field_146157_o = p_i1095_4_;
            }

            /**
             * Draws this button to the screen.
             */
            public void drawButton(Minecraft mc, int mouseX, int mouseY)
            {
                if (this.visible)
                {
                    mc.getTextureManager().bindTexture(GuiMerchant.field_147038_v);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    int k = 0;
                    int l = 176;

                    if (!this.enabled)
                    {
                        l += this.width * 2;
                    }
                    else if (flag)
                    {
                        l += this.width;
                    }

                    if (!this.field_146157_o)
                    {
                        k += this.height;
                    }

                    this.drawTexturedModalRect(this.xPosition, this.yPosition, l, k, this.width, this.height);
                }
            }
        }
}