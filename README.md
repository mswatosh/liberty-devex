# liberty-devex

Eclipse plugin that adds a context menu item to copy Gradle commands for OpenLiberty FAT test projects.

Predominantly vibe-coded with IBM Bob

## Features

- Right-click on a FAT test project in the Package Explorer
- Select "Copy BuildAndRun" from the context menu
- The Gradle command (e.g., `./gradlew com.ibm.ws.concurrent_fat_jakarta_11:buildandrun`) is copied to your clipboard
- Menu item only appears for FAT test projects (projects that have a `bnd.bnd` file in the root with the line `fat.project: true`)

## Testing the Plugin in Eclipse

### Prerequisites
- Eclipse IDE with Plugin Development Environment (PDE) installed
- Java 8 or higher

### Steps to Test

1. **Import the Plugin Project**
   - Open Eclipse IDE
   - Go to `File` → `Import` → `Existing Projects into Workspace`
   - Browse to this project directory and import it

2. **Launch Runtime Eclipse Instance**
   - Right-click on the project in Package Explorer
   - Select `Run As` → `Eclipse Application`
   - This will launch a new Eclipse instance with your plugin installed

3. **Import Test Projects**
   - In the runtime Eclipse instance, import the test workspace:
     - `File` → `Import` → `Existing Projects into Workspace`
     - Browse to `test-workspace` directory in this project
     - Select the projects (e.g., `testProject`, `fattest.simplicity`)
     - Click `Finish`

4. **Test the Plugin**
   - In the Package Explorer, right-click on `testProject`
   - You should see "Copy BuildAndRun" in the context menu
   - Click it to copy the Gradle command to clipboard
   - Paste in a text editor to verify: `./gradlew testProject:buildandrun`
   - Check the Eclipse Console for debug output

### Alternative: Export and Install

1. **Export the Plugin**
   - Right-click on the project
   - Select `Export` → `Plug-in Development` → `Deployable plug-ins and fragments`
   - Choose destination directory
   - Click `Finish`

2. **Install in Your Eclipse**
   - Copy the generated JAR from the `plugins` folder to your Eclipse installation's `dropins` folder
   - Restart Eclipse
   - The plugin will be available in your main Eclipse instance

## Development

### Project Structure
- `src/io/openliberty/devex/CopyForTestAction.java` - Main handler class
- `plugin.xml` - Plugin configuration (commands, menus, handlers)
- `META-INF/MANIFEST.MF` - Bundle manifest with dependencies
- `test-workspace/` - Sample test projects for testing

### Key Dependencies
- `org.eclipse.jdt.core` - Java project model
- `org.eclipse.jdt.ui` - Java UI components
- `org.eclipse.ui` - Eclipse UI framework
- `org.eclipse.swt` - Clipboard functionality

## Troubleshooting

- **Menu item doesn't appear**: Ensure you're right-clicking on a Java project
- **Nothing copied**: Check Eclipse Console for error messages
- **Plugin not loading**: Verify all dependencies are available in your Eclipse installation