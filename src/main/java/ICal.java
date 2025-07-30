/** ICal.java
 * This class represents an iCalendar (iCal) object that can store multiple events and export them in iCal format.
 * It supports adding events from Google Calendar and exporting to a file.
 */

import utils.EventConverter;
import calendar.MyEvent;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.List;
import java.util.ArrayList;

/**
 * The ICal class provides functionality to store calendar events and export them in iCalendar (iCal) format.
 * It supports adding events from Google Calendar and exporting the iCal data to a file.
 */
public class ICal {
    /**
     * List to hold events in iCal format.
     */
    private List<MyEvent> events;

    /**
     * Default constructor: creates an empty iCal object.
     */
    public ICal() {
        this.events = new ArrayList<>();
    }
    
    /**
     * Constructor to initialize with a list of MyEvent objects.
     * Makes a defensive copy to prevent external modification.
     *
     * @param events List of MyEvent objects to initialize the iCal with
     */
    public ICal(Events events) {
        this.events = new ArrayList<>();
        if (events != null && events.getItems() != null) {
            for (Event event : events.getItems()) {
                addGoogleEvent(event); // Convert and add each Google Event
            }
        }
    }

    /**
     * Adds a Google Calendar event to the iCal object by converting it to MyEvent.
     *
     * @param event The Google Calendar Event to add
     */
    public void addGoogleEvent(Event event) {
        MyEvent myEvent = EventConverter.convert(event); // Convert Google Event to MyEvent
        this.events.add(myEvent); // Add MyEvent to the list
    }

    /**
     * Generates the iCal string representation of all events.
     *
     * @return The iCal formatted string containing all events
     */
    public String getICalString() {
        StringBuilder icalStringBuilder = new StringBuilder();
        icalStringBuilder.append("BEGIN:VCALENDAR\r\n");
        icalStringBuilder.append("VERSION:2.0\r\n");
        icalStringBuilder.append("PRODID:-//My Calendar iCal Exporter//EN\r\n");
        for (MyEvent myEvent : events) {
            icalStringBuilder.append(myEvent.toICal()); // Convert MyEvent to iCal format
        }
        icalStringBuilder.append("END:VCALENDAR\r\n");
        return icalStringBuilder.toString(); // Return the complete iCal string
    }
    
    /**
     * Exports the iCal data to a file at the specified path and filename.
     *
     * @param filePath The directory path where the file will be saved
     * @param fileName The name of the file to save the iCal data to
     * @throws RuntimeException if writing to the file fails
     */
    public boolean exportICalToFile(String filePath, String fileName) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath, fileName), getICalString().getBytes());
            return true;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to export iCal to file: " + filePath + "/" + fileName, e);
        }
    }
}