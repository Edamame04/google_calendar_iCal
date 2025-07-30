/** EventConverter.java
 * The EventConverter class is responsible for converting a Google Calendar Event object into a custom MyEvent object.
 * It extracts key information such as the event ID, summary, description, location, organizer's email, and attendee emails.
 * It also handles both timed and all-day events by checking if the event times are set as DateTime or as all-day Date.
 * The start and end times are converted to LocalDateTime using the system's default timezone. Finally, it creates
 * and returns a new MyEvent instance with all the extracted and converted data.
 */

package utils;

// Imports Google Calendar API classes and Java time/date utilities
import calendar.MyEvent;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.client.util.DateTime;

// Imports Java time classes for date and time manipulation
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

// Converts a Google Calendar Event to a custom MyEvent object
public class EventConverter {

    /**
     * Converts a Google Calendar Event object to a MyEvent object.
     * Made static because EventConverter is stateless and used as a utility class.
     * This avoids unnecessary instantiation and follows common Java best practices for utility classes.
     *
     * @param googleEvent The Google Calendar Event to convert
     * @return MyEvent instance with mapped fields
     */
    public static MyEvent convert(Event googleEvent) {
        // Extracts basic event details
        String uid = googleEvent.getId();
        String summary = googleEvent.getSummary();
        String description = googleEvent.getDescription();
        String location = googleEvent.getLocation();
        // Gets organizer's email if available
        String organizer = googleEvent.getOrganizer() != null ? googleEvent.getOrganizer().getEmail() : null;
        // Collects attendee emails
        java.util.List<String> attendees = new java.util.ArrayList<>();
        if (googleEvent.getAttendees() != null) {
            for (EventAttendee attendee : googleEvent.getAttendees()) {
                attendees.add(attendee.getEmail());
            }
        }

        // Handles start and end times (all-day or timed events)
        DateTime startTime = googleEvent.getStart().getDateTime();
        DateTime endTime = googleEvent.getEnd().getDateTime();
        if (startTime == null) {
            startTime = googleEvent.getStart().getDate(); // all-day event
        }
        if (endTime == null) {
            endTime = googleEvent.getEnd().getDate(); // all-day event
        }
        // Converts DateTime to LocalDateTime using system default timezone
        LocalDateTime start = LocalDateTime.ofInstant(new Date(startTime.getValue()).toInstant(), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(new Date(endTime.getValue()).toInstant(), ZoneId.systemDefault());

        // Returns a new MyEvent object with all mapped fields
        return new MyEvent(uid, summary, description, location, start, end, organizer, attendees);
    }
}