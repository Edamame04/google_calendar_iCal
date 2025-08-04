/** ICal.java
 * This class represents an iCalendar (iCal) object that can store multiple events and export them in iCal format.
 * It supports adding events from Google Calendar and exporting to a file.
 */

import utils.EventFactory;
import calendar.CalendarEvent;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import exceptions.ICalExportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private List<CalendarEvent> events;

    /**
     * Default constructor: creates an empty iCal object.
     */
    public ICal() {
        this.events = new ArrayList<>();
    }
    
    /**
     * Constructor to initialize with a list of Google Calendar Events.
     * Makes a defensive copy to prevent external modification.
     *
     * @param events Google Calendar Events to initialize the iCal with
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
     * Adds a Google Calendar event to the iCal object by converting it using EventFactory.
     *
     * @param event The Google Calendar Event to add
     */
    public void addGoogleEvent(Event event) {
        CalendarEvent calendarEvent = EventFactory.createFromGoogle(event);
        this.events.add(calendarEvent);
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
        for (CalendarEvent event : events) {
            icalStringBuilder.append(event.toICal()); // Convert CalendarEvent to iCal format
        }
        icalStringBuilder.append("END:VCALENDAR\r\n");
        return icalStringBuilder.toString(); // Return the complete iCal string
    }
    
    /**
     * Exports the iCal data to a file at the specified path and filename.
     *
     * @param filePath The directory path where the file will be saved
     * @param fileName The name of the file to save the iCal data to
     * @return true if export was successful
     * @throws ICalExportException if writing to the file fails
     */
    public boolean exportICalToFile(String filePath, String fileName) throws ICalExportException {
        try {
            Files.write(
                    Paths.get(filePath, fileName),
                    getICalString().getBytes(),
                    java.nio.file.StandardOpenOption.WRITE,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
            return true;
        } catch (IOException e) {
            throw new ICalExportException("Failed to export iCal to file: " + filePath + "/" + fileName, e);
        }
    }
}