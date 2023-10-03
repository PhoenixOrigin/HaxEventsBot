package net.phoenix.eventsbot.events;

import net.dv8tion.jda.api.entities.Member;
import net.phoenix.eventsbot.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
 public class Event {
        private final List<Long> attendees;
        private final Long timestamp;
        private final int member_cap;
        private final Long planner;
        private final String event_name;
        private final String event_description;

        public Event(Long timestamp, int member_cap, Long planner, String event_name, String event_description) {
            this.timestamp = timestamp;
            this.member_cap = member_cap;
            this.planner = planner;
            this.event_name = event_name;
            this.event_description = event_description;
            this.attendees = new ArrayList<>();
            queue();
        }

        public String getEventName() {
            return event_name;
        }

        public String getEventDescription() {
            return event_description;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getLeader(){
            return planner;
        }

        public void registerAttendee(Member member) {
            if (attendees.size() == member_cap) return;
            attendees.add(member.getIdLong());
        }

        public void queue() {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            long currentTime = System.currentTimeMillis();
            long timeUntilEventStart = timestamp - currentTime;
            long reminderTime = timeUntilEventStart - (30 * 60 * 1000);


            ScheduledFuture<?> reminderFuture = scheduler.schedule(() -> {
                Main.jda.retrieveUserById(planner).queue(member -> member.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your event \"" + event_name + "\" is scheduled to start in 30 minutes! Please message us ASAP if you are unable to host your event.").queue()));
                }, reminderTime, TimeUnit.MILLISECONDS);
        }
    }
