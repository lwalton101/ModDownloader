package com.nicknotname.moddownloader;



import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModDownloader {
    public static String masterKey = "$2a$10$lr8scoeFI35OKJXkBfh6eO8q9TQLZCk3R88RqqJk3b8IZ3MVIkAni";
    public static ArrayList<Mod> mods;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        File startFile = new File("start.bat");
        if(!startFile.exists()){
            startFile.createNewFile();
            FileWriter writer = new FileWriter("start.bat");
            writer.write("java -jar ModDownloader.jar");
            writer.close();
            System.exit(0);
        }
        ArrayList<Integer> modIds = new ArrayList<>();
        ArrayList<Integer> fileIds = new ArrayList<>();


        String jsonString = NetworkHandler.getHTML("https://api.jsonbin.io/b/620e3c341b38ee4b33bfc8d4/latest");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        JsonArray jsonModIds = jsonObject.getAsJsonArray("modIds");
        JsonArray jsonFileIds = jsonObject.getAsJsonArray("fileIds");

        for(JsonElement i : jsonModIds){
            modIds.add(Integer.parseInt(i.toString()));
        }

        for(JsonElement i : jsonFileIds){
            fileIds.add(Integer.parseInt(i.toString()));
        }

        mods = assembleMods(modIds, fileIds);

        System.out.println("Welcome to the Mod Downloader!");
        System.out.println("What would you like to do?");
        System.out.println("1.) Download all mod files");
        System.out.println("2.) PURGE all files downloaded by the Mod Downloader");
        System.out.println("3.) View all files downloaded by the Mod Downloader");
        System.out.println("4.) View all files that could be downloaded by the Mod Downloader");

        try {
            int input = scanner.nextInt();
            switch (input){
                case 1:
                    downloadAllModFiles();
                    break;
                case 2:
                    if(shouldBePurged()){
                        System.out.println("Should be purged");
                        deleteMods();
                    }

                    System.out.println("");
                    System.out.println("Press enter to close");
                    System.in.read();
                    break;
                case 3:
                    String dir = new File("").getAbsolutePath();
                    File folder = new File(dir);
                    for(File file : folder.listFiles()){
                        if(file.getName().contains("[ModDownloader]")){
                            System.out.println(file.getName());
                        }
                    }
                    System.out.println("");
                    System.out.println("Press enter to close");
                    System.in.read();
                    break;
                case 4:
                    System.out.format("%-30s%-10s%-30s%-13s", "Mod Name", "Mod ID", "Mod Filename", "Installed");
                    System.out.println("");

                    for(Mod mod : mods){
                        System.out.format("%-30s%-10s%-30s%-13s", mod.name, mod.modId, mod.file.fileName, checkIfFileisInFolder(mod.file.fileName));
                        System.out.println("");
                    }
                    System.out.println("");
                    System.out.println("Press enter to close");
                    System.in.read();
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {

        }

        scanner.close();
    }

    private static boolean checkIfFileisInFolder(String name) {
        String dir = new File("").getAbsolutePath();
        File folder = new File(dir);
        for(File file : folder.listFiles()){
            if(file.getName().equals("[ModDownloader] " + name)){
                return true;
            }
        }

        return false;
    }

    public static void downloadAllModFiles() throws Exception {

        if(shouldBePurged()){
            System.out.println("It looks like you already have some [ModDownloader] mods installed! Do you want to purge first?");
            System.out.println("1.) Purge first then download(recommended)");
            System.out.println("2.) Continue with the download");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.nextLine()){
                case "1":
                    deleteMods();
                    break;
                case "2":
                    break;
            }
        }

        for(Mod mod : mods){
            NetworkHandler.downloadMod(mod);
        }

        System.out.println("");
        System.out.println("Press enter to close");
        System.in.read();
    }

    private static void deleteMods(){
        String dir = new File("").getAbsolutePath();
        File folder = new File(dir);
        for(File file : folder.listFiles()){
            if(file.getName().contains("[ModDownloader]")){
                file.delete();
            }
        }

        System.out.println("All purging complete");
    }

    public static ArrayList<Mod> assembleMods(ArrayList<Integer> modIds, ArrayList<Integer> fileIds) throws Exception {
        ArrayList<Mod> mods = new ArrayList<>();

        for(int i = 0; i < modIds.size(); i++){
            int index = modIds.get(i);
            String modResponse = NetworkHandler.getHTML("https://api.curseforge.com/v1/mods/" + index, masterKey);
            String fileResponse = NetworkHandler.getHTML("https://api.curseforge.com/v1/mods/" + modIds.get(i) + "/files/" + fileIds.get(i), masterKey);
            Gson gson = new Gson();
            JsonObject modObject = gson.fromJson(modResponse, JsonObject.class).getAsJsonObject().getAsJsonObject("data");
            JsonObject fileObject = gson.fromJson(fileResponse, JsonObject.class).getAsJsonObject().getAsJsonObject("data");
            Mod mod = new Mod(modObject.get("name").getAsString(), createFileFromJsonObject(fileObject), modObject.get("links").getAsJsonObject().get("websiteUrl").getAsString(), modObject.get("id").getAsInt());
            mods.add(mod);
        }

        return mods;
    }

    private static ModFile createFileFromJsonObject(JsonObject fileObject) {
        return new ModFile(fileObject.get("fileName").getAsString(),fileObject.get("downloadUrl").getAsString(), fileObject.get("id").getAsInt());
    }

    private static boolean shouldBePurged(){
        String dir = new File("").getAbsolutePath();
        File folder = new File(dir);
        for(File file : folder.listFiles()){
            if(file.getName().contains("[ModDownloader]")){
                return true;
            }
        }

        return false;
    }
}
