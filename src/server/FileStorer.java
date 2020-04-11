package server;

import java.io.*;
import java.net.Socket;

class FileStorer {

    private Socket socket;
    private String fileName;

    public FileStorer(Socket socket, String fileName) {
        this.socket = socket;
        this.fileName = fileName;
    }

    public void saveFile() throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] buffer = new byte[4096];

        int filesize = 15123; // Send file size in separate msg
        int read = 0;
        int totalRead = 0;
        int remaining = filesize;
        while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }

        fos.close();
        dis.close();
    }

    public String getFileName() {
        return fileName;
    }
}
