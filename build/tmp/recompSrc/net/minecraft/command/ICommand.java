package net.minecraft.command;

import java.util.List;

public interface ICommand extends Comparable
{
    String getCommandName();

    String getCommandUsage(ICommandSender sender);

    List getCommandAliases();

    void processCommand(ICommandSender sender, String[] args);

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    boolean canCommandSenderUseCommand(ICommandSender sender);

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    List addTabCompletionOptions(ICommandSender sender, String[] args);

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    boolean isUsernameIndex(String[] args, int index);
}