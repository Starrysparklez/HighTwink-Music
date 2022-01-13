package net.hightwink.musicbot.classes;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class AudioManager {
    private static final HashMap<Long, MusicPlayerManager> managers = new HashMap<>();

    public static MusicPlayerManager get(Guild guild) {
        long guildId = guild.getIdLong();
        if (!managers.containsKey(guildId)) {
            managers.put(guildId, new MusicPlayerManager(PlayerManager.getManager(), guild));
        }
        return managers.get(guildId);
    }
}
