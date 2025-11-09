package woowa.chrono.bot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChronoBot extends ListenerAdapter {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        System.out.println("봇을 실행합니다.");

        JDA jda = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.customStatus("개발 중 입니다."))
                .build();

        System.out.println("봇이 성공적으로 시작되었습니다!");
    }
}
