package fileOrchestration;

import java.io.*;
import java.net.Socket;

public class FileInitiator extends Thread {
    private Socket socket;
    private String fileName;
    private long fileSize;
    private File fileBlob;

    private byte[] buffer;


    public FileInitiator(Socket socket, String fileName) {
        this.socket = socket;
        this.fileName = fileName;
        this.fileBlob = new File(fileName);
        this.fileSize = fileBlob.length() + 1;
        this.buffer = new byte[(int) fileBlob.length()];

    }

    public void sendFile() throws IOException {
        DataOutputStream dos = null;
        FileInputStream fis = null;

        try {

            dos = new DataOutputStream(socket.getOutputStream());
            fis = new FileInputStream(fileBlob);

            int read;
            while ((read = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, read);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }finally {
            if (dos != null) dos.flush();
            if (fis != null) fis.close();
        }
    }

    @Override
    public void run() {

        try {
            System.out.println("Sending file");
            sendFile();
        } catch (IOException e) {
            System.err.println("File initialization failed");
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public File getFileBlob() {
        return fileBlob;
    }
}