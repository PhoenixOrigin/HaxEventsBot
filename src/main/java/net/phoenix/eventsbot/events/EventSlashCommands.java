package net.phoenix.eventsbot.events;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.phoenix.eventsbot.Main;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EventSlashCommands {

    @SubscribeEvent
    public static void handleCreateCommand(SlashCommandInteractionEvent event) {
        if (!Objects.equals(event.getSubcommandName(), "events")) return;
        if (!Objects.equals(event.getSubcommandName(), "create")) return;
        TextInput subject = TextInput.create("event_name", "Event Name", TextInputStyle.SHORT)
                .setMinLength(5)
                .setMaxLength(100)
                .setRequired(true)
                .build();

        TextInput description = TextInput.create("event_description", "Event Description", TextInputStyle.PARAGRAPH)
                .setMaxLength(200)
                .setRequired(true)
                .build();

        TextInput timestamp = TextInput.create("timestamp", "Epoch Timestamp of event hosting time", TextInputStyle.SHORT)
                .setMinLength(10)
                .setMaxLength(10)
                .setRequired(true)
                .build();

        TextInput member_cap = TextInput.create("member_cap", "Cap of members allowed", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(3)
                .setRequired(true)
                .build();

        Modal.Builder modal = Modal.create("event-creation-menu", "Event Creation Menu");
        modal.addActionRow(subject, description, timestamp, member_cap);
        event.replyModal(modal.build()).queue();
    }

    @SubscribeEvent
    public static void onCreateModal(ModalInteractionEvent event) {
        if (!event.getModalId().equals("event-creation-menu")) return;

        long timestamp = Long.parseLong(event.getValue("timestamp").getAsString()) * 1000L;
        String event_name = event.getValue("event_name").getAsString();
        String event_description = event.getValue("event_description").getAsString();
        int member_cap = Integer.parseInt(event.getValue("member_cap").getAsString());

        Event haxEvent = new Event(timestamp, member_cap, event.getMember().getIdLong(), event_name, event_description);
        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(""))) {
            Main.manager.addEvent(haxEvent);
        } else {
            event.getGuild().getTextChannelById("").sendMessageEmbeds(Main.manager.generateSuggestionEmbed(haxEvent)).setActionRow(
                    Button.success("event-accept-" + Main.manager.id, "Accept"),
                    Button.danger("event-deny-" + Main.manager.id, "Deny")
            ).queue(message -> {
                Main.manager.list.put(Main.manager.id, haxEvent);
                Main.manager.id++;
            });
        }
    }

    @SubscribeEvent
    public static void onButtonEventThing(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("event")) return;
        List<String> args = Arrays.asList(event.getComponentId().split("-"));
        args.remove(0);
        if (args.get(0).equals("accept")) {
            Event haxEvent = Main.manager.list.get(Integer.parseInt(args.get(1)));
            Main.manager.addEvent(haxEvent);
            event.getGuild().retrieveMemberById(haxEvent.getLeader()).queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Your suggestion of the event \"" + haxEvent.getEventName() + "\" has been approved!").queue();
                });
            });
        } else if (args.get(0).equals("deny")) {
            Event haxEvent = Main.manager.list.get(Integer.parseInt(args.get(1)));
            event.getGuild().retrieveMemberById(haxEvent.getLeader()).queue(member -> {
                member.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Your suggestion of the event \"" + haxEvent.getEventName() + "\" has been denied. For any questions, please DM a cosmonaut").queue();
                });
            });
        }
    }

    public static SlashCommandData buildCommand() {
        return Commands.slash("events", "cute lil housing for event commands")
                .addSubcommands(
                        new SubcommandData("create", "Create an event!")
                );
    }

}
