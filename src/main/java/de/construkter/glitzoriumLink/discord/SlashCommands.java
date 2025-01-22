package de.construkter.glitzoriumLink.discord;

import de.construkter.glitzoriumLink.api.LinkingAPI;
import de.construkter.glitzoriumLink.utils.Env;
import de.construkter.glitzoriumLink.utils.ErrorCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class SlashCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("auth")) handleAuthCommand(event);
    }

    private void handleAuthCommand(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        // User hat i.d.R. beim ausführen bereits mindestens eine Rolle
        boolean isLinked = Objects.requireNonNull(event.getMember()).getRoles().contains(event.getJDA().getRoleById(Env.LINKED_ID));
        if (isLinked) {
            event.replyEmbeds(ErrorCreator.createError("- Internal: Du bist bereits verknüpft!")).setEphemeral(true).queue();
        }
        var option = event.getOption("name");
        if (option == null) {
            event.replyEmbeds(ErrorCreator.createError("- Internal: Kein Minecraft Nutzername wurde angegeben!")).setEphemeral(true).queue();
            return;
        }
        String name = option.getAsString();
        if (LinkingAPI.playerCodes.containsKey(name.toLowerCase())) {
            event.replyEmbeds(ErrorCreator.createError("- Internal: Du hast bereits einen Code generiert!")).setEphemeral(true).queue();
            return;
        }
        String code = LinkingAPI.generateAuthCode();
        LinkingAPI.playerCodes.put(name.toLowerCase(), code);
        LinkingAPI.playerNames.put(name.toLowerCase(), user.getId());
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTitle("Erfolgreich authentifiziert")
                .setDescription("Du hast dich erfolgreich in unserem System authentifiziert!\n" +
                        "Nun musst du folgendes machen:\n" +
                        "**1.** Trete mit dem Account `" + name + "` **glitzorium.de** bei\n" +
                        "**2.** Um deinen Minecraft Account zu verknüpfen musst du folgenden Command anschließend ausführen:\n" +
                        "`/verify " + code + "`\n" +
                        "\nWenn du dies getan hast, ist dein Minecraft Account nun mit deinem Discord Account verknüpft!")
                .setThumbnail("https://cdn.construkter.de/success.png")
                .setFooter("Glitzorium ID", "https://cdn.construkter.de/glitzorium.png")
                .setColor(Color.GREEN);

        event.replyEmbeds(successEmbed.build()).setEphemeral(true).queue();
    }
}
