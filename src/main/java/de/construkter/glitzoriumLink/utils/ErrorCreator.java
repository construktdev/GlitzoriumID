package de.construkter.glitzoriumLink.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class ErrorCreator {
    public static MessageEmbed createError(String message) {
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("Es gab einen Fehler deine Anfrage zu verarbeiten:\n\n" +
                        "```\n" + message + "\n```")
                .setThumbnail("https://cdn.construkter.de/failed.png")
                .setColor(Color.RED)
                .setFooter("GlitzoriumID", "https://cdn.construkter.de/glitzorium.png")
                .build();
    }
}
