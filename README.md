QuestManager
============

Quest Manager (QM) is a plugin that creates, imports, and manages quests on a Bukkit server.  Quests vary from things as simple as 'deliver this item to this npc' to as comlicated as 'kill this horde of monsters, fight a boss, and then have one member of your party be at each of these locations all at once' and on. 

QM is designed to work completely from config, meaning no programming experience is required to design your own quests. This also means the plugin will work without needing to be recompiled. Just throw QM into your plugins folder, do some setup on the configuration files, and away you go!

Abstraction
-----------

Typically in games involving quests, very specific information (such as location information, dialog & dialog options, etc) is very closely married (coupled) with the codebase behind it. Quest systems derived from configuration files typically only include text-based RPG's and similar, as the more specific information is neccassary for games with a higher dimenionallity/complexity. To address this issue, QM:

    * Defines an expanded configuration vocabulary
    * Is optimized for large configuration file reading




Still To Come
-------------

An official documentation of the configuration:

- Structure
- Equipment Specification
- Requirement Enumeration/Types
