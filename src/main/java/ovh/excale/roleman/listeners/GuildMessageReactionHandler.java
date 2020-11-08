package ovh.excale.roleman.listeners;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

public interface GuildMessageReactionHandler {

	String getEmoteCode();

	default void onReactionAdd(GuildMessageReactionAddEvent event) { }

	default void onReactionRemove(GuildMessageReactionRemoveEvent event) { }

}
