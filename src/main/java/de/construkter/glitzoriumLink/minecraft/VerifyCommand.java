package de.construkter.glitzoriumLink.minecraft;

import de.construkter.glitzoriumLink.GlitzoriumLink;
import de.construkter.glitzoriumLink.api.LinkingAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VerifyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(LinkingAPI.playerCodes.containsKey(commandSender.getName().toLowerCase()))) {
            commandSender.sendMessage(ChatColor.RED + "Du hast keine aktive Verifikation offen!");
            return true;
        }
        if (strings.length != 1) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /verify <code>");
            return true;
        }
        String givenCode = strings[0];
        String correctCode = LinkingAPI.playerCodes.get(commandSender.getName().toLowerCase());

        if (givenCode.equals(correctCode)) {
            commandSender.sendMessage(ChatColor.DARK_GREEN + "Du hast deinen Account erfolgreich verknüpft!");
            LinkingAPI.playerCodes.remove(commandSender.getName().toLowerCase());
            GlitzoriumLink.handleConnectionSuccess(commandSender.getName());
        } else {
            commandSender.sendMessage(ChatColor.RED + "Der Code ist falsch! Bitte probiere es erneut!");
        }
        return true;
    }
}
