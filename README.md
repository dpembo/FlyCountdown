# FlyCountdown v2.0.0dp
**Modern, smooth, and powerful flight‑time control for Paper 1.20+.**  
A fully updated and rebuilt version of the original FlyCountdown plugin by *drafterplus*.

---

## ✨ What is FlyCountdown?
FlyCountdown lets you give players **temporary flight time** (e.g., 10 minutes, 30 minutes, 1 hour).  
It’s perfect for **Survival**, **RPG**, **Skyblock**, or any server where flight should be a **reward**, not a permanent perk.

Version **2.0.0dp** is a complete modernization of the original plugin, rebuilt to:

- Use **modern Kyori Adventure (4.17.0)**  
- Run cleanly on **Paper 1.20+**  
- Improve performance and stability  
- Remove legacy/decompiled dependencies  
- Provide a cleaner, more maintainable codebase  

This fork is licensed under **CC BY‑NC‑SA 4.0**, with attribution to the original author.

---

## 🚀 Features
- **Smooth & Simple** — No lag, no flicker, fully async-safe  
- **Visual Timer** — Clean digital countdown in the action bar  
- **Flight Shop** — Players can buy flight time using Vault economy  
- **Settings GUI** — Toggle sounds, particles, and features  
- **Customizable** — Full HEX color + gradient support via MiniMessage  
- **PlaceholderAPI Support** — Expose flight time to other plugins  
- **World Blocking** — Disable flight in specific worlds  
- **Admin Tools** — Add/remove/toggle flight for players  

---

## 📦 Dependencies
| Dependency | Required | Purpose |
|-----------|----------|---------|
| **Vault** | Yes | Economy support for the Flight Shop |
| **PlaceholderAPI** | No | External placeholders |
| **Paper 1.20+** | Yes | Modern Adventure + API stability |

---

## 🛒 Flight Shop GUI
Players can purchase flight time using in‑game currency.

![Shop GUI](images/shopgui.png)

---

## ⚙️ Settings GUI
Players can toggle plugin features such as:

- Action bar timer  
- Sounds  
- Particles  
- Notifications  

![Shop GUI](images/settingsgui.png)

---

## 🔧 Commands
| Command | Description |
|--------|-------------|
| `/fly` | Toggle your own flight |
| `/fly shop` | Open the flight purchase GUI |
| `/fly settings` | Open the settings GUI |
| `/fly check [player]` | Check remaining flight time |
| `/fly add <player> <time>` | Add flight time (e.g., `10m`, `1h`) |
| `/fly take <player> <time>` | Remove flight time |
| `/fly toggle <player>` | Toggle another player's flight |
| `/fly blockworld <world>` | Block/unblock flight in a world |
| `/fly reload` | Reload the configuration |

---

## 🔐 Permissions
| Permission | Description | Default |
|-----------|-------------|---------|
| `FlyCountdown.fly` | Use `/fly` | Everyone |
| `FlyCountdown.fly.inf` | Unlimited flight time | OP |
| `FlyCountdown.admin` | Admin features | OP |
| `FlyCountdown.check` | Check own/others’ time | Everyone |
| `FlyCountdown.toggle` | Toggle others’ flight | OP |
| `FlyCountdown.reload` | Reload config | OP |

---

## 📊 PlaceholderAPI Placeholders
| Placeholder | Description |
|------------|-------------|
| `%flycountdown_time%` | Formatted time (`5m 30s`, `∞`) |
| `%flycountdown_time_digital%` | Digital format (`05:30`, `∞`) |
| `%flycountdown_time_seconds%` | Raw seconds |
| `%flycountdown_percent%` | Percent remaining (0–100) |
| `%flycountdown_enabled%` | `true` / `false` |
| `%flycountdown_is_flying%` | `true` if airborne |

---

## 🧩 Internal Placeholders (config.yml)
| Placeholder | Description |
|------------|-------------|
| `%time%` | Auto‑formatted time |
| `%player%` | Player name |
| `{world}` | World name |

---

## 🛠️ Technical Notes (v2.0.0dp)
This modernized version includes:

- **Full migration to modern Kyori Adventure**  
  - `adventure-api 4.17.0`  
  - `adventure-text-minimessage 4.17.0`  
  - `adventure-platform-bukkit 4.3.2`  
- **Proper shading + relocation** of Adventure modules  
- Removal of all **decompiled Kyori source**  
- Cleaned and updated **MessageUtils**  
- Improved **ShopManager**, **FlightTimer**, and **GUI handlers**  
- Updated **POM.xml** for modern Paper builds  

This version is designed for **long-term maintainability** and **future Paper releases**.

---

## 📄 License
This project is licensed under:

### **Creative Commons Attribution–NonCommercial–ShareAlike 4.0 International (CC BY‑NC‑SA 4.0)**

- Original plugin by **drafterplus**  
- Modernization and updates by **Dave Pembo**  

You may:

- Share  
- Modify  
- Redistribute  

But **not** use commercially, and you must keep the same license.

Full license:  
https://creativecommons.org/licenses/by-nc-sa/4.0/

---

## ❤️ Credits
- **drafterplus** — Original FlyCountdown plugin  
- **Dave Pembo** — Modernization, rebuild, Adventure upgrade, and v2.0.0dp release  

---

## 📬 Support
If you need help with:

- Adventure migration  
- Shading issues  
- Plugin modernization  
- Flight system customization  

Feel free to open an issue or reach out.

