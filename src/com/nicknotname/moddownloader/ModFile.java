package com.nicknotname.moddownloader;

public class ModFile {
    public String fileName;
    public String downloadLink;
    public int fileId;

    public ModFile(String fileName, String downloadLink, int fileId) {
        this.fileName = fileName;
        this.downloadLink = downloadLink;
        this.fileId = fileId;
    }
}
