package woowa.chrono.config;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woowa.chrono.Listener.MemberListener;

@Configuration
public class JdaConfig {

    private final MemberListener memberListener;

    public JdaConfig(MemberListener memberListener) {
        this.memberListener = memberListener;
    }

    @Bean
    public JDABuilder jdaBuilder() {
        Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        System.out.println("봇을 실행합니다.");
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN,
                GatewayIntent.GUILD_MESSAGES,      // 서버 메시지 읽기
                GatewayIntent.MESSAGE_CONTENT);    // 메시지 내용 읽기

        // Listener 등록
        jdaBuilder.addEventListeners(memberListener);

        return jdaBuilder;
    }

    @Bean
    public JDA jda(JDABuilder builder) throws Exception {
        return builder.build();
    }
}
