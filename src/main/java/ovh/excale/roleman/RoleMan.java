package ovh.excale.roleman;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleMan extends ListenerAdapter {

	// Cr4zy5ky_U#1164
	private static final String OWNER = "309315257996673025";

	private static final String[] CO_OWNERS = new String[] {
			"481080371220316180",        // Lele050201#8305
			"331031994874200066"        // DEUSps#8621
	};

	private static final String TOKEN;

	private static final Logger logger;

	private static JDA jda;

	public static JDA getJda() {
		return jda;
	}

	static {

		// GET LOGGER
		logger = Logger.getLogger(RoleMan.class.getSimpleName());

		// GET TOKEN FROM SYS_ENV
		String token = System.getenv("ROLEMAN_BOT_TOKEN");

		// TOKEN NOT FOUND
		if(token == null)
			// GET TOKEN FROM PROPERTIES
			try {

				Properties properties = new Properties();
				InputStream in = RoleMan.class.getClassLoader().getResourceAsStream("bot.properties");
				properties.load(in);

				token = properties.getProperty("token");

			} catch(IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
				token = null;
			}

		TOKEN = token;
	}

	public static void main(String[] args) {

		if(TOKEN == null) {
			logger.log(Level.SEVERE, "Missing bot token, shutting down.");
			return;
		}

		CommandClient client = new CommandClientBuilder().setPrefix("role:")
				.setActivity(Activity.watching("roles"))
				.setOwnerId(OWNER)
				.setCoOwnerIds(CO_OWNERS)
				.build();

		try {

			jda = JDABuilder.create(TOKEN,
					GatewayIntent.GUILD_MEMBERS,
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_MESSAGE_REACTIONS).addEventListeners(client).build();

		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

}
