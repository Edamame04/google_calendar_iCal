import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Events;
import utils.CalendarApiConnector;
import utils.InputValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for generating iCal formatted output from Google Calendar events.
 * This class uses the utils.ApiConnector singleton to interact with the Google Calendar API
 * and converts the retrieved events to iCal format.
 */
public class MainCLI {

    /**
     * Main method that orchestrates the calendar selection and iCal generation process.
     * Takes user input from the arguments.
     * Arguments should be in the format: ⟨Calendar-Index⟩ ⟨Start-Date⟩ ⟨End-Date⟩ ⟨Filename⟩
     * If no arguments are provided or if the input is invalid, it will prompt the user interactively.
     * Then it will use them to set the calendar, time range and filename for the iCal export.
     *
     * @param args command line arguments for calendar index, start date, end date, and filename
     */
    public static void main(String... args) {
        // Check for info argument first
        if (args.length > 0 && (args[0].equals("--info") || args[0].equals("-i") || args[0].equals("info") || args[0].equals("--help") || args[0].equals("-h"))) {
            displayProgramInfo();
            return;
        }

        CalendarApiConnector apiConnector; // Initialize utils.ApiConnector instance
        List<CalendarListEntry> calendars; // List to hold user's calendars

        try {
            // Get the singleton instance of utils.ApiConnector
            // This handles authentication and API setup
            apiConnector = CalendarApiConnector.getInstance();

            // Retrieve all user calendars from Google Calendar API
            calendars = apiConnector.getUserCalendars();

            // Display welcome message and calendar list
            displayWelcomeMessage();
            displayCalendarList(calendars);

            // Parse command line arguments or prompt for input
            String calendarIndexStr = args.length > 0 ? args[0] : null;
            String startDateStr = args.length > 1 ? args[1] : null;
            String endDateStr = args.length > 2 ? args[2] : null;
            String fileName = args.length > 3 ? args[3] : null;

            // Use interactive validation for missing arguments
            String[] finalArgs = dynamicInteractiveFallbackWithValidation(calendarIndexStr, startDateStr, endDateStr, fileName, calendars);
            calendarIndexStr = finalArgs[0];
            startDateStr = finalArgs[1];
            endDateStr = finalArgs[2];
            fileName = finalArgs[3];

            // Normalize filename to ensure .ics extension
            fileName = InputValidator.normalizeFileName(fileName);

            // Convert and validate inputs
            int calendarIndex = Integer.parseInt(calendarIndexStr);
            CalendarListEntry selectedCalendar = calendars.get(calendarIndex);

            // Create DateTime objects for API calls
            DateTime startTime = DateTime.parseRfc3339(startDateStr + "T00:00:00Z");
            DateTime endTime = DateTime.parseRfc3339(endDateStr + "T23:59:59Z");

            System.out.println("\n" + "═".repeat(80));
            System.out.println("🔄 FETCHING EVENTS FROM GOOGLE CALENDAR...");
            System.out.println("📅 Calendar: " + selectedCalendar.getSummary());
            System.out.println("⏰ Date Range: " + startDateStr + " to " + endDateStr);
            System.out.println("═".repeat(80));

            // Fetch events from the selected calendar
            Events events = apiConnector.getCalendarEventsByCalendarId(selectedCalendar.getId(), startTime, endTime);

            // Generate iCal content and save to file
            ICal ical = new ICal(events);
            ical.exportICalToFile("./", fileName);

            System.out.println("\n✅ SUCCESS!");
            System.out.println("📁 iCal file generated: " + fileName);
            System.out.println("🎉 Export completed successfully!");
            System.out.println("═".repeat(80));

        } catch (GeneralSecurityException e) {
            System.err.println("🔐 Security Error: Authentication failed - " + e.getMessage());
            System.err.println("💡 Please check your credentials.json file and try again.");
        } catch (IOException e) {
            System.err.println("💾 IO Error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Displays program information, usage instructions, and help text.
     */
    private static void displayProgramInfo() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("📅 GOOGLE CALENDAR TO ICAL CONVERTER");
        System.out.println("═".repeat(80));
        System.out.println();
        System.out.println("📖 DESCRIPTION:");
        System.out.println("   This program exports events from your Google Calendar to iCal format (.ics files).");
        System.out.println("   You can use it in both command line and interactive modes.");
        System.out.println();
        System.out.println("🚀 USAGE:");
        System.out.println("   java MainCLI [calendar-index] [start-date] [end-date] [filename]");
        System.out.println("   java MainCLI --info    (show this help)");
        System.out.println("   java MainCLI           (interactive mode)");
        System.out.println();
        System.out.println("📋 ARGUMENTS:");
        System.out.println("   calendar-index  📊 Index of the calendar (0-based, shown when listing calendars)");
        System.out.println("   start-date      📅 Start date in YYYY-MM-DD format (e.g., 2025-07-29)");
        System.out.println("   end-date        📅 End date in YYYY-MM-DD format (e.g., 2025-08-05)");
        System.out.println("   filename        📁 Output filename (.ics extension added automatically if missing)");
        System.out.println();
        System.out.println("💡 EXAMPLES:");
        System.out.println("   java MainCLI 0 2025-07-29 2025-08-05 my_events");
        System.out.println("   java MainCLI 1 2025-08-01 2025-08-31 work_events.ics");
        System.out.println("   java MainCLI --help");
        System.out.println("   java MainCLI    # Interactive mode - program will prompt for inputs");
        System.out.println();
        System.out.println("🔧 FEATURES:");
        System.out.println("   ✨ Interactive mode with beautiful prompts");
        System.out.println("   ✅ Input validation and error handling");
        System.out.println("   📊 Calendar listing with details");
        System.out.println("   🎨 Colorful and user-friendly interface");
        System.out.println("   📁 Automatic .ics file extension");
        System.out.println();
        System.out.println("🔗 REQUIREMENTS:");
        System.out.println("   📋 Valid Google Calendar API credentials (credentials.json)");
        System.out.println("   🌐 Internet connection for API access");
        System.out.println("   ☕ Java 11 or higher");
        System.out.println("═".repeat(80));
    }

    /**
     * Handles dynamic validation and interactive prompting for missing arguments.
     *
     * @param calendarIndexStr initial calendar index
     * @param startDateStr initial start date
     * @param endDateStr initial end date
     * @param fileName initial filename
     * @param calendars list of available calendars
     * @return array of validated arguments
     */
    private static String[] dynamicInteractiveFallbackWithValidation(String calendarIndexStr, String startDateStr, String endDateStr, String fileName, List<CalendarListEntry> calendars) {
        Scanner scanner = new Scanner(System.in);

        // Validate calendar index
        if (calendarIndexStr == null) {
            System.out.println("\n📋 CALENDAR SELECTION");
            System.out.println("─".repeat(50));
            calendarIndexStr = promptForValidInput(scanner, "📝 Enter calendar index: ",
                    input -> InputValidator.validateCalendarIndex(input, calendars));
        } else {
            InputValidator.ValidationResult result = InputValidator.validateCalendarIndex(calendarIndexStr, calendars);
            if (!result.isValid()) {
                System.out.println("❌ Error: " + result.getErrorMessage());
                System.out.println("\n📋 CALENDAR SELECTION");
                System.out.println("─".repeat(50));
                calendarIndexStr = promptForValidInput(scanner, "📝 Enter calendar index: ",
                        input -> InputValidator.validateCalendarIndex(input, calendars));
            }
        }

        System.out.println();

        // Validate start date
        if (startDateStr == null) {
            System.out.println("📅 START DATE SELECTION");
            System.out.println("─".repeat(50));
            System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");
            startDateStr = promptForValidInput(scanner, "📝 Enter start date: ",
                    input -> InputValidator.validateDate(input, "Start date"));
        } else {
            InputValidator.ValidationResult result = InputValidator.validateDate(startDateStr, "Start date");
            if (!result.isValid()) {
                System.err.println("❌ Error: " + result.getErrorMessage());
                System.out.println("\n📅 START DATE SELECTION");
                System.out.println("─".repeat(50));
                System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");
                startDateStr = promptForValidInput(scanner, "📝 Enter start date: ",
                        input -> InputValidator.validateDate(input, "Start date"));
            }
        }

        System.out.println();

        // Validate end date
        if (endDateStr == null) {
            System.out.println("📅 END DATE SELECTION");
            System.out.println("─".repeat(50));
            System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");
            endDateStr = promptForValidInput(scanner, "📝 Enter end date: ",
                    input -> InputValidator.validateDate(input, "End date"));
        } else {
            InputValidator.ValidationResult result = InputValidator.validateDate(endDateStr, "End date");
            if (!result.isValid()) {
                System.err.println("❌ Error: " + result.getErrorMessage());
                System.out.println("\n📅 END DATE SELECTION");
                System.out.println("─".repeat(50));
                System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");
                endDateStr = promptForValidInput(scanner, "📝 Enter end date: ",
                        input -> InputValidator.validateDate(input, "End date"));
            }
        }

        // Validate date range
        InputValidator.ValidationResult dateRangeResult = InputValidator.validateDateRange(startDateStr, endDateStr);
        if (!dateRangeResult.isValid()) {
            System.err.println("❌ Error: " + dateRangeResult.getErrorMessage());
            System.out.println("🔄 Please re-enter the dates:");
            System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");

            startDateStr = promptForValidInput(scanner, "📝 Enter start date: ",
                    input -> InputValidator.validateDate(input, "Start date"));
            endDateStr = promptForValidInput(scanner, "📝 Enter end date: ",
                    input -> InputValidator.validateDate(input, "End date"));

            dateRangeResult = InputValidator.validateDateRange(startDateStr, endDateStr);
            while (!dateRangeResult.isValid()) {
                System.err.println("❌ Error: " + dateRangeResult.getErrorMessage());
                System.out.println("🔄 Please re-enter the dates:");
                System.out.println("💡 Format: YYYY-MM-DD (e.g., 2025-07-29)");

                startDateStr = promptForValidInput(scanner, "📝 Enter start date: ",
                        input -> InputValidator.validateDate(input, "Start date"));
                endDateStr = promptForValidInput(scanner, "��� Enter end date: ",
                        input -> InputValidator.validateDate(input, "End date"));

                dateRangeResult = InputValidator.validateDateRange(startDateStr, endDateStr);
            }
        }

        System.out.println();

        // Validate filename
        if (fileName == null) {
            System.out.println("📁 FILENAME SELECTION");
            System.out.println("─".repeat(50));
            System.out.println("💡 Example: my_calendar.ics");
            fileName = promptForValidInput(scanner, "📝 Enter filename (e.g., my_calendar.ics): ",
                    input -> InputValidator.validateFileName(input));
        } else {
            InputValidator.ValidationResult result = InputValidator.validateFileName(fileName);
            if (!result.isValid()) {
                System.err.println("❌ Error: " + result.getErrorMessage());
                System.out.println("\n📁 FILENAME SELECTION");
                System.out.println("─".repeat(50));
                System.out.println("💡 Example: my_calendar.ics");
                fileName = promptForValidInput(scanner, "📝 Enter filename (e.g., my_calendar.ics): ",
                        input -> InputValidator.validateFileName(input));
            }
        }

        return new String[]{calendarIndexStr, startDateStr, endDateStr, fileName};
    }

    /**
     * Prompts the user for valid input with retry mechanism.
     *
     * @param scanner the Scanner for input
     * @param prompt the prompt message
     * @param validator the validation function
     * @return valid input string
     */
    private static String promptForValidInput(Scanner scanner, String prompt, java.util.function.Function<String, InputValidator.ValidationResult> validator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            InputValidator.ValidationResult result = validator.apply(input);
            if (result.isValid()) {
                return input;
            } else {
                System.out.println("❌ Error: " + result.getErrorMessage());
                System.out.println("🔄 Please try again.");
            }
        }
    }

    /**
     * Displays a welcome message with program branding.
     */
    private static void displayWelcomeMessage() {
        System.out.println("\n" + "��".repeat(80));
        System.out.println("🎉 WELCOME TO GOOGLE CALENDAR TO ICAL CONVERTER! 🎉");
        System.out.println("═".repeat(80));
        System.out.println("📅 Export your Google Calendar events to iCal format (.ics files)");
        System.out.println("✨ Beautiful • Interactive • Easy to use");
        System.out.println("═".repeat(80));
    }

    /**
     * Displays the list of available calendars in a formatted manner.
     *
     * @param calendars list of calendar entries to display
     */
    private static void displayCalendarList(List<CalendarListEntry> calendars) {
        System.out.println("\n📋 AVAILABLE CALENDARS:");
        System.out.println("─".repeat(80));

        for (int i = 0; i < calendars.size(); i++) {
            CalendarListEntry calendar = calendars.get(i);
            System.out.printf("  📋 [%d] %s\n", i, calendar.getSummary());
            System.out.printf("      📧 ID: %s\n", calendar.getId());
            if (i < calendars.size() - 1) {
                System.out.println();
            }
        }
        System.out.println("─".repeat(80));
    }
}
