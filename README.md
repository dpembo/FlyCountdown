# FlyCountdown 1.2.2
**Simple, smooth, and powerful flight control for your server.**  
**Made by _drafterplus_**

---

## What is FlyCountdown?
FlyCountdown lets you give players **temporary flight time** (e.g., 30 minutes or 1 hour).  
It’s perfect for **Survival**, **RPG**, or **Skyblock** servers where flight should be a **reward**, not a permanent perk.

Version **1.2** has been completely rebuilt to:

- Look better  
- Run faster  
- Be easier to use  

---

## Dependencies
- **Vault** — Required for the Shop feature  
- **PlaceholderAPI** — Optional, but supported  

---

## Features
- **Smooth & Simple** — Built for modern Minecraft versions (1.20+) with no lag  
- **Visual Timer** — Clean digital timer in the action bar  
- **Flight Shop** — Players can buy flight time using in‑game money  
- **Easy Settings** — Toggle sounds, particles, and features using a GUI  
- **Customizable** — Full support for HEX colors and gradients  

---

## Flight Shop GUI
*(Image placeholder)*

---

## Settings GUI
*(Image placeholder)*

---

## Commands
| Command | Description |
|--------|-------------|
| `/fly` | Toggle your own flight |
| `/fly shop` | Open the flight time purchase GUI |
| `/fly settings` | Open the GUI to toggle plugin features |
| `/fly check [player]` | Check remaining flight time |
| `/fly add <player> <time>` | Add flight time (e.g., `10m`, `1h`) |
| `/fly take <player> <time>` | Remove flight time |
| `/fly toggle <player>` | Toggle flight for another player |
| `/fly blockworld <world>` | Block/Unblock flight in a specific world |
| `/fly reload` | Reload the configuration file |

---

## Permissions
| Permission | Description | Default |
|-----------|-------------|---------|
| `FlyCountdown.fly` | Allow using `/fly` | Everyone |
| `FlyCountdown.fly.inf` | Unlimited flight time & “Inf” in action bar | OP |
| `FlyCountdown.admin` | Access to settings, debug, and adding/taking time | OP |
| `FlyCountdown.check` | Allow checking own/others’ time | Everyone |
| `FlyCountdown.toggle` | Allow toggling others’ flight | OP |
| `FlyCountdown.reload` | Allow reloading the config | OP |

---

## PlaceholderAPI Placeholders
| Placeholder | Description |
|------------|-------------|
| `%flycountdown_time%` | Formatted time (e.g., `5m 30s` or `∞`) |
| `%flycountdown_time_digital%` | Digital clock format (e.g., `05:30` or `∞`) |
| `%flycountdown_time_seconds%` | Raw seconds remaining |
| `%flycountdown_percent%` | Percentage of time remaining (0–100) |
| `%flycountdown_enabled%` | Returns `true` or `false` |
| `%flycountdown_is_flying%` | Returns `true` if the player is airborne |

---

## Internal Placeholders (config.yml)
| Placeholder | Description |
|------------|-------------|
| `%time%` | Automatically shows the relevant time |
| `%player%` | Shows the target player's name |
| `{world}` | Shows the world name |

