📄 **Flower Recognition App (Java + XML + Firebase)**

📌 **Overview**

This project is a native Android Application that uses image recognition technology to classify and identify different flower species.
It includes:
🔐 User authentication (Login/Signup)

📸 Camera & Gallery integration

🌿 Detailed botanical profiles and educational data

📊 User dashboard with scan history

🧠 **Tech Stack**

* **Frontend:** XML (Native Android UI)
* **Backend:** Java (JDK)
* **Database:** Firebase (Authentication & Database)
* **Environment:** Android Studio

🚀 **Features**

✅ Detect and identify flowers from uploaded images

✅ User Authentication (Signup/Login/Logout)

✅ Personalized user dashboard

✅ Scan Tracking to review previously identified flowers

✅ Botanical encyclopedia with scientific names and habitats

✅ Clean Material UI design


⚙️ **Setup Instructions**

💻 **Local Development Setup**

```bash
# Clone the repository
git clone https://github.com/areebsmarty19/Flower_Recog.git

# Open the project
1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned Flower_Recog directory

# Sync dependencies
Allow Gradle to sync completely (requires active internet connection)

# Build the app
1. Double-tap the 'Shift' key
2. Type "Build APK" and press Enter
3. Locate your generated app-debug.apk

```

📦 **Live Demo & App Download**

⚠️ **No Installation Required (Web Emulator)**

Due to convenience, you can test the fully functional app directly in your web browser without an Android device.
👉 [Test Live on Appetize.io] : https://appetize.io/app/b_5aalnzmkd4d2hszpyefyxqe6j4?device=pixel7&deviceControls=false&osVersion=13.0

🔽 **Download for Android**

Download the installable APK to test on a physical device:
👉 *[Insert link to your GitHub Release app-debug.apk here]*

📁 **Placement Instructions (For Developers)**

After setting up the project:
Ensure you have your Firebase configuration file named:
`google-services.json`

Place it inside the `app` folder (the folder where your `src` directory is located)

📂 **Example Project Structure:**

```text
Flower_Recog/
│
├── app/
│   ├── src/
│   ├── build.gradle
│   └── google-services.json   ← place here ✅
│
├── gradle/
└── build.gradle

```

⚠️ **Important Notes**

* Make sure the `google-services.json` file is strictly inside the `app/` folder.
* The app will fail to authenticate or build properly if this Firebase file is missing.
* An active internet connection is required on the device/emulator to log in and fetch flower data.

📡 **App Flow & Screens**

🔹 **Authentication**

* **Signup Screen** → Register a new user account
* **Login Screen** → Authenticate existing user

🔹 **Core Functions**

* **Dashboard** → View welcome screen and navigate features
* **Scanner** → Upload or snap a picture for identification
* **Encyclopedia** → View results (scientific name, aliases, native habitat)
* **History** → View previously scanned items

🧪 **How to Test**

1. Signup/Login to the app
2. Tap to start scanning
3. Upload an image from the gallery or take a new photo
4. Wait for the analysis to complete
5. View the detailed prediction result and plant information

📸 **Screenshots**

| Welcome & Landing | Authentication UI | User Dashboard |
| --- | --- | --- |
| ![image](https://github.com/user-attachments/assets/232cac21-1248-46f1-b75c-3f373597290c) | ![image](https://github.com/user-attachments/assets/ee8f2da2-e32b-4963-865e-f3dca59a61c3) | ![image](https://github.com/user-attachments/assets/6c15d219-e04f-41d5-a61b-a62bbac2a703) |

| Profile Management | Identification Results |
| --- | --- |
| ![image](https://github.com/user-attachments/assets/bcc5544f-133e-4a7e-8561-fd9beb5c65ef) | ![image](https://github.com/user-attachments/assets/d14e9fbc-6be0-48aa-a901-6373ba954b8d) |


🚀 **Future Improvements**

🔹 Implement an offline machine learning model

🔹 Expand the flower database coverage

🔹 Add community/social sharing features

🔹 Add plant care instructions to the encyclopedia


👩‍💻 **Author**

Developed as a Native Android Application
Suitable for college project / hackathon / portfolio

⭐ **If you like this project**

Give it a ⭐ on GitHub!
