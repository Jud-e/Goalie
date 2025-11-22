# Player Setup Modal - When It Appears

## When Does the Modal Appear?

The player setup modal appears automatically when **ALL** of the following conditions are met:

1. ✅ User is logged in
2. ✅ User switches to **Player Mode** (clicks the "Player Mode" button)
3. ✅ User does **NOT** have a player profile yet (first time selecting Player Mode)
4. ✅ Page redirects to `/home` after mode switch

## Step-by-Step to See the Modal:

1. **Login** to your account
2. Go to the **Home page** (`/home`)
3. Click the **"Player Mode"** button in the mode switcher (top of page)
4. The modal should appear **immediately** covering the entire screen

## Why It Might Not Appear:

### Reason 1: User Already Has a Profile
- If you've already filled out the player profile once, the modal won't appear again
- **Solution**: Check the database - if a `UserProfile` record exists for your user, delete it to test again

### Reason 2: Not in Player Mode
- The modal only appears when `currentMode == "player"`
- **Solution**: Make sure you clicked "Player Mode" and the button shows as active

### Reason 3: Session Issue
- The mode might not be saved in session
- **Solution**: Clear browser cookies/session and try again

## How to Test:

### First Time (Should Show Modal):
1. Login
2. Go to home page
3. Click "Player Mode" button
4. **Modal should appear** ✅

### Second Time (Should NOT Show Modal):
1. After completing the form once
2. Switch back to User Mode
3. Switch to Player Mode again
4. **Modal should NOT appear** (because profile exists) ✅

## Code Logic:

```java
// In HomeController.java
boolean hasPlayerProfile = service.hasPlayerProfile(user);
boolean showPlayerSetup = false;

if ("player".equals(currentMode) && !hasPlayerProfile) {
    showPlayerSetup = true;  // Modal will appear
}

model.addAttribute("showPlayerSetup", showPlayerSetup);
```

```html
<!-- In home.html -->
<div th:if="${showPlayerSetup}" id="playerSetupModal" class="modal-overlay">
    <!-- Modal content -->
</div>
```

## Debugging:

If the modal doesn't appear, check:

1. **Browser Console**: Look for "Player setup modal detected" message
2. **Page Source**: Search for "playerSetupModal" - if it's not in the HTML, `showPlayerSetup` is false
3. **Database**: Check if `userprofile` table has a record for your user
4. **Session**: Verify `userMode` is set to "player" in session

## Visual Indicators:

- **Modal Overlay**: Dark background covering entire screen
- **Modal Container**: Centered card with form
- **Z-index**: 10000 (should be on top of everything)
- **Backdrop**: Blurred background effect

## Fixed Issues:

✅ Modal now appears immediately when rendered (no hidden state)
✅ Removed initial transform animation that was hiding it
✅ Modal is visible by default when Thymeleaf renders it
✅ Z-index set to 10000 to ensure it's on top

