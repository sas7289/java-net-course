package ru.daniilazarnov.serverOperations;

import ru.daniilazarnov.Commands;

import java.nio.channels.SelectionKey;

public class FabricOfOperations {
    public static ServerOperation getOperation(Commands command, SelectionKey key) {
        switch (command) {
            case stor:
                return new SaveFile(key);
            case mkd:
                return new MakeDirectory(key);
            case cd:
                return new ChangeClientDirectory(key);
            case retr:
                return new UploadFileToClient(key);
            case user:
                return new AuthenticationUser(key);
            case pwd:
                return new PresentWorkDirectory(key);
            case disconnect:
                return new DisconnectClient(key);
            case ls:
                return new ListOfFilesOrDirectories(key);
            default:
                return null;
        }
    }
}
