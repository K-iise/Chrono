package woowa.chrono.domain.member.handler;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Component;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.Member;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class RegisterCommandHandler implements CommandHandler {

    private final MemberService memberService;

    public RegisterCommandHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "사용자를 등록합니다.";
    }

    @Override
    public Grade requiredGrade() {
        return Grade.ADMIN;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        try {
            String callerId = event.getUser().getId();
            Grade callerGrade = memberService.findMemberOrThrow(callerId).getGrade();

            if (callerGrade != Grade.ADMIN) {
                event.reply("이 명령어는 관리자만 사용할 수 있습니다.").setEphemeral(true).queue();
                return;
            }

            String memberId = event.getOption("user").getAsUser().getId();
            String memberName = event.getOption("user").getAsUser().getName();

            // 특정 회원 ID와 부여할 권한을 미리 설정합니다.
            long targetUserId = event.getOption("user").getAsUser().getIdLong();

            // 특정 회원에게 허용할 권한: 채널 보기(VIEW_CHANNEL) 및 메시지 전송(MESSAGE_SEND)
            long allowForMember = Permission.VIEW_CHANNEL.getRawValue() | Permission.MESSAGE_SEND.getRawValue();

            // 개인 텍스트 채널 생성
            TextChannel personalChannel = event.getGuild()
                    .createTextChannel(memberName)
                    .addPermissionOverride(event.getGuild().getPublicRole(), 0L,
                            allowForMember)
                    .addMemberPermissionOverride(targetUserId,
                            allowForMember, 0L)
                    .complete();

            // 생성한 채널 ID 저장
            Member member = Member.builder()
                    .userId(memberId)
                    .userName(memberName)
                    .channelId(personalChannel.getId())
                    .build();

            memberService.registerMember(member);

            event.reply("등록 완료!").queue();

        } catch (IllegalStateException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "등록할 사용자", true));
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of();
    }
}
