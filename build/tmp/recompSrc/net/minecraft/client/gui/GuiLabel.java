package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiLabel extends Gui
{
    protected int field_146167_a;
    protected int field_146161_f;
    public int field_146162_g;
    public int field_146174_h;
    private ArrayList field_146173_k;
    private boolean centered;
    public boolean visible;
    private boolean labelBgEnabled;
    private int field_146168_n;
    private int field_146169_o;
    private int field_146166_p;
    private int field_146165_q;
    private FontRenderer fontRenderer;
    private int field_146163_s;
    private static final String __OBFID = "CL_00000671";

    public void drawLabel(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int k = this.field_146174_h + this.field_146161_f / 2 + this.field_146163_s / 2;
            int l = k - this.field_146173_k.size() * 10 / 2;

            for (int i1 = 0; i1 < this.field_146173_k.size(); ++i1)
            {
                if (this.centered)
                {
                    this.drawCenteredString(this.fontRenderer, (String)this.field_146173_k.get(i1), this.field_146162_g + this.field_146167_a / 2, l + i1 * 10, this.field_146168_n);
                }
                else
                {
                    this.drawString(this.fontRenderer, (String)this.field_146173_k.get(i1), this.field_146162_g, l + i1 * 10, this.field_146168_n);
                }
            }
        }
    }

    protected void drawLabelBackground(Minecraft p_146160_1_, int p_146160_2_, int p_146160_3_)
    {
        if (this.labelBgEnabled)
        {
            int k = this.field_146167_a + this.field_146163_s * 2;
            int l = this.field_146161_f + this.field_146163_s * 2;
            int i1 = this.field_146162_g - this.field_146163_s;
            int j1 = this.field_146174_h - this.field_146163_s;
            drawRect(i1, j1, i1 + k, j1 + l, this.field_146169_o);
            this.drawHorizontalLine(i1, i1 + k, j1, this.field_146166_p);
            this.drawHorizontalLine(i1, i1 + k, j1 + l, this.field_146165_q);
            this.drawVerticalLine(i1, j1, j1 + l, this.field_146166_p);
            this.drawVerticalLine(i1 + k, j1, j1 + l, this.field_146165_q);
        }
    }
}