package com.paul9834.exoplayerchannel.Entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogCat {

    public String writeLog () {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }

            return log.toString();
        } catch (IOException e) {
        }
        return "null";
    }

    public void clearLog(){
        try {
            Process process = new ProcessBuilder()
                    .command("logcat", "-d")
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException e) {
        }
    }
}
