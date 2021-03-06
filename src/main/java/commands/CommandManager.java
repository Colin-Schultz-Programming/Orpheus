package commands;

import commands.botcommands.*;
import commands.botcommands.musiccommands.NowplayingCommand;
import commands.botcommands.musiccommands.PlayCommand;
import commands.botcommands.musiccommands.SkipCommand;
import commands.botcommands.musiccommands.StopCommand;
import configs.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager(){
        addCommand(new PingCommand());
        addCommand(new HelloCommand());
        addCommand(new ColinCommand());
        addCommand(new NathanCommand());
        addCommand(new ConnectCommand());
        addCommand(new DisconnectCommand());
        addCommand(new EchoCommand());
        addCommand(new HelpCommand(this));
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new SkipCommand());
        addCommand(new NowplayingCommand());
    }

    private void addCommand(ICommand cmd){
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
        if (nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }
        commands.add(cmd);
    }

    public List<ICommand> getAllCommands(){
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (ICommand cmd: this.commands){
            if (cmd.getName().equals(searchLower) || cmd.getAliases().contains(searchLower)){
                return cmd;
            }
        }
        return null;
    }

    public void handle(GuildMessageReceivedEvent event){
        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("PREFIX")), "")
                .split("\\s");
        String invoke = split[0].toLowerCase();
        ICommand cmd = this.getCommand(invoke);
        if(cmd!= null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1, split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
        else{
            event.getChannel().sendMessage("Command not found...").queue();
        }
    }
}
