package woowa.chrono.config.jda.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;
import woowa.chrono.common.exception.ChronoException;
import woowa.chrono.common.exception.ErrorCode;
import woowa.chrono.config.jda.handler.CommandHandler;
import woowa.chrono.domain.member.Grade;
import woowa.chrono.domain.member.service.MemberService;

@Component
public class CommandListener extends ListenerAdapter {
    private final Map<String, CommandHandler> handlerMap;
    private final MemberService memberService;

    public CommandListener(List<CommandHandler> handlers, MemberService memberService) {
        this.memberService = memberService;
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getName, h -> h));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandHandler handler = handlerMap.get(event.getName());

        if (handler == null) {
            event.reply("지원하지 않는 명령어입니다.").queue();
            return;
        }

        try {
            // 호출자 등급 조회
            String callerId = event.getUser().getId();

            Grade callerGrade = null;
            if (handler.requiredGrade() != null) {
                // 등급 체크가 필요한 경우에만 DB 조회
                callerGrade = memberService.findMember(callerId, true).getGrade();
            }

            // 권한 체크
            if (!hasPermission(callerGrade, handler.requiredGrade())) {
                event.reply(ErrorCode.NOT_ADMIN.getMessage())
                        .setEphemeral(true)
                        .queue();
                return;
            }

            handler.handle(event);
        } catch (ChronoException e) {
            event.reply(e.getMessage()).queue();
        }

    }

    private boolean hasPermission(Grade caller, Grade required) {
        if (required == null) {
            return true; // 권한 체크 필요 없는 커맨드
        }
        return caller.ordinal() >= required.ordinal();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        handlerMap.values().forEach(handler -> {
            CommandData cd = Commands.slash(handler.getName(), handler.getDescription())
                    .addOptions(handler.getOptions().toArray(new OptionData[0]))
                    .addSubcommands(handler.getSubcommands())
                    .addSubcommandGroups(handler.getSubcommandGroups());

            // 관리자인 경우만 Permission 설정
            // 일반 멤버는 눌러도 비활성화 됨
            if (handler.requiredGrade() == Grade.ADMIN) {
                cd.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
            }

            commandData.add(cd);
        });

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
