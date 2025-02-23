package ru.daniilazarnov;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Protocol {
    private static final int DEFAULT_BUFFER_SIZE_FOR_STRING_REICIVER = 4;
    private static final int DEFAULT_BUFFER_SIZE_FOR_STRING_SENDER = 512;
    private static final int DEFAULT_BUFFER_SIZE_FOR_FILE_SENDER = 8192;

    public static String getStringFromSocketChannel(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE_FOR_STRING_REICIVER);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        int fileNameLength = byteBuffer.getInt();
        byteBuffer = ByteBuffer.allocate(fileNameLength);
        StringBuilder sb = new StringBuilder();
        while (fileNameLength > 0) {
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining() && fileNameLength > 0) {
                sb.append((char) byteBuffer.get());
                fileNameLength--;
            }
            byteBuffer.compact();
        }
        String fileName = sb.toString();
        return fileName;
    }

    public static ByteBuffer getFileInByteBufferFromSocketChannel(SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE_FOR_FILE_SENDER);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        int sizeFile = byteBuffer.getInt();
        ByteBuffer fileBuffer = ByteBuffer.allocate(sizeFile);
//                   Собираем файл из буфера
        while (sizeFile > 0) {
            while (byteBuffer.hasRemaining() && sizeFile > 0) {
                fileBuffer.put(byteBuffer.get());
                sizeFile--;
            }
            byteBuffer.clear();
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
        }
        return fileBuffer;
    }

    public static ByteBuffer wrapStringInByteBuffer(String message) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE_FOR_STRING_SENDER);
        byteBuffer.put(Commands.message.getNumberOfCommand());
        byteBuffer.putInt(message.length());
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        return byteBuffer;
    }

    public static boolean sendFileToSocketChannel(Path srcPath, SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE_FOR_FILE_SENDER);
        String fileName = srcPath.getFileName().toString();
        if (!Files.exists(srcPath)) {
            return false;
        }
        byteBuffer.put((Commands.stor.getNumberOfCommand()));
        int fileNameLength = fileName.length();
        byteBuffer.putInt(fileNameLength);
        byteBuffer.put(fileName.getBytes());
        byteBuffer.putInt((int) Files.size(srcPath));
        FileChannel srcFileChannel = (FileChannel) Files.newByteChannel(srcPath);
        while (srcFileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        srcFileChannel.close();
        return true;
    }
}
