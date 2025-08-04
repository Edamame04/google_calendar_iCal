package calendar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder pattern implementation for creating MyEvent objects.
 * This provides a more flexible and readable way to construct events.
 */
public class MyEventBuilder {
    private String uid;
    private String summary;
    private String description;
    private String location;
    private LocalDateTime start;
    private LocalDateTime end;
    private String organizer;
    private List<String> attendees = new ArrayList<>();

    public MyEventBuilder setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public MyEventBuilder setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public MyEventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MyEventBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public MyEventBuilder setStart(LocalDateTime start) {
        this.start = start;
        return this;
    }

    public MyEventBuilder setEnd(LocalDateTime end) {
        this.end = end;
        return this;
    }

    public MyEventBuilder setOrganizer(String organizer) {
        this.organizer = organizer;
        return this;
    }

    public MyEventBuilder addAttendee(String attendee) {
        this.attendees.add(attendee);
        return this;
    }

    public MyEventBuilder setAttendees(List<String> attendees) {
        this.attendees = new ArrayList<>(attendees);
        return this;
    }

    public MyEvent build() {
        return new MyEvent(uid, summary, description, location, start, end, organizer, attendees);
    }
}
