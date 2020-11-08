package ovh.excale.roleman.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.User;

public class DirectPingCommand extends Command {

	public DirectPingCommand() {
		this.name = "pingdm";
		this.arguments = "";
		this.help = "Pings you through Direct Message.";
		this.guildOnly = false;
		this.usesTopicTags = false;
	}

	@Override
	protected void execute(CommandEvent commandEvent) {
		User user = commandEvent.getAuthor();

		if(!user.isBot())
			user.openPrivateChannel()
					.flatMap(privateChannel -> privateChannel.sendMessage("Pong!"))
					.queue();
	}

}
