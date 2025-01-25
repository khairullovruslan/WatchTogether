package ru.itis.khairullovruslan.watchtogether.constants;

public enum ChatCommand {
    HELP("/help", "Показать доступные команды"),
    INFO("/info", "Показать пароль и код комнаты"),
    TITLE("/title", "[название] - изменить заголовок"),
    KICK("/kick", "[никнейм] - выгнать игрока ");
    private final String command;
    private final String description;

    ChatCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }
    public String getDescription() {
        return description;
    }

    public static ChatCommand fromString(String command) {
        for (ChatCommand cmd : ChatCommand.values()) {
            if (cmd.getCommand().equals(command)) {
                return cmd;
            }
        }
        return null;
    }
}
