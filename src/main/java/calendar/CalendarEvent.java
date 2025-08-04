package calendar;

/**
 * Interface representing a calendar event that can be converted to iCal format.
 * This allows for different event implementations while maintaining a common contract.
 */
public interface CalendarEvent {
    String getUid();
    String getSummary();
    String getDescription();
    String getLocation();
    java.time.LocalDateTime getStart();
    java.time.LocalDateTime getEnd();
    String getOrganizer();
    java.util.List<String> getAttendees();

    /**
     * Converts the event to iCal format string.
     * @return iCal formatted string representation of the event
     */
    String toICal();
}
