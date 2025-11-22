# Frontend Modifications Documentation

**Author**: uchechisebastian20@gmail.com  
**Branch**: ucFrontendModification  
**Date**: 2025

## Overview

This document describes all the frontend modifications and enhancements made to the Goalie application. The changes include a complete redesign of the login/signup pages, home page improvements with mode switching functionality, player profile setup, and various UI/UX enhancements.

---

## Table of Contents

1. [Login and Signup Pages Redesign](#1-login-and-signup-pages-redesign)
2. [Home Page Redesign with Mode Switcher](#2-home-page-redesign-with-mode-switcher)
3. [Player Profile Setup Modal](#3-player-profile-setup-modal)
4. [Header/Navigation Improvements](#4-headernavigation-improvements)
5. [Code Changes by File](#5-code-changes-by-file)

---

## 1. Login and Signup Pages Redesign

### Purpose
Modernized the authentication pages with a sophisticated split-screen design, improved user experience, and better visual appeal.

### Key Features
- **Split-screen layout**: Left side with branding/features, right side with form
- **Gradient backgrounds**: Using app's color scheme (--primary, --secondary, --accent)
- **Animated splash section**: Subtle pulse effects and feature cards
- **Enhanced form styling**: Modern inputs with focus states and smooth transitions
- **Error handling**: Styled error messages with proper validation
- **Success messages**: Visual feedback after successful signup

### Files Modified
- `src/main/resources/templates/login.html`
- `src/main/resources/templates/signup.html`
- `src/main/resources/static/css/main.css`
- `src/main/java/com/example/goalie/controller/HomeController.java`

### Code Explanation

#### Signup Form (`signup.html`)
```html
<!-- Split-screen layout with splash content and form -->
<div class="formParent">
    <div class="splashImg">
        <!-- Feature highlights on left side -->
    </div>
    <div class="formSection">
        <!-- Form on right side with validation -->
    </div>
</div>
```

**What it does**: Creates a visually appealing two-column layout where users see platform benefits on the left while filling out the form on the right.

#### Login Form (`login.html`)
Similar structure to signup but with login-specific messaging and features.

#### CSS Styling (`main.css`)
```css
.formParent {
    display: flex;
    min-height: 100vh;
    background: linear-gradient(135deg, var(--background) 0%, var(--primary) 100%);
}
```

**What it does**: 
- Creates a full-height flex container
- Applies gradient background using CSS variables
- Ensures responsive design across devices

#### Controller Updates (`HomeController.java`)
```java
@PostMapping("/signup")
public String signupSubmit(@ModelAttribute("user") User user, BindingResult br, Model model){
    // Validate required fields
    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        br.rejectValue("email", "error.user", "Email is required");
    }
    // ... more validation
}
```

**What it does**:
- Validates all required fields (email, username, password)
- Checks for duplicate emails
- Provides user-friendly error messages
- Redirects to login with success message after signup

---

## 2. Home Page Redesign with Mode Switcher

### Purpose
Created a sophisticated home page dashboard with the ability to switch between "User Mode" (organizer) and "Player Mode" (participant), each showing relevant action cards.

### Key Features
- **Mode Switcher**: Toggle between User and Player modes
- **Dynamic Content**: Action cards change based on selected mode
- **Stats Dashboard**: Visual statistics cards
- **Session Persistence**: Mode preference saved in session
- **Consistent Card Design**: All cards use uniform styling

### Files Modified
- `src/main/resources/templates/home.html`
- `src/main/resources/static/css/main.css`
- `src/main/java/com/example/goalie/controller/HomeController.java`

### Code Explanation

#### Mode Switcher (`home.html`)
```html
<div class="mode-switcher">
    <form method="post" th:action="@{/switch-mode}" class="mode-form">
        <div class="mode-toggle">
            <button type="submit" name="mode" value="user" 
                    th:class="${currentMode == 'user' ? 'mode-btn active' : 'mode-btn'}">
                <span class="mode-icon">👤</span>
                <span class="mode-label">User Mode</span>
            </button>
            <!-- Player mode button -->
        </div>
    </form>
</div>
```

**What it does**:
- Displays two buttons for mode selection
- Highlights the active mode with different styling
- Submits form to switch modes via POST request
- Uses Thymeleaf to conditionally apply CSS classes

#### Controller Logic (`HomeController.java`)
```java
@GetMapping("/home")
public String home(HttpSession session, Model model) {
    User user = (User) session.getAttribute("loggedInUser");
    if(user == null) {
        return "redirect:/login";
    }
    
    // Get current mode from session, default to "user"
    String currentMode = (String) session.getAttribute("userMode");
    if (currentMode == null) {
        currentMode = "user";
        session.setAttribute("userMode", currentMode);
    }
    
    model.addAttribute("currentMode", currentMode);
    return "home";
}

@PostMapping("/switch-mode")
public String switchMode(@RequestParam String mode, HttpSession session) {
    if ("user".equals(mode) || "player".equals(mode)) {
        session.setAttribute("userMode", mode);
    }
    return "redirect:/home";
}
```

**What it does**:
- Retrieves current mode from session (defaults to "user")
- Stores mode preference in session for persistence
- Handles mode switching via POST request
- Validates mode value before storing

#### Action Cards (`home.html`)
```html
<div class="action-section" th:if="${currentMode == 'user'}">
    <h2 class="section-title">Organizer Dashboard</h2>
    <div class="action-grid">
        <a th:href="@{/tournaments/create}" class="action-card create-tournament">
            <!-- Card content -->
        </a>
    </div>
</div>
```

**What it does**:
- Conditionally displays cards based on current mode
- User Mode shows: Create Tournament, View Tournaments, Create Team, Analytics
- Player Mode shows: Join Tournament, Create Tournament, Create Team, My Teams, Leaderboard
- Uses Thymeleaf `th:if` for conditional rendering

#### CSS Styling (`main.css`)
```css
.action-card {
    background: linear-gradient(135deg, rgba(168, 145, 216, 0.1) 0%, rgba(107, 121, 43, 0.1) 100%);
    border: 1px solid rgba(168, 145, 216, 0.3);
    border-radius: 20px;
    padding: 2.5rem;
    transition: all 0.3s ease;
}

.action-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 15px 40px rgba(150, 234, 155, 0.3);
    border-color: var(--accent);
}
```

**What it does**:
- Creates visually appealing cards with gradient backgrounds
- Adds smooth hover animations (lift effect)
- Uses consistent styling across all cards
- Implements backdrop blur for modern glass-morphism effect

---

## 3. Player Profile Setup Modal

### Purpose
When a user switches to Player Mode for the first time, a modal popup appears to collect essential player information for their profile.

### Key Features
- **Automatic Display**: Shows only on first-time player mode selection
- **5-Star Skill Rating**: Interactive star rating system
- **Player Information**: Nickname, position, dominant foot, bio
- **Form Validation**: Required fields with proper error handling
- **Animated Modal**: Smooth entrance/exit animations

### Files Modified
- `src/main/resources/templates/home.html`
- `src/main/resources/static/css/main.css`
- `src/main/java/com/example/goalie/controller/HomeController.java`
- `src/main/java/com/example/goalie/config/AppService.java`
- `src/main/java/com/example/goalie/model/UserProfile.java`

### Code Explanation

#### Modal HTML (`home.html`)
```html
<div th:if="${showPlayerSetup}" id="playerSetupModal" class="modal-overlay">
    <div class="modal-container">
        <form id="playerProfileForm" method="post" th:action="@{/setup-player-profile}">
            <input type="text" id="playerNickname" name="playerNickname" required>
            <!-- Star rating system -->
            <div class="star-rating">
                <input type="radio" id="star5" name="skillRating" value="5">
                <label for="star5" class="star">★</label>
                <!-- More stars -->
            </div>
        </form>
    </div>
</div>
```

**What it does**:
- Creates a modal overlay that covers the entire screen
- Contains a form to collect player information
- Uses Thymeleaf conditional rendering (`th:if`) to show only when needed
- Implements a custom star rating system using radio buttons and labels

#### Star Rating JavaScript (`home.html`)
```javascript
const stars = document.querySelectorAll('.star-rating input[type="radio"]');
const ratingLabels = {
    1: 'Beginner',
    2: 'Novice',
    3: 'Intermediate',
    4: 'Advanced',
    5: 'Expert'
};

stars.forEach(star => {
    star.addEventListener('change', function() {
        const value = parseInt(this.value);
        ratingText.textContent = ratingLabels[value];
    });
});
```

**What it does**:
- Listens for star selection changes
- Updates the rating text dynamically (Beginner, Novice, etc.)
- Provides visual feedback to users

#### Controller Logic (`HomeController.java`)
```java
@GetMapping("/home")
public String home(HttpSession session, Model model) {
    // ... existing code ...
    
    // Check if player profile exists
    boolean hasPlayerProfile = service.hasPlayerProfile(user);
    boolean showPlayerSetup = false;
    
    if ("player".equals(currentMode) && !hasPlayerProfile) {
        showPlayerSetup = true;
    }
    
    model.addAttribute("showPlayerSetup", showPlayerSetup);
    return "home";
}

@PostMapping("/setup-player-profile")
public String setupPlayerProfile(@RequestParam String playerNickname,
                                 @RequestParam Integer skillRating,
                                 @RequestParam(required = false) String preferredPosition,
                                 @RequestParam(required = false) String dominantFoot,
                                 @RequestParam(required = false) String bio,
                                 HttpSession session) {
    User user = (User) session.getAttribute("loggedInUser");
    service.createOrUpdatePlayerProfile(user, playerNickname, skillRating, 
                                       preferredPosition, dominantFoot, bio);
    return "redirect:/home";
}
```

**What it does**:
- Checks if user has a player profile when in player mode
- Shows modal only if profile doesn't exist and mode is "player"
- Handles form submission to save player profile
- Maps skill rating (1-5) to SkillLevel enum (BEGINNER, INTERMEDIATE, ADVANCED)

#### Service Layer (`AppService.java`)
```java
public boolean hasPlayerProfile(User user) {
    return userProfileRepository.findAll().stream()
            .anyMatch(profile -> profile.getUser() != null && 
                     profile.getUser().getId().equals(user.getId()));
}

public UserProfile createOrUpdatePlayerProfile(User user, String playerNickname, 
                                               Integer skillRating, ...) {
    UserProfile profile = getPlayerProfile(user);
    
    if (profile == null) {
        profile = new UserProfile();
        profile.setUser(user);
    }
    
    profile.setPlayerNickname(playerNickname);
    profile.setSkillRating(skillRating);
    
    // Map skill rating to SkillLevel enum
    if (skillRating <= 2) {
        profile.setSkillLevel(SkillLevel.BEGINNER);
    } else if (skillRating <= 4) {
        profile.setSkillLevel(SkillLevel.INTERMEDIATE);
    } else {
        profile.setSkillLevel(SkillLevel.ADVANCED);
    }
    
    return userProfileRepository.save(profile);
}
```

**What it does**:
- Checks if a player profile exists for the user
- Creates new profile or updates existing one
- Automatically maps numeric skill rating (1-5) to enum values
- Saves profile to database

#### Model Updates (`UserProfile.java`)
```java
@OneToOne
@JoinColumn(name = "user_id")
private User user;

private String playerNickname;
private Integer skillRating; // 1-5 star rating
private SkillLevel skillLevel;
// ... other fields
```

**What it does**:
- Establishes one-to-one relationship between User and UserProfile
- Stores player nickname and numeric skill rating
- Maintains both numeric rating and enum skill level

#### CSS Styling (`main.css`)
```css
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(10px);
    z-index: 10000;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
}

.modal-overlay.show {
    opacity: 1;
    visibility: visible;
}

.star-rating {
    display: flex;
    flex-direction: row-reverse;
    justify-content: flex-end;
}

.star-rating label.star {
    font-size: 2.5rem;
    color: rgba(150, 234, 155, 0.3);
    cursor: pointer;
}

.star-rating input[type="radio"]:checked ~ label.star {
    color: var(--accent);
}
```

**What it does**:
- Creates a full-screen overlay with blur effect
- Implements smooth fade-in animation
- Styles star rating with hover and selection states
- Uses CSS sibling selectors for star highlighting

---

## 4. Header/Navigation Improvements

### Purpose
Enhanced the navigation header with modern styling, better hover effects, and improved user experience.

### Key Features
- **Gradient Background**: Modern gradient with backdrop blur
- **Sticky Navigation**: Header stays at top when scrolling
- **Enhanced Logo**: Hover effects with rotation
- **Improved Links**: Underline animations on hover
- **Profile Section**: Better styling with hover effects

### Files Modified
- `src/main/resources/templates/layout.html`
- `src/main/resources/templates/home.html`
- `src/main/resources/static/css/main.css`

### Code Explanation

#### Header HTML (`layout.html`)
```html
<header layout:fragment="header">
    <section class="main_section">
        <nav>
            <div class="navlogo">
                <div>
                    <img th:src="@{/images/logo.jpg}" alt="Goalie Logo">
                </div>
                <a th:href="@{/home}">Goalie</a>
            </div>
            <div class="nav_list">
                <a th:href="@{/tournaments}">Tournaments</a>
                <!-- More links -->
            </div>
        </nav>
    </section>
</header>
```

**What it does**:
- Creates a structured navigation header
- Logo links back to home page
- Navigation links for main sections

#### CSS Styling (`main.css`)
```css
header {
    position: sticky;
    top: 0;
    z-index: 1000;
    background: linear-gradient(135deg, rgba(10, 6, 16, 0.95) 0%, rgba(28, 13, 64, 0.95) 100%);
    backdrop-filter: blur(20px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.nav_list > a:not(.profile-pic)::before {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%) scaleX(0);
    width: 80%;
    height: 2px;
    background: linear-gradient(90deg, var(--accent) 0%, var(--secondary) 100%);
    transition: transform 0.3s ease;
}

.nav_list > a:not(.profile-pic):hover::before {
    transform: translateX(-50%) scaleX(1);
}
```

**What it does**:
- Makes header sticky (stays at top when scrolling)
- Applies gradient background with transparency
- Adds backdrop blur for modern glass effect
- Creates animated underline on link hover using pseudo-elements
- Uses CSS transforms for smooth animations

---

## 5. Code Changes by File

### 5.1 `HomeController.java`

#### Changes Made:
1. **Enhanced Signup Validation**
   - Added null checks for all required fields
   - Improved error handling with try-catch
   - Added success message redirect

2. **Enhanced Login Validation**
   - Added null checks for email and password
   - Improved password comparison with null safety
   - Better error messages

3. **Mode Switching**
   - Added `currentMode` to model
   - Created `/switch-mode` endpoint
   - Session-based mode persistence

4. **Player Profile Setup**
   - Added `showPlayerSetup` flag
   - Created `/setup-player-profile` endpoint
   - Integration with AppService

#### Key Methods:
```java
// Validates and creates user account
@PostMapping("/signup")
public String signupSubmit(...)

// Handles user login
@PostMapping("/login")
public String loginSubmit(...)

// Displays home page with mode awareness
@GetMapping("/home")
public String home(...)

// Switches between user and player modes
@PostMapping("/switch-mode")
public String switchMode(...)

// Saves player profile information
@PostMapping("/setup-player-profile")
public String setupPlayerProfile(...)
```

### 5.2 `AppService.java`

#### Changes Made:
1. **Player Profile Management**
   - Added `hasPlayerProfile()` method
   - Added `getPlayerProfile()` method
   - Added `createOrUpdatePlayerProfile()` method

2. **Skill Rating Mapping**
   - Automatically converts 1-5 star rating to SkillLevel enum
   - 1-2 stars = BEGINNER
   - 3-4 stars = INTERMEDIATE
   - 5 stars = ADVANCED

#### Key Methods:
```java
// Checks if user has a player profile
public boolean hasPlayerProfile(User user)

// Retrieves existing player profile
public UserProfile getPlayerProfile(User user)

// Creates or updates player profile with all information
public UserProfile createOrUpdatePlayerProfile(User user, String playerNickname, 
                                               Integer skillRating, ...)
```

### 5.3 `UserProfile.java`

#### Changes Made:
1. **Added User Relationship**
   - One-to-one relationship with User entity
   - Foreign key: `user_id`

2. **New Fields**
   - `playerNickname`: String for player's display name
   - `skillRating`: Integer (1-5) for star rating
   - `bio`: String for player description

#### Code:
```java
@OneToOne
@JoinColumn(name = "user_id")
private User user;

private String playerNickname;
private Integer skillRating; // 1-5 star rating
private SkillLevel skillLevel;
private String bio;
```

### 5.4 `main.css`

#### Major Additions:
1. **Form Styling** (Login/Signup)
   - Split-screen layout
   - Gradient backgrounds
   - Feature cards
   - Input focus states

2. **Home Page Styling**
   - Mode switcher design
   - Action cards grid
   - Stats dashboard
   - Hover effects

3. **Modal Styling**
   - Overlay with blur
   - Star rating system
   - Form inputs
   - Animations

4. **Header Styling**
   - Sticky positioning
   - Gradient background
   - Link animations
   - Profile section

#### Key CSS Classes:
- `.formParent`: Container for login/signup forms
- `.mode-switcher`: Mode toggle buttons
- `.action-card`: Dashboard action cards
- `.modal-overlay`: Player setup modal
- `.star-rating`: 5-star rating system

### 5.5 Template Files

#### `login.html` & `signup.html`
- Complete redesign with modern layout
- Feature highlights
- Improved form structure
- Success/error message display

#### `home.html`
- Mode switcher component
- Conditional card display
- Stats dashboard
- Player setup modal
- Quick actions section

#### `layout.html`
- Enhanced header structure
- Improved navigation links
- Better logo presentation

---

## Technical Details

### Technologies Used
- **Spring Boot**: Backend framework
- **Thymeleaf**: Template engine
- **Java**: Programming language
- **CSS3**: Styling with modern features (gradients, animations, backdrop-filter)
- **JavaScript**: Client-side interactions (star rating, modal)

### Design Patterns
- **MVC Pattern**: Model-View-Controller architecture
- **Session Management**: Storing user preferences
- **Conditional Rendering**: Thymeleaf conditionals for dynamic content
- **Form Validation**: Server-side and client-side validation

### CSS Variables Used
```css
--text: #e9e3f5
--background: #0a0610
--primary: #1c0d40
--secondary: #6b792b
--accent: #96ea9b
```

### Database Changes
- **UserProfile Table**: 
  - Added `user_id` foreign key
  - Added `playerNickname` column
  - Added `skillRating` column (Integer)
  - Added `bio` column

---

## Testing Recommendations

1. **Login/Signup Flow**
   - Test form validation
   - Test error messages
   - Test success redirects

2. **Mode Switching**
   - Test switching between modes
   - Verify session persistence
   - Check card visibility changes

3. **Player Profile Setup**
   - Test modal appearance (first time only)
   - Test star rating interaction
   - Test form submission
   - Verify profile creation in database

4. **Responsive Design**
   - Test on mobile devices
   - Test on tablets
   - Test on different screen sizes

---

## Future Enhancements

1. **Player Profile**
   - Add profile picture upload
   - Add more detailed player statistics
   - Add player history/achievements

2. **Mode Features**
   - Add more mode-specific features
   - Add mode-specific statistics
   - Add mode switching preferences

3. **UI/UX**
   - Add loading states
   - Add more animations
   - Improve accessibility

---

## Conclusion

These modifications significantly enhance the user experience of the Goalie application by:
- Providing modern, visually appealing authentication pages
- Enabling flexible mode switching for different user roles
- Collecting essential player information for better platform engagement
- Improving overall navigation and user interface consistency

All changes maintain backward compatibility and follow Spring Boot best practices.

---

**End of Documentation**

