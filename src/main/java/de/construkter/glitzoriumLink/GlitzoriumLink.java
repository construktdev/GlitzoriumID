package de.construkter.glitzoriumLink;

import de.construkter.glitzoriumLink.api.LinkingAPI;
import de.construkter.glitzoriumLink.discord.SlashCommands;
import de.construkter.glitzoriumLink.minecraft.VerifyCommand;
import de.construkter.glitzoriumLink.utils.Env;
import de.construkter.glitzoriumLink.utils.ErrorCreator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public final class GlitzoriumLink extends JavaPlugin {

    private static JDA jda = null;
    public static Guild glitzorium = null;

    @Override
    public void onEnable() {
        jda = JDABuilder.createDefault(Env.TOKEN, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setActivity(Activity.listening("/auth"))
                .addEventListeners(new SlashCommands())
                .build();

        Role tempRole = jda.getRoleById(Env.LINKED_ID);

        if (tempRole != null) {
            glitzorium = tempRole.getGuild();
        }

        if (glitzorium != null) {
            glitzorium.upsertCommand(Commands.slash("auth", "Verknüpfe Minecraft mit Discord").addOption(OptionType.STRING, "name", "Dein Minecraft Name (Bedrock: Mit Punkt davor)")).queue();
            glitzorium.upsertCommand(Commands.slash("clear-cache", "Setze den Verifikations Cache zurück")).queue();
        }

        LinkingAPI.playerCodes = new HashMap<>();
        LinkingAPI.playerNames = new HashMap<>();

        Objects.requireNonNull(getCommand("verify")).setExecutor(new VerifyCommand());
    }

    @Override
    public void onDisable() {
        jda.shutdown();
    }

    @Override
    public void onLoad() {
        getLogger().info("Loading GlitzoriumLink...");
    }

    public static void handleConnectionSuccess(String minecraftName) {
        User user = jda.getUserById(LinkingAPI.playerNames.get(minecraftName.toLowerCase()));

        if (user != null) {
            user.openPrivateChannel().queue((channel) -> {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Erfolgreich verifiziert!")
                        .setDescription("Du hast deinen Minecraft Account `" + minecraftName + "` erfolgreich mit deinem Discord Account verknüpft!")
                        .setThumbnail("https://cdn.construkter.de/success.png")
                        .setFooter("Glitzorium ID", "https://cdn.construkter.de/glitzorium.png")
                        .setColor(Color.GREEN);

                channel.sendMessageEmbeds(embed.build()).queue();

                if (glitzorium != null) {
                    glitzorium.addRoleToMember(user, Objects.requireNonNull(jda.getRoleById(Env.LINKED_ID))).queue();
                } else {
                    channel.sendMessageEmbeds(ErrorCreator.createError("- Fatal: Du kannst dich momentan nicht verifizieren, da die Verifikationsgilde nicht gesetzt wurde\n- Bitte wende dich an einen Admin und probiere es später erneut!")).queue();
                }
            });
        }
    }
}