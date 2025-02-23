package ru.daniilazarnov.clientConnection.commands;

import ru.daniilazarnov.Commands;


public class FabricOfCommands {

    public static ICommand getCommand(ArgumentsForCommand arguments) {
        Commands command = arguments.getCommand();
        switch (command) {
            case stor:
                return new UploadFileToServerCommand(arguments);
            case mkd:
                return new MkDirCommand(arguments);
            case cd:
                return new ChangeDirCommand(arguments);
            case retr:
                return new DownloadFileCommand(arguments);
            case user:
                return new AuthCommand(arguments);
            case help:
                return new ShowHelpCommand(arguments);
            case connect:
                return new ConnectToServerCommand(arguments);
            case pwd:
                return new PresentWorkDirectoryCommand(arguments);
            case disconnect:
                return new DisconnectCommand(arguments);
            case ls:
                return new ListOfFilesOrDirectories(arguments);
            default:
                return new UnknownCommand();
        }
    }
}
