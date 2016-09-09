package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand
{
    private static IAdminCommand theAdmin;
    private static final String __OBFID = "CL_00001739";

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

    public List getCommandAliases()
    {
        return null;
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    /**
     * Parses an int from the given string.
     */
    public static int parseInt(ICommandSender sender, String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {str});
        }
    }

    /**
     * Parses an int from the given sring with a specified minimum.
     */
    public static int parseIntWithMin(ICommandSender sender, String str, int min)
    {
        /**
         * Parses an int from the given string within a specified bound.
         */
        return parseIntBounded(sender, str, min, Integer.MAX_VALUE);
    }

    /**
     * Parses an int from the given string within a specified bound.
     */
    public static int parseIntBounded(ICommandSender sender, String str, int min, int max)
    {
        int k = parseInt(sender, str);

        if (k < min)
        {
            throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[] {Integer.valueOf(k), Integer.valueOf(min)});
        }
        else if (k > max)
        {
            throw new NumberInvalidException("commands.generic.num.tooBig", new Object[] {Integer.valueOf(k), Integer.valueOf(max)});
        }
        else
        {
            return k;
        }
    }

    /**
     * Parses a double from the given string or throws an exception if it's not a double.
     */
    public static double parseDouble(ICommandSender sender, String str)
    {
        try
        {
            double d0 = Double.parseDouble(str);

            if (!Doubles.isFinite(d0))
            {
                throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {str});
            }
            else
            {
                return d0;
            }
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {str});
        }
    }

    /**
     * Parses a double from the given string.  Throws if the string could not be parsed as a double, or if it's less
     * than the given minimum value.
     */
    public static double parseDouble(ICommandSender sender, String str, double min)
    {
        /**
         * Parses a double from the given string.  Throws if the string could not be parsed as a double, or if it's not
         * between the given min and max values.
         */
        return parseDouble(sender, str, min, Double.MAX_VALUE);
    }

    /**
     * Parses a double from the given string.  Throws if the string could not be parsed as a double, or if it's not
     * between the given min and max values.
     */
    public static double parseDouble(ICommandSender sender, String str, double min, double max)
    {
        double d2 = parseDouble(sender, str);

        if (d2 < min)
        {
            throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d2), Double.valueOf(min)});
        }
        else if (d2 > max)
        {
            throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d2), Double.valueOf(max)});
        }
        else
        {
            return d2;
        }
    }

    /**
     * Parses a boolean value from the given string.  Throws if the string does not contain a boolean value.  Accepted
     * values are (case-sensitive): "true", "false", "0", "1".
     *  
     * @param str accepted values are: true, false, 1, 0
     */
    public static boolean parseBoolean(ICommandSender sender, String str)
    {
        if (!str.equals("true") && !str.equals("1"))
        {
            if (!str.equals("false") && !str.equals("0"))
            {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] {str});
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns the given ICommandSender as a EntityPlayer or throw an exception.
     */
    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender)
    {
        if (sender instanceof EntityPlayerMP)
        {
            return (EntityPlayerMP)sender;
        }
        else
        {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
        }
    }

    public static EntityPlayerMP getPlayer(ICommandSender sender, String username)
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(sender, username);

        if (entityplayermp != null)
        {
            return entityplayermp;
        }
        else
        {
            entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                return entityplayermp;
            }
        }
    }

    public static String getPlayerName(ICommandSender sender, String query)
    {
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(sender, query);

        if (entityplayermp != null)
        {
            return entityplayermp.getCommandSenderName();
        }
        else if (PlayerSelector.hasArguments(query))
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return query;
        }
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int p_147178_2_)
    {
        return getChatComponentFromNthArg(sender, args, p_147178_2_, false);
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean p_147176_3_)
    {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int j = index; j < args.length; ++j)
        {
            if (j > index)
            {
                chatcomponenttext.appendText(" ");
            }

            Object object = new ChatComponentText(args[j]);

            if (p_147176_3_)
            {
                IChatComponent ichatcomponent = PlayerSelector.func_150869_b(sender, args[j]);

                if (ichatcomponent != null)
                {
                    object = ichatcomponent;
                }
                else if (PlayerSelector.hasArguments(args[j]))
                {
                    throw new PlayerNotFoundException();
                }
            }

            chatcomponenttext.appendSibling((IChatComponent)object);
        }

        return chatcomponenttext;
    }

    public static String getStringFromNthArg(ICommandSender sender, String[] args, int index)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int j = index; j < args.length; ++j)
        {
            if (j > index)
            {
                stringbuilder.append(" ");
            }

            String s = args[j];
            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    public static double clamp_coord(ICommandSender sender, double p_110666_1_, String p_110666_3_)
    {
        return clamp_double(sender, p_110666_1_, p_110666_3_, -30000000, 30000000);
    }

    public static double clamp_double(ICommandSender sender, double p_110665_1_, String p_110665_3_, int min, int max)
    {
        boolean flag = p_110665_3_.startsWith("~");

        if (flag && Double.isNaN(p_110665_1_))
        {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {Double.valueOf(p_110665_1_)});
        }
        else
        {
            double d1 = flag ? p_110665_1_ : 0.0D;

            if (!flag || p_110665_3_.length() > 1)
            {
                boolean flag1 = p_110665_3_.contains(".");

                if (flag)
                {
                    p_110665_3_ = p_110665_3_.substring(1);
                }

                d1 += parseDouble(sender, p_110665_3_);

                if (!flag1 && !flag)
                {
                    d1 += 0.5D;
                }
            }

            if (min != 0 || max != 0)
            {
                if (d1 < (double)min)
                {
                    throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[] {Double.valueOf(d1), Integer.valueOf(min)});
                }

                if (d1 > (double)max)
                {
                    throw new NumberInvalidException("commands.generic.double.tooBig", new Object[] {Double.valueOf(d1), Integer.valueOf(max)});
                }
            }

            return d1;
        }
    }

    /**
     * Gets the Item specified by the given text string.  First checks the item registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the item
     * wasn't found.  Returns the item if it was found.
     */
    public static Item getItemByText(ICommandSender sender, String id)
    {
        Item item = (Item)Item.itemRegistry.getObject(id);

        if (item == null)
        {
            try
            {
                Item item1 = Item.getItemById(Integer.parseInt(id));

                if (item1 != null)
                {
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Item.itemRegistry.getNameForObject(item1)});
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    sender.addChatMessage(chatcomponenttranslation);
                }

                item = item1;
            }
            catch (NumberFormatException numberformatexception)
            {
                ;
            }
        }

        if (item == null)
        {
            throw new NumberInvalidException("commands.give.notFound", new Object[] {id});
        }
        else
        {
            return item;
        }
    }

    /**
     * Gets the Block specified by the given text string.  First checks the block registry, then tries by parsing the
     * string as an integer ID (deprecated).  Warns the sender if we matched by parsing the ID.  Throws if the block
     * wasn't found.  Returns the block if it was found.
     */
    public static Block getBlockByText(ICommandSender sender, String id)
    {
        if (Block.blockRegistry.containsKey(id))
        {
            return (Block)Block.blockRegistry.getObject(id);
        }
        else
        {
            try
            {
                int i = Integer.parseInt(id);

                if (Block.blockRegistry.containsId(i))
                {
                    Block block = Block.getBlockById(i);
                    ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.generic.deprecatedId", new Object[] {Block.blockRegistry.getNameForObject(block)});
                    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    sender.addChatMessage(chatcomponenttranslation);
                    return block;
                }
            }
            catch (NumberFormatException numberformatexception)
            {
                ;
            }

            throw new NumberInvalidException("commands.give.notFound", new Object[] {id});
        }
    }

    /**
     * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
     * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
     */
    public static String joinNiceString(Object[] elements)
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < elements.length; ++i)
        {
            String s = elements[i].toString();

            if (i > 0)
            {
                if (i == elements.length - 1)
                {
                    stringbuilder.append(" and ");
                }
                else
                {
                    stringbuilder.append(", ");
                }
            }

            stringbuilder.append(s);
        }

        return stringbuilder.toString();
    }

    /**
     * Creates a linguistic series joining the input chat components.  Examples: 1) {} --> "",  2) {"Steve"} -->
     * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
     */
    public static IChatComponent joinNiceString(IChatComponent[] components)
    {
        ChatComponentText chatcomponenttext = new ChatComponentText("");

        for (int i = 0; i < components.length; ++i)
        {
            if (i > 0)
            {
                if (i == components.length - 1)
                {
                    chatcomponenttext.appendText(" and ");
                }
                else if (i > 0)
                {
                    chatcomponenttext.appendText(", ");
                }
            }

            chatcomponenttext.appendSibling(components[i]);
        }

        return chatcomponenttext;
    }

    /**
     * Creates a linguistic series joining together the elements of the given collection.  Examples: 1) {} --> "",  2)
     * {"Steve"} --> "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil
     * and Mark"
     */
    public static String joinNiceStringFromCollection(Collection strings)
    {
        /**
         * Creates a linguistic series joining the input objects together.  Examples: 1) {} --> "",  2) {"Steve"} -->
         * "Steve",  3) {"Steve", "Phil"} --> "Steve and Phil",  4) {"Steve", "Phil", "Mark"} --> "Steve, Phil and Mark"
         */
        return joinNiceString(strings.toArray(new String[strings.size()]));
    }

    /**
     * Returns true if the given substring is exactly equal to the start of the given string (case insensitive).
     */
    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    /**
     * Returns a List of strings (chosen from the given strings) which the last word in the given string array is a
     * beginning-match for. (Tab completion).
     */
    public static List getListOfStringsMatchingLastWord(String[] args, String ... possibilities)
    {
        String s1 = args[args.length - 1];
        ArrayList arraylist = new ArrayList();
        String[] astring1 = possibilities;
        int i = possibilities.length;

        for (int j = 0; j < i; ++j)
        {
            String s2 = astring1[j];

            if (doesStringStartWith(s1, s2))
            {
                arraylist.add(s2);
            }
        }

        return arraylist;
    }

    /**
     * Returns a List of strings (chosen from the given string iterable) which the last word in the given string array
     * is a beginning-match for. (Tab completion).
     */
    public static List getListOfStringsFromIterableMatchingLastWord(String[] args, Iterable possibilities)
    {
        String s = args[args.length - 1];
        ArrayList arraylist = new ArrayList();
        Iterator iterator = possibilities.iterator();

        while (iterator.hasNext())
        {
            String s1 = (String)iterator.next();

            if (doesStringStartWith(s, s1))
            {
                arraylist.add(s1);
            }
        }

        return arraylist;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object ... msgParams)
    {
        notifyOperators(sender, command, 0, msgFormat, msgParams);
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, int p_152374_2_, String msgFormat, Object ... msgParams)
    {
        if (theAdmin != null)
        {
            theAdmin.notifyOperators(sender, command, p_152374_2_, msgFormat, msgParams);
        }
    }

    /**
     * Sets the static IAdminCommander.
     */
    public static void setAdminCommander(IAdminCommand command)
    {
        theAdmin = command;
    }

    public int compareTo(ICommand p_compareTo_1_)
    {
        return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
    }

    public int compareTo(Object p_compareTo_1_)
    {
        return this.compareTo((ICommand)p_compareTo_1_);
    }
}