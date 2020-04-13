package server;

import model.User;

public class TransferRequest {
    private int transferId;
    private User sender;
    private User receiver;

    private String fileName;

    public TransferRequest(int transferId, User sender, User receiver, String fileName) {
        this.transferId = transferId;
        this.sender = sender;
        this.receiver = receiver;
        this.fileName = fileName;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
