# Bot Documentation - Design

## Objectives
 - Create an easily scalable bot with easy to create modules of independant logic.
 - Easy to extract stats and logging mechanics
 - Easy to extract information about commands and guild-specific information

### Bot
 - Dedicated websocket hosting information about:
    - Current loaded modules and their commands/permissions
    - Current statistics (api accesses, status, no. guilds/users, etc...)
 - ApiClient
 - Logger
 - ExtManager
    - Responsible for keeping track of loaded modules
 -