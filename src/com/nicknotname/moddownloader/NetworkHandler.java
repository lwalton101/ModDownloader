package com.nicknotname.moddownloader;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class NetworkHandler {
    public static void downloadMod(Mod mod) throws IOException, InterruptedException {
        System.out.println("Downloading " + mod.name);
        URL url = new URL(mod.file.downloadLink);
        try(InputStream in = url.openStream()){
            Files.copy(in, Paths.get("[ModDownloader] " + mod.file.fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(mod.name + " downloaded");
        }
    }

    public static String getHTML(String urlToRead) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlToRead))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String getHTML(String urlToRead, String apiKey) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlToRead))
                .header("x-api-key", apiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
