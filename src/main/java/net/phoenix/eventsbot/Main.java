package net.phoenix.eventsbot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.phoenix.eventsbot.events.EventManager;
import net.phoenix.eventsbot.events.EventSlashCommands;

public class Main {

    public static JDA jda;
    public static EventManager manager;

    public static void main(String[] args) throws InterruptedException {
        Dotenv dotenv = Dotenv.load();
        jda = JDABuilder.createDefault(dotenv.get("bot-token"))
                .setEventManager(new AnnotatedEventManager())
                .build()
                .awaitReady();

        jda.upsertCommand(EventSlashCommands.buildCommand()).queue();

        manager = new EventManager(jda.getGuildById("").getTextChannelById(""), "./events.json");
    }

}
