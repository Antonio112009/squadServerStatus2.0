![Java version](https://img.shields.io/badge/java%20version-12.0.1-red.svg)
![Maven version](https://img.shields.io/badge/maven%20version-3.6.0-red.svg)<br>
![hibernate version](https://img.shields.io/badge/hibernate%20version-5.4.2.Final-blue.svg)
![JDA version](https://img.shields.io/badge/jda%20version-4.BETA.0_4-blue.svg)

Creator: \[LANCE\]Tony Anglichanin (Tony Anglichanin#0007)


# SquadServers

Open source discord bot that displays number of people on squad servers, map and mode.

<img src="https://github.com/Antonio112009/squadServerStatus2.0/blob/master/screenshots/screensho1.png?raw=true" width="400" height="auto">

## Navigation:
* [Add bot to your Discord server](#Add-bot-to-your-Discord-server)
* [Guide](##Guide)
* [Commands](#Commands)


## Add bot to your Discord server 

To add bot to your server - simply [press this link](https://discordapp.com/oauth2/authorize?client_id=562952086438936586&scope=bot&permissions=8)

## Guide
To see how to set up bot on your server - go to [SquadServer wiki page](https://github.com/Antonio112009/squadServerStatus2.0/wiki).

## Commands
Here's a list of all commands that are implemented in the bot:

* everyone - public command
* admins - people with `MANAGE SERVER` permission or with role(s) that assigned to the bot
* creator - only creator of the bot OR holder (if he/she runs on their own server)

For Public channels:

| command | access | description |
| ------- | ------ | ----------- |
| ss/help | admins| shows list of commands |
| ss/guide | admins | shows guide of bot installation |
| ss/about | everyone | shows information about bot and discord servers that are using this bot |
| ss/credits | everyone | shows people contributed to the project  |
| ss/link | everyone | shows link to add bot to other discord channels  |
| ss/channel | admins | shows assigned channel to the bot |
| ss/addchannel #CHANNEL | admins | assignes bot to the channel |
| ss/editchannel #CHANNEL | admins | overrides already assigned channel to the bot |
| ss/servers | admins |  shows list of servers assigned to the bot |
| ss/addserver server1 server2 .. serverN | admins | assigns server/servers to the bot |
| ss/editserver server1 to server2 | admins | changes old server1 to new server2 |
| ss/deleteserver server1 server2 .. serverN | admins | delete server/servers from the bot |
| ss/clean | admins | refreshes data in assigned to bot channel (Firstly, Deletes all messages in assigned channel and after create again list of servers assigned to the bot) |
| ss/access | admins | shows list of roles who have access permission to the bot |
| ss/addrole @ROLE1 @ROLE2 .. @ROLE | admins | adds role/roles that can manage bot |
| ss/deleterole @ROLE1 @ROLE2 .. @ROLE | admins | delete role/roles that can manage bot |
| ss/channel | admins | shows assigned channel to the bot |
| ss/tr | creator | God knows what it does. Just shows number of threads |
| ss/exit | creator | turn off bot permanently |

For Private channel (write direct to the bot):    

| command | access | description |
| ------- | ------ | ----------- |
| ss/guilds | creator | shows all Discord servers where bot operates |
| ss/guilds_link | creator | get links to all discord servers where bot operates |
| ss/channels GUILD_ID | creator | shows all channels name and their ids (helps creator to locate server-status channel) |
| ss/data GUILD_ID CHANNEL_ID | creator | shows all messages (IDs only) in mentioned Discord server and channel (helps creator to see id's of the messages to fix them in database if needed) |

All commands that has `creator` access are only used to help/support people who are struggling with the bot.



