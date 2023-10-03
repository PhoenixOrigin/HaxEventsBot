package net.phoenix.eventsbot.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private final String jsonFilePath;
    public Message embed;
    public Map<Integer, Event> list = new HashMap<>();
    public int id = 0;
    private List<Event> events;

    public EventManager(TextChannel textChannel, String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        List<Event> eventsList = loadEventsFromFile();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HackForums Events");
        builder.setDescription("List of upcoming events");

        this.events = eventsList.stream().sorted(Comparator.comparingLong(Event::getTimestamp)).collect(Collectors.toList());

        for (Event e : this.events) {
            String description = String.format("Leader: <@!%s>\nTime: <t:%d:R>\nDescription: %s", e.getLeader(), e.getTimestamp(), e.getEventDescription());
            builder.addField(new MessageEmbed.Field(e.getEventName(), description, false));
        }
        textChannel.sendMessageEmbeds(builder.build()).queue(embed -> this.embed = embed);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEventsToFile();
        updateEventEmbed();
    }

    public void removeEvent(Event event) {
        events.remove(event);
        saveEventsToFile();
    }

    public void saveEventsToFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(jsonFilePath), events);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Event> loadEventsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Event>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public MessageEmbed generateSuggestionEmbed(Event event) {
        EmbedBuilder builder = new EmbedBuilder();
        String description = String.format("Leader: <@!%s>\nTime: <t:%d:R>\nDescription: %s\nMember cap: %d", event.getLeader(), event.getTimestamp(), event.getEventDescription(), event.member_cap);
        builder.setDescription(description);
        builder.setTitle("Event suggestion by <@!" + event.getLeader() + ">");
        return builder.build();
    }

    private List<MessageEmbed> generateEmbeds() {
        List<MessageEmbed> embeds = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HackForums Events");
        builder.setDescription("List of running events");

        for (Event e : this.events) {
            if (!e.running) {
                continue;
            }
            String description = String.format("Leader: <@!%s>\n\nDescription: %s\n **This Event is Running!**", e.getLeader(), e.getEventDescription());
            builder.addField(new MessageEmbed.Field(e.getEventName(), description, false));
        }

        embeds.add(builder.build());

        builder = new EmbedBuilder();
        builder.setTitle("HackForums Events");
        builder.setDescription("List of upcoming events");

        this.events = events.stream().sorted(Comparator.comparingLong(Event::getTimestamp)).collect(Collectors.toList());

        for (Event e : this.events) {
            String description = String.format("Leader: <@!%s>\nTime: <t:%s:R>\nDescription: %s\nAttendees: %d/%d", e.getLeader(), e.getTimestamp(), e.getEventDescription(), e.attendees.size(), e.member_cap);
            if (e.running) {
                continue;
            }
            builder.addField(new MessageEmbed.Field(e.getEventName(), description, false));
        }

        embeds.add(builder.build());

        return embeds;
    }

    private void updateEventEmbed() {
        embed.editMessageEmbeds(generateEmbeds()).queue();
    }
}
