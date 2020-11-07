package ovh.excale.roleman.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import ovh.excale.roleman.RoleMan;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand extends Command {

	public SpawnCommand() {
		this.name = "spawn";
		this.arguments = "<@RoleMention>...";
		this.help = "Spawns one or more messages to which you can react to, it'll add or remove a certain role.";
		this.guildOnly = true;
		this.usesTopicTags = false;
		this.botPermissions = new Permission[] {
				Permission.MANAGE_ROLES,
				Permission.MESSAGE_WRITE,
				Permission.MESSAGE_MANAGE,
				Permission.MESSAGE_ADD_REACTION,
		};
		this.userPermissions = new Permission[] { Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION };
	}

	@Override
	protected void execute(CommandEvent commandEvent) {
		Message message = commandEvent.getMessage();
		MessageChannel channel = message.getChannel();
		User author = commandEvent.getAuthor();

		List<Role> roles = message.getMentionedRoles();

		try {

			roles.forEach(role ->

					channel.sendMessage(new EmbedBuilder().setTitle(role.getName())
							.setDescription("React to this message to self add/remove the " + role.getAsMention() + " role")
							.build()).queue(roleMessage -> {
								roleMessage.addReaction(RoleMan.EMOTE_YES).queue();
								roleMessage.addReaction(RoleMan.EMOTE_NO).queue();
							},
							t -> author.openPrivateChannel()
									.flatMap(dm -> dm.sendMessage(
											"There has been an error trying to send messages for roles `" + roles.stream()
													.map(Role::getName)
													.collect(Collectors.joining()) + "`\n" + t.getMessage()))
									.queue()));

		} catch(Exception e) {
			author.openPrivateChannel()
					.flatMap(dm -> dm.sendMessage("There has been an error trying to send a message from you command.\n" + e
							.getMessage()))
					.queue();
		}

		message.delete().queue();
	}

}
