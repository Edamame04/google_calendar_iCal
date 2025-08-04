# Google Calendar to iCal Converter - Project Structure Documentation

## Project Overview
A Java application that exports Google Calendar events to iCal format (.ics files) using object-oriented design principles and patterns.

## Architecture Overview

```
google_calendar_iCal/
├── src/main/java/
│   ├── ICal.java                    # Main iCal container class
│   ├── MainCLI.java                 # Command-line interface
│   ├── calendar/                    # Domain objects package
│   │   ├── CalendarEvent.java       # Event interface (abstraction)
│   │   ├── MyEvent.java            # Concrete event implementation
│   │   └── MyEventBuilder.java     # Builder pattern for event creation
│   ├── exceptions/                  # Custom exception handling
│   │   └── ICalExportException.java # Export-specific exceptions
│   └── utils/                       # Utility classes package
│       ├── CalendarApiConnector.java # Google Calendar API integration
│       ├── EventConverter.java      # Event conversion logic
│       ├── EventFactory.java       # Factory pattern for event creation
│       └── InputValidator.java      # Input validation utilities
├── src/main/resources/
│  └── credentials.json             # Google API credentials
└── tokens                          # Token storage for Google API
```

## Object-Oriented Design Patterns Implemented

### 1. **Interface Segregation Principle**
- **`CalendarEvent` Interface**: Defines contract for all calendar events
- **Benefits**: Allows different event implementations, improves testability

### 2. **Factory Pattern**
- **`EventFactory`**: Centralized creation of CalendarEvent objects
- **Methods**:
  - `createFromGoogle(Event)`: Creates events from Google Calendar API
  - `createFromData(...)`: Creates events from raw data
- **Benefits**: Encapsulates object creation, easy to extend for new event types

### 3. **Builder Pattern**
- **`MyEventBuilder`**: Fluent API for constructing complex MyEvent objects
- **Benefits**: Readable code, handles optional parameters, immutable object creation

### 4. **Singleton Pattern**
- **`CalendarApiConnector`**: Single instance for Google API connections
- **Benefits**: Resource management, consistent authentication state

### 5. **Custom Exception Handling**
- **`ICalExportException`**: Specific exceptions for export operations
- **Benefits**: Clear error handling, better debugging information

## Class Responsibilities

### Core Classes

#### `ICal.java`
**Purpose**: Main container for iCalendar data and export functionality
**Key Responsibilities**:
- Store collection of CalendarEvent objects
- Generate iCal formatted strings
- Export to .ics files
- Handle export exceptions

**Key Methods**:
```java
public void addGoogleEvent(Event event)           // Add Google Calendar event
public String getICalString()                     // Generate iCal format
public boolean exportICalToFile(String, String)  // Export to file
```

#### `MainCLI.java`
**Purpose**: Command-line interface and application entry point
**Key Responsibilities**:
- Parse command-line arguments
- Handle user interaction
- Coordinate between API and export functionality
- Manage application flow and error handling

### Domain Package (`calendar/`)

#### `CalendarEvent.java` (Interface)
**Purpose**: Define contract for all calendar events
**Methods**:
```java
String getUid(), getSummary(), getDescription(), getLocation()
LocalDateTime getStart(), getEnd()
String getOrganizer()
List<String> getAttendees()
String toICal()  // Convert to iCal format
```

#### `MyEvent.java`
**Purpose**: Concrete implementation of calendar events
**Key Features**:
- Implements `CalendarEvent` interface
- Stores all event data (UID, summary, description, location, times, organizer, attendees)
- Provides iCal formatting logic
- Includes getter/setter methods

#### `MyEventBuilder.java`
**Purpose**: Builder pattern for creating MyEvent objects
**Usage Example**:
```java
MyEvent event = new MyEventBuilder()
    .setUid("123")
    .setSummary("Meeting")
    .setStart(startTime)
    .setEnd(endTime)
    .build();
```

