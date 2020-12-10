package ovh.excale.roleman;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ovh.excale.roleman.commands.DirectPingCommand;
import ovh.excale.roleman.commands.PurgeChannelCommand;
import ovh.excale.roleman.commands.SpawnCommand;
import ovh.excale.roleman.listeners.GuildMessageReactionListener;
import ovh.excale.roleman.listeners.RoleAddHandler;
import ovh.excale.roleman.listeners.RoleRemoveHandler;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleMan {

	public static final String EMOTE_YES = "\u2705";
	public static final String EMOTE_NO = "\u274E";

	public static final String OWNER;
	public static final String[] CO_OWNERS;
	public static final String VERSION;

	private transient static final String TOKEN;
	private static final Logger logger;

	private static JDA jda;
	private static User selfUser;

	public static User selfUser() {
		return selfUser;
	}

	static {

		// GET LOGGER
		logger = Logger.getLogger(RoleMan.class.getSimpleName());

		// VERSION META
		String version;
		InputStream in = RoleMan.class.getClassLoader()
				.getResourceAsStream("VERSION");
		if(in != null)
			try(Scanner scanner = new Scanner(in)) {
				version = scanner.nextLine();
			} catch(Exception e) {
				logger.log(Level.WARNING, "Coudln't retrieve VERSION meta", e);
				version = "unknown";
			}
		else
			version = "unknown";

		VERSION = version;

		for(String arg : new String[] { "DS_TOKEN", "DS_OWNER" })
			try {
				Objects.requireNonNull(System.getenv(arg));
			} catch(NullPointerException e) {
				logger.log(Level.SEVERE, "Missing " + arg + ", shutting down", e);
				System.exit(-1);
			}

		BiFunction<String, String, String> coal = (s1, s2) -> s1 != null ? s1 : s2;

		// GET ENVs
		TOKEN = System.getenv("DS_TOKEN");
		OWNER = System.getenv("DS_OWNER");
		CO_OWNERS = Arrays.stream(Optional.ofNullable(System.getenv("DS_COOWNERS"))
				.orElse("")
				.trim()
				.split(" *, *"))
				.filter(s -> s.length() != 0)
				.toArray(String[]::new);
	}

	public static void main(String[] args) {


		CommandClient client = new CommandClientBuilder().setOwnerId(OWNER)
				.addCommands(new SpawnCommand(), new DirectPingCommand(), new PurgeChannelCommand())
				.setCoOwnerIds(CO_OWNERS)
				.setActivity(Activity.watching("roles | role:help"))
				.setPrefix("role:")
				.build();

		GuildMessageReactionListener reactionListener = new GuildMessageReactionListener();
		reactionListener.registerHandlers(new RoleAddHandler(), new RoleRemoveHandler());

		try {

			jda = JDABuilder.create(TOKEN,
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_MESSAGE_REACTIONS)
					.disableCache(CacheFlag.ACTIVITY,
							CacheFlag.VOICE_STATE,
							CacheFlag.EMOTE,
							CacheFlag.CLIENT_STATUS)
					.addEventListeners(new EventWaiter(), reactionListener, client)
					.build()
					.awaitReady();

		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		selfUser = jda.getSelfUser();

		logger.log(Level.INFO, String.format("Bot running on version %s.", VERSION));

	}

}
