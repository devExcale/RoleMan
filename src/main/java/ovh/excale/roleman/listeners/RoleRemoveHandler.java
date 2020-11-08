package ovh.excale.roleman.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import ovh.excale.roleman.RoleMan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoleRemoveHandler implements GuildMessageReactionHandler {

	private static final Pattern roleIdRegex = Pattern.compile("<@&([\\d]{1,26})>");

	@Override
	public String getEmoteCode() {
		return RoleMan.EMOTE_NO;
	}

	@Override
	public void onReactionAdd(GuildMessageReactionAddEvent event) {
		MessageReaction reaction = event.getReaction();
		Member member = event.getMember();
		Guild guild = event.getGuild();
		long messageId = reaction.getMessageIdLong();

		System.out.println("130");

		// Get original message
		reaction.getChannel().retrieveMessageById(messageId).queue(message -> {

			List<MessageEmbed> embeds = message.getEmbeds();

			// Message was sent by RoleMan
			if(message.getAuthor()
					.getId()
					.equals(RoleMan.selfUser().getId()) && embeds.size() == 1) {


				// Get message embed
				String description = embeds.get(0).getDescription();
				System.out.println(description);
				if(description == null)
					return;

				Matcher matcher = roleIdRegex.matcher(description);
				// Role id !found
				if(!matcher.find()) {
					System.out.println("Matcher out");
					return;
				}

				// Get role from id
				Role role = guild.getRoleById(matcher.group(1));

				// Open DM to comunicate operation result
				member.getUser().openPrivateChannel().queue(dm -> {
					String guildName = guild.getName();

					// Role not found
					if(role == null) {
						dm.sendMessageFormat(
								"The role you're trying to get in guild `%s` doesn't exist anymore.",
								guildName).queue();
						return;
					}

					String roleName = role.getName();
					System.out.println(roleName);

					// Try to give role
					// TODO: EXCEPTION CATCH
					guild.removeRoleFromMember(member, role).queue(

							// Role give success
							unused -> dm.sendMessageFormat(
									"You no longer have the role `%s` on guild `%s`.",
									roleName,
									guildName).queue(),

							// Role give fail
							throwable -> dm.sendMessageFormat(
									"I couldn't remove from you the role `%s` on guild `%s`.",
									roleName,
									guildName).queue());

				}); // Open DM channel END
			} // Role mention found END
		}); // Get original message END
	}

}