### Utilities Package (`utils/`)

#### `EventConverter.java`
**Purpose**: Convert Google Calendar Event objects to CalendarEvent objects
**Key Features**:
- Static utility methods
- Handles date/time conversion
- Extracts organizer and attendee information
- Returns CalendarEvent interface (not concrete class)

#### `EventFactory.java`
**Purpose**: Factory for creating CalendarEvent instances
**Key Features**:
- Centralizes object creation logic
- Uses EventConverter internally
- Provides multiple creation methods
- Returns interface types for flexibility

#### `CalendarApiConnector.java`
**Purpose**: Singleton for Google Calendar API integration
**Key Features**:
- Manages authentication
- Provides calendar listing
- Fetches events by date range
- Handles API-specific exceptions

#### `InputValidator.java`
**Purpose**: Validate user inputs and command-line arguments
**Key Features**:
- Date format validation
- Calendar index validation
- Filename validation
- Returns ValidationResult objects

### Exceptions Package (`exceptions/`)

#### `ICalExportException.java`
**Purpose**: Custom exception for export-related errors
**Key Features**:
- Extends Exception (checked exception)
- Provides specific error context
- Wraps underlying IO exceptions

## Design Principles Applied

### 1. **Single Responsibility Principle (SRP)**
- Each class has one clear purpose
- EventConverter only handles conversion
- InputValidator only handles validation
- ICal only handles iCal operations

### 2. **Open/Closed Principle (OCP)**
- CalendarEvent interface allows new event types without modifying existing code
- Factory pattern allows new creation methods
- Strategy pattern ready for different export formats

### 3. **Dependency Inversion Principle (DIP)**
- ICal depends on CalendarEvent interface, not concrete MyEvent
- EventFactory returns interfaces, not concrete classes
- Easier testing with mock implementations

### 4. **Interface Segregation Principle (ISP)**
- CalendarEvent interface contains only necessary methods
- No forced implementation of unused methods

### 5. **Don't Repeat Yourself (DRY)**
- EventConverter centralizes conversion logic
- InputValidator centralizes validation logic
- Factory pattern centralizes creation logic

## Data Flow

```
1. User Input → MainCLI
2. MainCLI → CalendarApiConnector (fetch calendars)
3. MainCLI → CalendarApiConnector (fetch events)
4. Google Events → EventFactory → CalendarEvent objects
5. CalendarEvent objects → ICal container
6. ICal → Generate iCal string → Export to file
```

## Extension Points

### Easy to Add:
1. **New Event Types**: Implement CalendarEvent interface
2. **New Export Formats**: Strategy pattern for exporters
3. **New Input Sources**: Factory pattern for event creation
4. **New Validation Rules**: Extend InputValidator
5. **New Exception Types**: Custom exception hierarchy

### Future Enhancements:
- **Observer Pattern**: Progress notifications during export
- **Command Pattern**: CLI command objects
- **Strategy Pattern**: Multiple export formats (JSON, XML)
- **Template Method**: Different calendar provider integrations

## Testing Strategy

### Unit Testing Ready:
- Interface-based design allows easy mocking
- Factory pattern isolates object creation
- Single responsibility makes testing focused
- Custom exceptions provide specific test scenarios

### Integration Testing:
- CalendarApiConnector can be mocked for API testing
- File export can be tested with temporary directories
- End-to-end testing through MainCLI

## Benefits of Current Structure

1. **Maintainability**: Clear separation of concerns
2. **Extensibility**: Easy to add new features
3. **Testability**: Interface-based design
4. **Reusability**: Utility classes can be reused
5. **Error Handling**: Specific exceptions for different scenarios
6. **Code Quality**: Follows OOP principles and design patterns

## Dependencies

- **Google Calendar API**: For calendar integration
- **Java Time API**: For date/time handling
- **Java NIO**: For file operations
- **Standard Java Collections**: For data structures
