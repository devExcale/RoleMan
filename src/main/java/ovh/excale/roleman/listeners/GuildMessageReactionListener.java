package ovh.excale.roleman.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuildMessageReactionListener extends ListenerAdapter {

	private final Map<String, GuildMessageReactionHandler> handlers;

	public GuildMessageReactionListener() {
		handlers = Collections.synchronizedMap(new HashMap<>());
	}

	public void registerHandler(GuildMessageReactionHandler handler) {
		handlers.put(handler.getEmoteCode(), handler);
	}

	public void registerHandlers(GuildMessageReactionHandler... handlers) {

		for(GuildMessageReactionHandler handler : handlers)
			this.handlers.put(handler.getEmoteCode(), handler);

	}

	public void unregisterHandler(GuildMessageReactionHandler handler) {
		handlers.remove(handler.getEmoteCode());
	}

	public void unregisterHandler(String emoteCode) {
		handlers.remove(emoteCode);
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		GuildMessageReactionHandler handler = handlers.get(event.getReaction()
				.getReactionEmote()
				.getAsReactionCode());
		User user = event.getUser();

		if(handler != null && !user.isBot())
			handler.onReactionAdd(event);

		event.getReaction().removeReaction(user).queue();

	}

	@Override
	public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
		GuildMessageReactionHandler handler = handlers.get(event.getReaction()
				.getReactionEmote()
				.getAsReactionCode());

		if(handler != null)
			handler.onReactionRemove(event);

	}

}
