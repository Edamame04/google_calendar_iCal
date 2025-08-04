/** * MyEvent.java
 * MyEvent represents a calendar event with all necessary fields for iCal export.
 * It stores event details such as UID, summary, description, location, start/end times, organizer, and attendees.
 */

package calendar;

public class MyEvent implements CalendarEvent {
    // Fields for the MyEvent class
    private String uid;
    private String summary;
    private String description;
    private String location;
    private java.time.LocalDateTime start;
    private java.time.LocalDateTime end;
    private String organizer;
    private java.util.List<String> attendees;

    /**
     * Constructs a MyEvent with all fields specified.
     *
     * @param uid Unique identifier for the event
     * @param summary Event summary or title
     * @param description Event description
     * @param location Event location
     * @param start Event start time (LocalDateTime)
     * @param end Event end time (LocalDateTime)
     * @param organizer Organizer's email address
     * @param attendees List of attendee email addresses
     */
    public MyEvent(String uid, String summary, String description, String location, java.time.LocalDateTime start,
                   java.time.LocalDateTime end, String organizer, java.util.List<String> attendees) {
        this.uid = uid;
        this.summary = summary;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
        this.organizer = organizer;
        this.attendees = attendees;
    }

    /**
     * Default constructor. Initializes attendees list as empty.
     */
    public MyEvent() {
        this.attendees = new java.util.ArrayList<>();
    }

    /**
     * Converts the MyEvent object to an iCal formatted string.
     * This method constructs the iCal representation of the event with all necessary fields.
     *
     * @return iCal formatted string representing the event
     */
    public String toICal( ) {
        StringBuilder icalStringBuilder = new StringBuilder();
        icalStringBuilder.append("BEGIN:VEVENT\r\n");
        icalStringBuilder.append(formatUid());
        icalStringBuilder.append(formatDtStamp());
        icalStringBuilder.append(formatSummary());
        icalStringBuilder.append(formatDescription());
        icalStringBuilder.append(formatLocation());
        icalStringBuilder.append(formatStart());
        icalStringBuilder.append(formatEnd());
        icalStringBuilder.append(formatOrganizer());
        icalStringBuilder.append(formatAttendees());
        icalStringBuilder.append("END:VEVENT\r\n");
        return icalStringBuilder.toString();
    }

    /**
     * Helper methods to format the fields for iCal export.
     * @return Formatted strings
     */
    private String formatUid() {
        return "UID:" + uid + "\r\n";
    }
    private String formatSummary() {
        return "SUMMARY:" + summary + "\r\n";
    }
    private String formatDescription() {
        return "DESCRIPTION:" + description + "\r\n";
    }
    private String formatLocation() {
        return "LOCATION:" + location + "\r\n";
    }
    private String formatStart() {
        return "DTSTART:" + start.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")) + "\r\n";
    }
    private String formatEnd() {
        return "DTEND:" + end.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")) + "\r\n";
    }
    private String formatOrganizer() {
        return "ORGANIZER:" + organizer + "\r\n";
    }
    private String formatAttendees() {
        StringBuilder sb = new StringBuilder();
        for (String attendee : attendees) {
            sb.append("ATTENDEE:").append(attendee).append("\r\n");
        }
        return sb.toString();
    }
    private String formatDtStamp() {
        java.time.ZonedDateTime nowUtc = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        return "DTSTAMP:" + nowUtc.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")) + "\r\n";
    }

    /**
     * getter and setter methods
     */
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public java.time.LocalDateTime getStart() { return start; }
    public void setStart(java.time.LocalDateTime start) { this.start = start; }
    public java.time.LocalDateTime getEnd() { return end; }
    public void setEnd(java.time.LocalDateTime end) { this.end = end; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public java.util.List<String> getAttendees() { return attendees; }
    public void setAttendees(java.util.List<String> attendees) { this.attendees = attendees; }

    @Override
    public String toString() {
        return "MyEvent{" +
                "uid='" + uid + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", organizer='" + organizer + '\'' +
                ", attendees=" + attendees +
                '}';
    }
}
