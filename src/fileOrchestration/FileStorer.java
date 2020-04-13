package fileOrchestration;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The FileStorer is responsible for saving a file on the {fileSocket} if any data is written to it - note that its up to the programmer to manually start this process
 */
public class FileStorer extends Thread {

    private final Socket socket;
    //    private final String saveDir = "file_buffer/";
    private String fileName;
    private boolean success = false;

    public FileStorer(Socket socket, String fileName) {
        this.socket = socket;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        System.out.println("Setting up file storing procedure");
        try {
            fileName = saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileName != null) {
                System.out.println("File saved successfully at: " + fileName);
            }
        }
    }

    /**
     * @return path to file: String - on success or null on failure
     */
    public synchronized String saveFile() throws IOException {
        OutputStream fos = null;
        DataInputStream dis = null;
        try {
            int bytesRead;

            dis = new DataInputStream(socket.getInputStream());

            fos = new FileOutputStream(fileName);
            byte[] buffer = new byte[4096];
            while ((bytesRead = dis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            success = true;
            System.out.println("File " + fileName + " received from client.");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Server error. Connection closed.");
            return null;
        } finally {
            if (fos != null) fos.close();
            if (dis != null) dis.close();
        }

        return fileName;
    }

    public boolean isSucces() {
        return success;
    }

}