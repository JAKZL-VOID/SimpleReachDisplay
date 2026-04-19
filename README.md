# SimpleReachDisplay
**by JAKZL**

A Fabric mod for Minecraft 1.21.1 that displays your reach distance when you hit an entity, with a fully customizable HUD and Zoomify-style config screen.

---

## 🎮 Features
- Shows reach distance only when you land a hit on an entity
- Color coded: 🟢 Green (2.75–3.0), 🟠 Orange (2.0–2.75), 🔴 Red (below 2.0)
- Fades out after a configurable duration
- Filter by target type: any entity, players only, hostile mobs, etc.
- Full Mod Menu config screen with live preview
- Draggable HUD position

---

## 🏗️ How to Build (No Software Needed)

### Step 1 — Create a GitHub Account
1. Go to [github.com](https://github.com) and click **Sign up**
2. Follow the steps to create a free account
3. Verify your email

### Step 2 — Create a New Repository
1. Click the **+** button in the top right → **New repository**
2. Name it `SimpleReachDisplay`
3. Set it to **Public**
4. Do **NOT** check "Add a README" (we already have one)
5. Click **Create repository**

### Step 3 — Upload the Files
1. On your new empty repository page, click **uploading an existing file**
2. Extract the zip I gave you
3. Drag **all the files and folders** into the GitHub upload window
   - Make sure you upload the folder structure, not just the files
   - You should see: `src/`, `.github/`, `build.gradle`, `gradle.properties`, `settings.gradle`, `gradlew`, `gradle/`, `README.md`
4. Scroll down, click **Commit changes**

### Step 4 — Wait for the Build
1. Click the **Actions** tab at the top of your repository
2. You'll see a workflow called **"Build Mod"** running (yellow circle = running, green ✓ = done)
3. It takes about 2–4 minutes
4. Click on the workflow run → scroll down to **Artifacts**
5. Click **SimpleReachDisplay** to download a zip
6. Extract that zip — inside is your `.jar` file!

### Step 5 — Install Required Mods
Download these from [modrinth.com](https://modrinth.com) (all free):
- **Fabric API** — search "Fabric API", pick version for 1.21.1
- **Cloth Config API** — search "Cloth Config", pick Fabric 1.21.1
- **Mod Menu** — search "Mod Menu", pick 1.21.1

### Step 6 — Add to Lunar Client
1. Open **Lunar Client Launcher**
2. Make sure you have a **Fabric 1.21.1** profile set up
3. Find your Lunar mods folder:
   - Windows: `C:\Users\YourName\.lunarclient\mods\fabric\1.21.1\`
   - Mac: `~/.lunarclient/mods/fabric/1.21.1/`
4. Drop these `.jar` files into that folder:
   - `simplereachdisplay-1.0.0.jar` (the one you just built)
   - `fabric-api-*.jar`
   - `cloth-config-fabric-*.jar`
   - `modmenu-*.jar`
5. Launch Minecraft with your Fabric 1.21.1 profile
6. Done! Hit an entity to see your reach displayed.

---

## ⚙️ Configuration
In-game, press **Escape** → **Mods** → find **SimpleReachDisplay** → click the config button.

| Setting | Description |
|---|---|
| Enabled | Toggle the mod on/off |
| Target Filter | Which entities trigger the display |
| Display Duration | How long the text stays (0.5–5.0s) |
| Fade Out | Smooth fade vs instant disappear |
| Text Size | Small / Medium / Large |
| Bold / Italic | Text style |
| Background Box | Adds a dark box behind the text |
| Dynamic Color | Use green/orange/red based on distance |
| HUD Position | Drag to reposition in the Position tab |

---

## 📤 Uploading to Modrinth
Once you have your `.jar`:
1. Go to [modrinth.com](https://modrinth.com) and create an account
2. Click your avatar → **Create a project**
3. Fill in the details, upload your `.jar`
4. Set game version to `1.21.1`, loader to `Fabric`
5. Publish!

---

## 📋 Dependencies
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config API](https://modrinth.com/mod/cloth-config)
- [Mod Menu](https://modrinth.com/mod/modmenu)
