package ovh.excale.roleman.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import ovh.excale.roleman.RoleMan;

import java.util.List;
import java.util.logging.Logger;

public class RoleAddListener extends ListenerAdapter {

	private static final Logger logger = Logger.getLogger(RoleAddListener.class.getSimpleName());

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		MessageReaction reaction = event.getReaction();
		Member member = event.getMember();
		Guild guild = event.getGuild();
		long messageId = reaction.getMessageIdLong();

		reaction.getChannel().retrieveMessageById(messageId).queue(message -> {

			if(message.getAuthor().getId().equals(RoleMan.selfUser().getId())) {

				reaction.removeReaction(event.getUser()).queue();
				List<Role> roles = message.getMentionedRoles();
				if(roles.size() == 1) {
					Role role = roles.get(0);
					guild.addRoleToMember(member, roles.get(0))
							.queue(unused -> {},
									throwable -> member.getUser()
											.openPrivateChannel()
											.flatMap(dm -> dm.sendMessage("I'm sorry, I couldn't give you the role " + role
													.getName()))
											.queue());
				}
			}

		});
	}

}
