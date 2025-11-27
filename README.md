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
4.  Edit the `config.yml` file in the `plugins/AquaCore-Food` directory to customize settings.
5.  Restart the server or reload the plugin to apply changes.

## Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/aquafood help` | Shows the help menu. | `aquafood.use` |
| `/aquafood carbs` | Shows your current carbohydrates level. | `aquafood.use` |
| `/aquafood vit` | Shows your current vitamins level. | `aquafood.use` |
| `/aquafood prot` | Shows your current proteins level. | `aquafood.use` |
| `/aquafood set <stat> <val> <player>` | Sets a specific stat for a player. | `aquafood.admin` |
| `/aquafood add <stat> <val> <player>` | Adds (or subtracts) from a stat for a player. | `aquafood.admin` |
| `/aquafood reload` | Reloads the plugin configuration. | `aquafood.admin` |

## Permissions

| Permission | Description | Recommended Role |
| :--- | :--- | :--- |
| `aquafood.use` | Allows access to basic info commands (`help`, `carbs`, `vit`, `prot`). | Default / Players |
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
