# AquaCore-Food

AquaCore-Food is a custom Minecraft plugin designed to enhance food and saturation mechanics on your server.

## Features

- **Custom Food Mechanics**: Tailor how food and saturation work to fit your server's gameplay.
- **Configurable**: extensive configuration options via `config.yml`.
- **PlaceholderAPI Support**: Integrated with PlaceholderAPI for dynamic displays.

## Installation

1.  Download the latest release of `AquaCore-Food`.
2.  Place the `.jar` file into your server's `plugins` folder.
3.  Restart your server.
| `aquafood.admin` | Grants access to administrative commands (`set`, `add`, `reload`). | Admins / Ops |

## Placeholders

| Placeholder | Description |
| :--- | :--- |
| `%aquafood_carbs%` | Shows current carbohydrates level. |
| `%aquafood_prot%` | Shows current proteins level. |
| `%aquafood_vit%` | Shows current vitamins level. |
| `%aquafood_average%` | Shows the average of all three stats. |

## Configuration

The plugin generates a `config.yml` file where you can tweak various settings. Make sure to check it out to fully customize the plugin to your needs.

## Building from Source

To build this project, you need JDK 21 installed.

1.  Clone the repository.
2.  Run the build command:
    *   **Windows**: `gradlew build`
    *   **Linux/macOS**: `./gradlew build`
3.  The compiled jar will be located in `build/libs`.
