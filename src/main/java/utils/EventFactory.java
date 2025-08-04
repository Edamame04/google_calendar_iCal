package utils;

import calendar.CalendarEvent;
import calendar.MyEvent;
import com.google.api.services.calendar.model.Event;

/**
 * Factory for creating different types of calendar events.
 * This pattern allows for easy extension to support other calendar formats.
 */
public class EventFactory {

    /**
     * Creates a CalendarEvent from a Google Calendar Event.
     * @param googleEvent The source Google Calendar event
     * @return CalendarEvent implementation
     */
    public static CalendarEvent createFromGoogle(Event googleEvent) {
        return EventConverter.convert(googleEvent);
    }

    /**
     * Creates a CalendarEvent from raw data.
     * @param uid Event unique identifier
     * @param summary Event title
     * @param description Event description
     * @param location Event location
     * @param start Start time
     * @param end End time
     * @param organizer Organizer email
     * @param attendees List of attendee emails
     * @return CalendarEvent implementation
     */
    public static CalendarEvent createFromData(String uid, String summary, String description,
                                             String location, java.time.LocalDateTime start,
                                             java.time.LocalDateTime end, String organizer,
                                             java.util.List<String> attendees) {
        return new MyEvent(uid, summary, description, location, start, end, organizer, attendees);
    }
}
