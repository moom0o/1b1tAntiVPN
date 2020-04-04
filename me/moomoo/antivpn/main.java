package me.moomoo.antivpn;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class main extends JavaPlugin implements Listener {
    public void onEnable() {
        System.out.println("[ENABLED] moomoo's antivpn plugin, originally made for 1b1t.tk");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);


    }
    public void onDisable() {
        System.out.println("[DISABLED] moomoo's antivpn plugin, originally made for 1b1t.tk. Goodnight.");
    }
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent evt) throws IOException, ParseException {
        String ip = evt.getAddress().toString().replace("/", ""); // The ip
        String name = evt.getName();
        System.out.println("[JOIN] " + name + " " + ip);
        URL url = new URL("http://v2.api.iphub.info/ip/" + ip);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        httpConn.setRequestProperty ("X-Key", "INPUTKEYHERE==");
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());

        if(json.get("block").toString().equals("1")){
            URL url2 = new URL("http://whitelist.1b1t.tk/names");
            URLConnection connection2 = url2.openConnection();
            HttpURLConnection httpConn2 = (HttpURLConnection) connection2;
            BufferedReader in2 = new BufferedReader(new InputStreamReader(httpConn2.getInputStream()));
            String inputLine2;
            StringBuffer response2 = new StringBuffer();
            while ((inputLine2 = in2.readLine()) != null) {
                response2.append(inputLine2);
            }
            in2.close();
            JSONParser parser3 = new JSONParser();
            Object obj  = parser3.parse(response2.toString());
            JSONArray array = new JSONArray();
            array.add(obj);
            JSONArray array2 = (JSONArray) array.get(0);

            if(array2.contains(name)) {
                evt.allow();
                System.out.println("Allowed player: " + name + " ip: " + ip + " block number: " + json.get("block") + " because whitelisted");
            } else {
                evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§7You were kicked for joining with a vpn/proxy.\n§7You can type in your username (§f" + name + "§7) at §fwhitelist.1b1t.tk §7to skip this!\n§7If you still can't add your name for some reason contact the owner.\n§fhttps://discord.gg/2cVrTN5");
                System.out.println("Disallowed player: " + name + " ip: " + ip + " block number: " + json.get("block"));
            }
        } else {
            evt.allow();
            System.out.println("Allowed player: " + name + " ip: " + ip + " block number: " + json.get("block"));
        }
    }
}
