package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tv.twitch.chat.ChatUserInfo;

@SideOnly(Side.CLIENT)
public class GuiChat extends GuiScreen implements GuiYesNoCallback
{
    private static final Set supportedProtocols = Sets.newHashSet(new String[] {"http", "https"});
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";
    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List foundPlayerNames = new ArrayList();
    /** used to pass around the URI to various dialogues and to the host os */
    private URI clickedURI;
    /** Chat entry field */
    protected GuiTextField inputField;
    /** is the text that appears when you press the chat key and the input box appears pre-filled */
    private String defaultInputFieldText = "";
    private static final String __OBFID = "CL_00000682";

    public GuiChat() {}

    public GuiChat(String p_i1024_1_)
    {
        this.defaultInputFieldText = p_i1024_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15)
        {
            this.autocompletePlayerNames();
        }
        else
        {
            this.playerNamesFound = false;
        }

        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
        else if (keyCode != 28 && keyCode != 156)
        {
            if (keyCode == 200)
            {
                this.getSentHistory(-1);
            }
            else if (keyCode == 208)
            {
                this.getSentHistory(1);
            }
            else if (keyCode == 201)
            {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            }
            else if (keyCode == 209)
            {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            }
            else
            {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        }
        else
        {
            String s = this.inputField.getText().trim();

            if (s.length() > 0)
            {
                this.submitChatMessage(s);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public void submitChatMessage(String p_146403_1_)
    {
        this.mc.ingameGUI.getChatGUI().addToSentMessages(p_146403_1_);
        if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(mc.thePlayer, p_146403_1_) != 0) return;
        this.mc.thePlayer.sendChatMessage(p_146403_1_);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && this.mc.gameSettings.chatLinks)
        {
            IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (ichatcomponent != null)
            {
                ClickEvent clickevent = ichatcomponent.getChatStyle().getChatClickEvent();

                if (clickevent != null)
                {
                    if (isShiftKeyDown())
                    {
                        this.inputField.writeText(ichatcomponent.getUnformattedTextForChat());
                    }
                    else
                    {
                        URI uri;

                        if (clickevent.getAction() == ClickEvent.Action.OPEN_URL)
                        {
                            try
                            {
                                uri = new URI(clickevent.getValue());

                                if (!supportedProtocols.contains(uri.getScheme().toLowerCase()))
                                {
                                    throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + uri.getScheme().toLowerCase());
                                }

                                if (this.mc.gameSettings.chatLinksPrompt)
                                {
                                    this.clickedURI = uri;
                                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 0, false));
                                }
                                else
                                {
                                    this.openLink(uri);
                                }
                            }
                            catch (URISyntaxException urisyntaxexception)
                            {
                                logger.error("Can\'t open url for " + clickevent, urisyntaxexception);
                            }
                        }
                        else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE)
                        {
                            uri = (new File(clickevent.getValue())).toURI();
                            this.openLink(uri);
                        }
                        else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                        {
                            this.inputField.setText(clickevent.getValue());
                        }
                        else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                        {
                            this.submitChatMessage(clickevent.getValue());
                        }
                        else if (clickevent.getAction() == ClickEvent.Action.TWITCH_USER_INFO)
                        {
                            ChatUserInfo chatuserinfo = this.mc.getTwitchStream().func_152926_a(clickevent.getValue());

                            if (chatuserinfo != null)
                            {
                                this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), chatuserinfo));
                            }
                            else
                            {
                                logger.error("Tried to handle twitch user but couldn\'t find them!");
                            }
                        }
                        else
                        {
                            logger.error("Don\'t know how to handle " + clickevent);
                        }
                    }

                    return;
                }
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 0)
        {
            if (result)
            {
                this.openLink(this.clickedURI);
            }

            this.clickedURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openLink(URI p_146407_1_)
    {
        try
        {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {p_146407_1_});
        }
        catch (Throwable throwable)
        {
            logger.error("Couldn\'t open link", throwable);
        }
    }

    public void autocompletePlayerNames()
    {
        String s1;

        if (this.playerNamesFound)
        {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size())
            {
                this.autocompleteIndex = 0;
            }
        }
        else
        {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty())
            {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1)
        {
            StringBuilder stringbuilder = new StringBuilder();

            for (Iterator iterator = this.foundPlayerNames.iterator(); iterator.hasNext(); stringbuilder.append(s1))
            {
                s1 = (String)iterator.next();

                if (stringbuilder.length() > 0)
                {
                    stringbuilder.append(", ");
                }
            }

            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes((String)this.foundPlayerNames.get(this.autocompleteIndex++)));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_)
    {
        if (p_146405_1_.length() >= 1)
        {
            net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(p_146405_1_, p_146405_2_);
            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_));
            this.waitingOnAutocomplete = true;
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */
    public void getSentHistory(int p_146402_1_)
    {
        int j = this.sentHistoryCursor + p_146402_1_;
        int k = this.mc.ingameGUI.getChatGUI().getSentMessages().size();

        if (j < 0)
        {
            j = 0;
        }

        if (j > k)
        {
            j = k;
        }

        if (j != this.sentHistoryCursor)
        {
            if (j == k)
            {
                this.sentHistoryCursor = k;
                this.inputField.setText(this.historyBuffer);
            }
            else
            {
                if (this.sentHistoryCursor == k)
                {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(j));
                this.sentHistoryCursor = j;
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null)
        {
            HoverEvent hoverevent = ichatcomponent.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = null;

                try
                {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(hoverevent.getValue().getUnformattedText());

                    if (nbtbase != null && nbtbase instanceof NBTTagCompound)
                    {
                        itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbtbase);
                    }
                }
                catch (NBTException nbtexception)
                {
                    ;
                }

                if (itemstack != null)
                {
                    this.renderToolTip(itemstack, mouseX, mouseY);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", mouseX, mouseY);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(Splitter.on("\n").splitToList(hoverevent.getValue().getFormattedText()), mouseX, mouseY);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null)
                {
                    IChatComponent ichatcomponent1 = statbase.getStatName();
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                    chatcomponenttranslation.getChatStyle().setItalic(Boolean.valueOf(true));
                    String s = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    ArrayList arraylist = Lists.newArrayList(new String[] {ichatcomponent1.getFormattedText(), chatcomponenttranslation.getFormattedText()});

                    if (s != null)
                    {
                        arraylist.addAll(this.fontRendererObj.listFormattedStringToWidth(s, 150));
                    }

                    this.drawHoveringText(arraylist, mouseX, mouseY);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", mouseX, mouseY);
                }
            }

            GL11.glDisable(GL11.GL_LIGHTING);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onAutocompleteResponse(String[] p_146406_1_)
    {
        if (this.waitingOnAutocomplete)
        {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();
            String[] astring1 = p_146406_1_;
            int i = p_146406_1_.length;

            String[] complete = net.minecraftforge.client.ClientCommandHandler.instance.latestAutoComplete;
            if (complete != null)
            {
                astring1 = com.google.common.collect.ObjectArrays.concat(complete, astring1, String.class);
                i = astring1.length;
            }

            for (int j = 0; j < i; ++j)
            {
                String s = astring1[j];

                if (s.length() > 0)
                {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(p_146406_1_);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2))
            {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            }
            else if (this.foundPlayerNames.size() > 0)
            {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}