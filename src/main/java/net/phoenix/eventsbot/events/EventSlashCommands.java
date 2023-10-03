package net.phoenix.eventsbot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class EventSlashCommands {

    @SubscribeEvent
    public static void handleCreateCommand(SlashCommandInteractionEvent event) {
        if (!Objects.equals(event.getSubcommandName(), "events")) return;
        if(!Objects.equals(event.getSubcommandName(), "create")) return;
        TextInput subject = TextInput.create("event_name", "Event Name", TextInputStyle.SHORT)
                .setMinLength(5)
                .setMaxLength(100)
                .build();

        TextInput description = TextInput.create("event_description", "Event Description", TextInputStyle.PARAGRAPH)
                .setMaxLength(200)
                .build();

        TextInput timestamp = TextInput.create("timestamp", "Epoch Timestamp of event hosting time", TextInputStyle.PARAGRAPH)
                .setMinLength(10)
                .setMaxLength(10)
                .build();

        Modal.Builder modal = Modal.create("event-creation-menu", "Event Creation Menu");
        modal.addActionRow(subject, description, timestamp);
        event.replyModal(modal.build()).queue();
    }

    public static SlashCommandData buildCommand(){
        return Commands.slash("events", "cute lil housing for event commands")
                .addSubcommands(
                        new SubcommandData("create", "Create an event!"),
                        new SubcommandData("modify", "Modify an event's details")
                );
    }

}
