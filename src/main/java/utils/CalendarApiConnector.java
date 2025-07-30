/** * utils.ApiConnector.java
 * This class handles the connects to the Google Calendar API.
 * It handles authentication, calendar service initialization, and provides methods
 * to interact with the Google Calendar API.
 * It uses OAuth2 for secure access and allows retrieval of user calendars and events.
 */

package utils;

// Imports necessary Google Calendar API and Java libraries
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Events;

// Imports for handling exceptions and I/O operations
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * ApiConnector is a singleton class that manages the connection to the Google Calendar API.
 * The singleton pattern ensures that only one instance of ApiConnector exists throughout the application,
 * providing a centralized point for accessing the Google Calendar service.
 */
public class CalendarApiConnector {
    
    // Singleton instance
    private static CalendarApiConnector instance;
    
    //Application name for Google Calendar API.
    private static final String APPLICATION_NAME = "Google Calendar ICal Exporter";
    
    // Global instance of the JSON factory for parsing JSON responses.
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    // Directory path where authorization tokens are stored for persistence.
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /*
     * Global instance of the scopes required by this application.
     * This set to read-only access to calendars because it only needs to read calendar data.
     * * The scope defines the level of access the application has to the user's calendar data.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    
    //Path to the credentials file in the resources directory.
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    
    // Instance variables
    private Calendar calendarService;
    private NetHttpTransport httpTransport;
    
    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the HTTP transport and calendar service.
     * 
     * @throws IOException if there's an error with I/O operations
     * @throws GeneralSecurityException if there's a security-related error
     */
    private CalendarApiConnector() throws IOException, GeneralSecurityException {
        initializeService();
    }
    
    /**
     * Returns the singleton instance of utils.ApiConnector.java
     * Creates a new instance if one doesn't exist.
     * The synchronized keyword ensures that only one thread can access this method at a time.
     * Even if I don't use it in a multithreaded environment, it is a good practice to ensure thread safety.
     * 
     * @return the singleton instance of utils.ApiConnector
     * @throws IOException if there's an error with I/O operations
     * @throws GeneralSecurityException if there's a security-related error
     */
    public static synchronized CalendarApiConnector getInstance() throws IOException, GeneralSecurityException {
        if (instance == null) {
            instance = new CalendarApiConnector();
        }
        return instance;
    }
    
    /**
     * Initializes the HTTP transport and Google Calendar service.
     * This method sets up the authenticated connection to Google Calendar API.
     * This is called in the constructor to ensure the service is ready for use.
     * 
     * @throws IOException if there's an error with I/O operations
     * @throws GeneralSecurityException if there's a security-related error
     */
    private void initializeService() throws IOException, GeneralSecurityException {
        // Build a new trusted HTTP transport
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        // Create the Calendar service with authenticated credentials
        calendarService = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    /**
     * Creates an authorized Credential object for Google Calendar API access.
     * This method handles the OAuth2 flow for authentication.
     *
     * @return An authorized Credential object
     * @throws IOException If the credentials.json file cannot be found or read
     */
    private Credential getCredentials() throws IOException {
        // Load client secrets from the credentials file
        InputStream in = CalendarApiConnector.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Credential object resource not found: " + CREDENTIALS_FILE_PATH);
        }
        
        // Parse the client secrets from JSON
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build the authorization flow with required parameters
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline") // Allows refresh tokens for long-term access
                .build();
        
        // Set up local server receiver for OAuth callback
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        
        // Authorize the user and return the credential
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }




    /* Form here on the methods are used to interact with the Google Calendar API */

    /**
     * Retrieves a list of all calendars accessible to the authenticated user.
     * 
     * @return List of CalendarListEntry objects representing user's calendars
     * @throws IOException if there's an error communicating with the API
     */
    public List<CalendarListEntry> getUserCalendars() throws IOException {
        CalendarList calendarList = calendarService.calendarList().list().execute();
        return calendarList.getItems();
    }
    
    /**
     * Retrieves events from a specified calendar within a time range.
     *
     * @param calendarId the ID of the calendar to retrieve events from
     * @param startTime the start time for the event query range
     * @param endTime the end time for the event query range
     * @return Events object containing the list of calendar events
     * @throws IOException if there's an error communicating with the API
     */
    public Events getCalendarEventsByCalendarId(String calendarId, DateTime startTime, DateTime endTime) throws IOException {
        return calendarService.events().list(calendarId)
                .setTimeMin(startTime)     // Events must start after this time
                .setTimeMax(endTime)       // Events must start before this time
                .setOrderBy("startTime")   // Order events by start time
                .setSingleEvents(true)     // Expand recurring events into individual instances
                .execute();
    }

    /**
     * Retrieves events from the primary calendar within a specified time range.
     *
     * @param startTime the start time for the event query range
     * @param endTime the end time for the event query range
     * @return Events object containing the list of events from the primary calendar
     * @throws IOException if there's an error communicating with the API
     */
    public Events getPrimaryCalendarEvents(DateTime startTime, DateTime endTime) throws IOException {
        return getCalendarEventsByCalendarId("primary", startTime, endTime);
    }

    /**
     * Retrieves events from all calendars within a specified time range.
     * This method aggregates events from all calendars accessible to the user.
     *
     * @param startTime the start time for the event query range
     * @param endTime the end time for the event query range
     * @return Events object containing all events from all calendars
     * @throws IOException if there's an error communicating with the API
     */
    public Events getAllCalendarsEvents(DateTime startTime, DateTime endTime) throws IOException {
        List<CalendarListEntry> calendars = getUserCalendars();
        Events allEvents = new Events();

        for (CalendarListEntry calendar : calendars) {
            Events events = getCalendarEventsByCalendarId(calendar.getId(), startTime, endTime);
            if (events.getItems() != null) {
                if (allEvents.getItems() == null) {
                    allEvents.setItems(events.getItems());
                } else {
                    allEvents.getItems().addAll(events.getItems());
                }
            }
        }

        return allEvents;
    }
}
