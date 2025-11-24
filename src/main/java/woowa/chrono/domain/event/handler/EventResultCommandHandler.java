package woowa.chrono.domain.event.handler;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.util.DurationUtils;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.domain.event.service.EventService;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.study.repository.StudyRecordProjection;

@Component
public class EventResultCommandHandler implements CommandHandler {

    private final EventService eventService;

    public EventResultCommandHandler(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public String getDescription() {
        return "이벤트 결과";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.reply("유효하지 않은 명령어입니다.").setEphemeral(true).queue();
            return;
        }
        switch (subCommand) {
            case "result" -> handleResult(event);
            default -> event.reply("알 수 없는 명령어입니다.").queue();
        }
    }

    @Override
    public Grade requiredGrade() {
        return Grade.ADMIN;
    }

    private void handleResult(SlashCommandInteractionEvent event) {
        try {
            event.deferReply(false).queue();
            MessageChannelUnion channel = event.getChannel();

            if (!channel.getType().isThread()) {
                event.reply("이 명령어는 **포럼 글(스레드)** 안에서만 사용 가능합니다.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            // 포럼 이벤트의 저장 위치를 통해서 이벤트 참가 여부를 결정함.
            ThreadChannel threadChannel = channel.asThreadChannel();
            String location = threadChannel.getJumpUrl();

            // 정렬된 상태로 이벤트 참가자의 공부 내역을 조회합니다.
            List<StudyRecordProjection> list = eventService.summaryStudyEvent(location);
            AtomicInteger rankCounter = new AtomicInteger(1);
            String result = list.stream()
                    // 총 공부 시간(TotalTimeSeconds) 기준으로 내림차순 정렬
                    .sorted(Comparator.comparingLong(StudyRecordProjection::getTotalTimeSeconds).reversed())
                    // 등수(rankCounter)를 증가시키면서 결과를 포맷팅
                    .map(p -> formatResult(p, rankCounter.getAndIncrement()))
                    .collect(Collectors.joining("\n"));

            if (result.isEmpty()) {
                event.getHook().sendMessage("이벤트에 참여한 멤버가 없습니다.").queue();
            } else {
                event.getHook().sendMessage("## \uD83C\uDFC6 이벤트 최종 랭킹\n\n" + result).queue();
            }
        } catch (ChronoException e) {
            event.getHook().sendMessage(e.getMessage()).queue();
        }

    }

    private String formatResult(StudyRecordProjection p, int rank) {
        return String.format(
                "• **%d등** | <@%s> 님의 이용시간: %s",
                rank,
                p.getMember().getUserId(),
                DurationUtils.format(p.getTotalTime())
        );
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
                new SubcommandData("result", "이벤트의 결과를 출력합니다.")
        );
    }
}
