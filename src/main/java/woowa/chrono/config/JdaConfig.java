package woowa.chrono.config;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JdaConfig {

    @Bean
    public JDABuilder jdaBuilder() {
        Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        System.out.println("봇을 실행합니다.");
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        // Listener 등록
        
        return jdaBuilder;
    }

    @Bean
    public JDA jda(JDABuilder builder) throws Exception {
        return builder.build();
    }
}
