package net.phoenix.eventsbot.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventManager {
    private final List<Event> events;
    private final String jsonFilePath;
    public Message embed;

    public EventManager(TextChannel textChannel, String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        List<Event> eventsList = loadEventsFromFile();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HackForums Events");
        builder.setDescription("List of upcoming events");

        this.events = eventsList.stream().sorted(Comparator.comparingLong(Event::getTimestamp)).collect(Collectors.toList());

        for (Event e : this.events) {
            String description = String.format("Leader: <@!%s>\nTime: <t:%d:R>\nDescription: %s");
            MessageEmbed.Field field = new MessageEmbed.Field(e.getEventName(), , false);
        }
        textChannel.sendMessageEmbeds(builder.build()).queue(embed -> this.embed = embed);

    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEventsToFile();
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
            return objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Event>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void updateEventEmbed(Event event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Event Information");
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.addField("Event Name", event.getEventName(), false);
        embedBuilder.addField("Event Description", event.getEventDescription(), false);
        embedBuilder.addField("Status", "Started", false);
    }
}
