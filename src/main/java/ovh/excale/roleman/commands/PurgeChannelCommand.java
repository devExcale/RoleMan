package ovh.excale.roleman.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Set;
import java.util.stream.Collectors;

public class PurgeChannelCommand extends Command {

	public PurgeChannelCommand() {
		this.name = "purge";
		this.arguments = "[<@UserMention...>]";
		this.help = "Clears all messages by a user in a channel.";
		this.guildOnly = true;
		this.usesTopicTags = false;
		this.ownerCommand = true;
	}

	@Override
	protected void execute(CommandEvent commandEvent) {
		MessageChannel channel = commandEvent.getChannel();

		Set<String> usersId = commandEvent.getMessage()
				.getMentionedUsers()
				.stream()
				.map(ISnowflake::getId)
				.collect(Collectors.toSet());

		channel.purgeMessages(channel.getHistory()
				.getRetrievedHistory()
				.stream()
				.filter(message -> usersId.contains(message.getAuthor().getId()))
				.collect(Collectors.toList()));
	}

}
