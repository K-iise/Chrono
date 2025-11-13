package woowa.chrono.Listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class MemberListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        if (user.isBot()) {
            return;
        }
        String[] messageArray = message.getContentRaw().split(" ");

        if (messageArray[0].equalsIgnoreCase("/명령어")) {
            String test = "test";
            textChannel.sendMessage(test).queue();
        }
    }
}
