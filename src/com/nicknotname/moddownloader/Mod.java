package com.nicknotname.moddownloader;

public class Mod {
    public Mod(String name, ModFile file, String curseforgeLink, int modId) {
        this.name = name;
        this.file = file;
        this.curseforgeLink = curseforgeLink;
        this.modId = modId;
    }



    public String name;
    public ModFile file;
    public String curseforgeLink;
    public int modId;
}
