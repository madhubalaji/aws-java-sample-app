# 🎬 Movie Search and Filtering Feature 🏴‍☠️

Ahoy matey! Welcome to the spectacular movie search treasure hunt feature! This enhancement adds powerful search and filtering capabilities to the existing movie service.

## 🚀 New Features

### 1. Movie Search Endpoint
- **URL**: `/movies/search`
- **Method**: GET
- **Parameters**: 
  - `name` (optional): Search for movies by name (partial match, case-insensitive)
  - `id` (optional): Search for movies by exact ID
  - `genre` (optional): Filter movies by genre (exact match, case-insensitive)

### 2. Enhanced Movie Model
- Added `genre` field to Movie class
- Backward compatible with existing code
- Default genre is "Unknown" for movies without specified genre

### 3. Interactive Search Form
- Beautiful HTML search form with pirate theme 🏴‍☠️
- Input fields for name, ID, and genre
- Dropdown selection for popular genres
- Search and "View All Movies" buttons

### 4. Enhanced Movie Display
- Movies now show ID, name, and genre
- Improved styling with color-coded elements
- Search results counter
- User-friendly "no results" messages

## 🔍 How to Use

### Basic Search Examples

1. **Search by Movie Name** (partial match):
   ```
   GET /movies/search?name=pirate
   ```
   Returns all movies containing "pirate" in the name

2. **Search by Movie ID** (exact match):
   ```
   GET /movies/search?id=1
   ```
   Returns the movie with ID 1

3. **Search by Genre** (exact match):
   ```
   GET /movies/search?genre=Adventure
   ```
   Returns all Adventure movies

4. **Combined Search**:
   ```
   GET /movies/search?name=treasure&genre=Adventure
   ```
   Returns Adventure movies containing "treasure" in the name

5. **Search Form Access**:
   ```
   GET /movies/search
   ```
   Shows the search form without parameters

## 🎭 Available Genres

The system supports the following genres:
- Action
- Adventure  
- Comedy
- Drama
- Fantasy
- Horror
- Romance
- Sci-Fi
- Thriller
- Unknown (default)

## 🛡️ Input Validation and Error Handling

### Validation Rules
- **Movie Name**: 1-200 characters, no empty strings
- **Movie ID**: Must be positive integer
- **Genre**: 1-50 characters, no empty strings

### Error Handling
- Invalid parameters return empty results with user-friendly messages
- AppConfig failures automatically fallback to static movie data
- All errors are logged for debugging
- Graceful handling of malformed requests

## 🏗️ Technical Implementation

### Enhanced Components

1. **Movie.java**
   - Added `genre` field with getter method
   - Backward compatible constructor
   - Default genre handling

2. **MoviesController.java**
   - New `/movies/search` endpoint with `@GetMapping`
   - Parameter validation using MovieUtils
   - Filtering logic with Java Streams
   - Enhanced JSON parsing for genre field
   - Updated static movie data with genres

3. **HTMLBuilder.java**
   - New `getSearchResultsHtml()` method
   - Interactive search form generation
   - Enhanced movie display with styling
   - Result counter and empty state handling

4. **MovieUtils.java**
   - Added `isValidMovieName()` method
   - New validation methods for ID and genre
   - Case-insensitive matching utilities
   - Comprehensive input sanitization

### Data Sources
- **Primary**: AWS AppConfig (with genre support)
- **Fallback**: Static movie array with predefined genres
- **Graceful Degradation**: Automatic fallback on AppConfig failures

## 🧪 Testing

The feature includes comprehensive unit tests:
- Search functionality with various parameter combinations
- Input validation and error handling
- Movie model enhancements
- Utility method validation
- Static data integrity checks

Run tests with:
```bash
mvn test
```

## 🎯 Usage Examples

### Web Interface
1. Navigate to `/movies/search` in your browser
2. Use the search form to enter criteria
3. Click "Search Movies" to find matching results
4. Click "View All Movies" to see the complete list

### API Integration
```bash
# Search for action movies
curl "http://localhost:8080/movies/search?genre=Action"

# Search for movies with "pirate" in the name
curl "http://localhost:8080/movies/search?name=pirate"

# Get movie with specific ID
curl "http://localhost:8080/movies/search?id=5"

# Combined search
curl "http://localhost:8080/movies/search?name=adventure&genre=Fantasy"
```

## 🔧 Configuration

The search feature uses the same AppConfig settings as the existing movie service:
- `appconfig.application`: Your AppConfig application name
- `appconfig.environment`: Your AppConfig environment
- `appconfig.config`: Your AppConfig configuration profile
- `appconfig.cacheTtlInSeconds`: Cache TTL for AppConfig data

## 📊 Expected AppConfig JSON Format

For full functionality, your AppConfig should include genre information:

```json
{
  "movies": [
    {
      "id": 1,
      "movieName": "The Great Adventure",
      "genre": "Adventure"
    },
    {
      "id": 2,
      "movieName": "Comedy Gold",
      "genre": "Comedy"
    }
  ]
}
```

**Note**: The `genre` field is optional. Movies without genre will default to "Unknown".

## 🎉 Benefits

- **Enhanced User Experience**: Interactive search form with immediate feedback
- **Flexible Filtering**: Multiple search criteria can be combined
- **Robust Error Handling**: Graceful degradation and user-friendly messages
- **Backward Compatibility**: Existing endpoints and functionality preserved
- **Performance Optimized**: Efficient filtering using Java Streams
- **Comprehensive Testing**: Full test coverage for reliability

Arrr! Now ye can search for movie treasures like a true master of the seas! 🏴‍☠️⚓