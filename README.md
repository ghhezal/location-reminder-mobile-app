# Location Reminder

An Android app that lets you create reminders tied to a physical location instead of a time. Pin a spot on the map, set a radius, and get notified the moment you walk into that zone, built for a Mobile Development course project (L3, Semester 6).

## Features

- **Location-based reminders:** attach a reminder to a place on the map instead of a clock time.
- **Geofencing with push notifications:** get notified automatically when entering a 100m radius around a saved location, even in the background.
- **Full reminder lifecycle:** create, view, edit, and delete reminders, each with a name, description, and map pin.
- **Map view of all locations:** see every saved reminder location plotted on a single map.
- **Authentication:** email/password login and sign up, with a password hint field for recovery.
- **Account settings:** change password and log out from a dedicated settings screen.
- **Onboarding walkthrough:** a 3-step intro (set boundary → smart notifications → fast task creation) for first-time users.
- **Local persistence:** reminders are stored on-device with Room/SQLite, no backend required.

## Screens

| Onboarding                                          | Auth                                                    | Home                                                   |
| --------------------------------------------------- | ------------------------------------------------------- | ------------------------------------------------------ |
| Welcome → 3-step walkthrough (skip or step through) | Login / Sign Up with email, password, and password hint | "My Reminders" list with name, description, and delete |

| Add / Edit Reminder                              | Location Map                                  | Settings                               |
| ------------------------------------------------ | --------------------------------------------- | -------------------------------------- |
| Name, description, and a map to pin the location | All saved reminder locations plotted together | Account info, change password, log out |

## How It Works

- **`WalkthroughActivity`:** first-run onboarding shown before login.
- **`Welcome`:** the launcher activity; entry point and routing to auth or home.
- **`Login` / `SignUp`:** email/password authentication.
- **`HomeActivity`:** main hub after login.
- **`MyRemindersActivity`:** lists all saved reminders.
- **`AddReminderActivity` / `EditReminderActivity`:** create or update a reminder, including picking its location on the map.
- **`SettingsActivity`:** account settings, password change, and logout.
- **`LocationService`:** a foreground service (type `location`) responsible for tracking location while a geofence is active.
- **`GeofenceBroadcastReceiver`:** receives geofence transition events (entering the 100m radius) and triggers the reminder notification; also listens for `BOOT_COMPLETED` so geofences can be re-registered after a device restart.

Reminders are persisted locally with Room/SQLite, so the app works fully offline aside from location services.

## Tech Stack

- **Language:** Java
- **Build system:** Gradle
- **Storage:** Room / SQLite (local)
- **Location:** Android Geofencing API, foreground location service
- **Notifications:** Android push notifications, triggered on geofence entry

## Permissions

The app requests the following at runtime/install:

| Permission                                           | Why                                                             |
| ---------------------------------------------------- | --------------------------------------------------------------- |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`    | Determine the user's current location for geofence triggers     |
| `ACCESS_BACKGROUND_LOCATION`                         | Detect geofence entry even when the app isn't in the foreground |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_LOCATION` | Keep location tracking alive via`LocationService`               |
| `POST_NOTIFICATIONS`                                 | Show the reminder notification on geofence entry                |
| `RECEIVE_BOOT_COMPLETED`                             | Re-register geofences after the device reboots                  |
| `WAKE_LOCK`                                          | Keep the device awake briefly to process geofence events        |
| `INTERNET` / `ACCESS_NETWORK_STATE`                  | Map tiles and connectivity checks                               |
| `WRITE_EXTERNAL_STORAGE`                             | Legacy storage access (older Android versions)                  |

## Getting Started

### Prerequisites

- Android Studio (Giraffe or later recommended)
- An Android device or emulator running API level 26+ (required for foreground services)
- A Google Maps API key (for map display and location picking)

### Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/ghhezal/location-reminder-mobile-app.git
   ```
2. Open the project in Android Studio.
3. Add your Google Maps API key (e.g. in `local.properties` or `AndroidManifest.xml`, depending on your setup).
4. Build and run on a device or emulator with location services enabled.

## Known Limitations

- Geofence radius is currently fixed at 100m (not user-configurable).
- Background location accuracy depends on the device's battery optimization settings, some OEMs (e.g. Xiaomi, Huawei) may need manual whitelisting for reliable geofence delivery.
- No password reset flow yet, "Forgot Password?" is present in the UI but it only provide a hint about the password in a toest message.

## Project Structure

```
.
├── app/                  # Main application module (activities, services, receivers)
├── gradle/                # Gradle wrapper files
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## Authors

**Amine Ghezal** and **Mounir Cherad**
