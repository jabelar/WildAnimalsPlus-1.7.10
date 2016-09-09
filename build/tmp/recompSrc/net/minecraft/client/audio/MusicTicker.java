package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MusicTicker implements IUpdatePlayerListBox
{
    private final Random rand = new Random();
    private final Minecraft mc;
    private ISound currentMusic;
    private int timeUntilNextMusic = 100;
    private static final String __OBFID = "CL_00001138";

    public MusicTicker(Minecraft p_i45112_1_)
    {
        this.mc = p_i45112_1_;
    }

    /**
     * Updates the JList with a new model.
     */
    public void update()
    {
        MusicTicker.MusicType musictype = this.mc.getAmbientMusicType();

        if (this.currentMusic != null)
        {
            if (!musictype.getMusicTickerLocation().equals(this.currentMusic.getSoundLocation()))
            {
                this.mc.getSoundHandler().stopSound(this.currentMusic);
                this.timeUntilNextMusic = MathHelper.getRandomIntegerInRange(this.rand, 0, musictype.getMinDelay() / 2);
            }

            if (!this.mc.getSoundHandler().isSoundPlaying(this.currentMusic))
            {
                this.currentMusic = null;
                this.timeUntilNextMusic = Math.min(MathHelper.getRandomIntegerInRange(this.rand, musictype.getMinDelay(), musictype.getMaxDelay()), this.timeUntilNextMusic);
            }
        }

        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0)
        {
            this.currentMusic = PositionedSoundRecord.createPositionedSoundRecord(musictype.getMusicTickerLocation());
            this.mc.getSoundHandler().playSound(this.currentMusic);
            this.timeUntilNextMusic = Integer.MAX_VALUE;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum MusicType
    {
        MENU(new ResourceLocation("minecraft:music.menu"), 20, 600),
        GAME(new ResourceLocation("minecraft:music.game"), 12000, 24000),
        CREATIVE(new ResourceLocation("minecraft:music.game.creative"), 1200, 3600),
        CREDITS(new ResourceLocation("minecraft:music.game.end.credits"), Integer.MAX_VALUE, Integer.MAX_VALUE),
        NETHER(new ResourceLocation("minecraft:music.game.nether"), 1200, 3600),
        END_BOSS(new ResourceLocation("minecraft:music.game.end.dragon"), 0, 0),
        END(new ResourceLocation("minecraft:music.game.end"), 6000, 24000);
        private final ResourceLocation field_148645_h;
        private final int minDelay;
        private final int maxDelay;

        private static final String __OBFID = "CL_00001139";

        private MusicType(ResourceLocation p_i45111_3_, int p_i45111_4_, int p_i45111_5_)
        {
            this.field_148645_h = p_i45111_3_;
            this.minDelay = p_i45111_4_;
            this.maxDelay = p_i45111_5_;
        }

        public ResourceLocation getMusicTickerLocation()
        {
            return this.field_148645_h;
        }

        /**
         * Returns the minimum delay between playing music of this type.
         */
        public int getMinDelay()
        {
            return this.minDelay;
        }

        /**
         * Returns the maximum delay between playing music of this type.
         */
        public int getMaxDelay()
        {
            return this.maxDelay;
        }
    }
}