package woowa.chrono.config;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woowa.chrono.Listener.CommandListener;
import woowa.chrono.Listener.MemberListener;
import woowa.chrono.Listener.StudyRecordListener;

@Configuration
public class JdaConfig {

    private final CommandListener commandListener;
    private final MemberListener memberListener;
    private final StudyRecordListener studyRecordListener;

    public JdaConfig(CommandListener commandListener, MemberListener memberListener,
                     StudyRecordListener studyRecordListener) {
        this.commandListener = commandListener;
        this.memberListener = memberListener;
        this.studyRecordListener = studyRecordListener;
    }

    @Bean
    public JDABuilder jdaBuilder() {
        Dotenv dotenv = Dotenv.load();
        final String TOKEN = dotenv.get("DISCORD_TOKEN");
        System.out.println("봇을 실행합니다.");
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN,
                GatewayIntent.GUILD_MESSAGES,      // 서버 메시지 읽기
                GatewayIntent.MESSAGE_CONTENT,     // 메시지 내용 읽기
                GatewayIntent.GUILD_VOICE_STATES); // 음성 채널 상태 확인용 인텐트

        // Listener 등록
        jdaBuilder.addEventListeners(commandListener);
        jdaBuilder.addEventListeners(memberListener);
        jdaBuilder.addEventListeners(studyRecordListener);

        return jdaBuilder;
    }

    @Bean
    public JDA jda(JDABuilder builder) throws Exception {
        return builder.build();
    }
}
