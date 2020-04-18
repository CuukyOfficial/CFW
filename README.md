# CFW - Cuuky FrameWork
A powerful bukkit framework that runs on 1.7+ and makes coding with bukkit less painful

#Dear developers
In the following I will show you the advantages of using my FrameWork and how much work it can save 

#Why should I use it for my plugin?
**CFW** makes it much easier to make your plugin 1.7+ compatible by having following included:

- A NetworkManager which contains following methods:
  - sendPacket(Packet packet) - Sends a packet
  - getPing() - Gets the ping of a player
  - respawnPlayer() - Respawns a player instantly without waiting for them to press the respawn button
  - sendLinkedMessage(String message, String link) - Sends a clickable chat message
  - sendTitle(String header, String footer) - Sends title header and footer
  - setnTablist(String header, String footer) (1.8+) - Sends tablist header and footer
  - sendActionbar(String message) (1.8+) - Sends a string to the actionbar of the player
  - setAttributeSpeed(double value) (1.9+) - Sets (removes) the hit delay implemented in 1.9

#How to use it?
