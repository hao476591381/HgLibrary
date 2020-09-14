package com.hg.lib.audio;

public class AudioBean {
    private String fileName;
    private String fileTime;
    private String filePath;
    private String fileSize;

    public AudioBean(String fileName, String fileTime, String filePath, String fileSize) {
        this.fileName = fileName;
        this.fileTime = fileTime;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTime() {
        return fileTime;
    }

    public String getFilePath() {
        return filePath;
    }
}
