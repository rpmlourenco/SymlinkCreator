package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class SymlinkInstaller {

    String baseFolder;
    String[] ignoreFolder;
    Map<String, String> variableFolder = new LinkedHashMap<>();
    String[] operationFolder;

    public SymlinkInstaller() throws IOException {
        this("user.dir");
    }

    public SymlinkInstaller(String folder) throws IOException {
        baseFolder = folder;

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream("config.properties")) {
            props.load(resourceStream);
            ignoreFolder = props.getProperty("folder.ignore").split(",");
            operationFolder = props.getProperty("folder.operation").split(",");
            String[] map = props.getProperty("folder.variable").split(",");
            for (String item: map) {
                String[] key = item.split("=");
                variableFolder.put(key[0],key[1]);
            }
        }
    }

    private boolean ignore(String aFolder) {
        boolean result = false;

        for (String folder: ignoreFolder) {
            if (folder.startsWith(aFolder)) {
                result=true;
                break;
            }
        }

        return result;
    }

    private String replaceVariable(String aFolder) {
        for (Map.Entry<String, String> entry : variableFolder.entrySet()) {
            if (aFolder.contains(entry.getKey())) {
                aFolder = aFolder.replaceFirst(Pattern.quote(entry.getKey()), entry.getValue());
                break;
            }
        }

        return aFolder;
    }

    private void copyBuffered(BufferedReader br, BufferedWriter bw) throws IOException {
        int i;
        do {
            i = br.read();
            if (i != -1) {
                bw.write((char) i);
            }
        } while (i != -1);
    }

    public void createInstaller() throws IOException {

        BufferedReader inHeader = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("Header.cmd"),
                        StandardCharsets.UTF_8  // Set encoding
                )
        );

        BufferedReader inFooter = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("Footer.cmd"),
                        StandardCharsets.UTF_8  // Set encoding
                )
        );

        BufferedWriter outInstaller = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(baseFolder + "\\SymLink Installer.cmd", false), // true to append
                        StandardCharsets.UTF_8                  // Set encoding
                )
        );

        copyBuffered(inHeader, outInstaller);
        inHeader.close();

        File dir = new File(baseFolder + "\\C");
        File[] files = dir.listFiles();
        if (files != null) analyseFolders(files, dir.getAbsolutePath());
        //outInstaller.write("Isto é um teste!");

        copyBuffered(inFooter, outInstaller);
        inFooter.close();
        outInstaller.close();

    }

    public void analyseFolders(File[] files, String root) {
        for (File file : files) {
            if (file.isDirectory()) {
                String relativePath = file.getAbsolutePath().replaceFirst(Pattern.quote(root+"\\"),"");
                if (ignore(relativePath)) {
                    //System.out.println("Directory: " + relativePath + " (skip)");
                    File[] sFiles = file.listFiles();
                    if (sFiles != null) analyseFolders(sFiles, root); // Calls same method again.
                } else {
                    for (String operation: operationFolder) {
                        File opFile = new File(file.getParent()+"\\"+file.getName()+"_"+operation+".txt");
                        if (opFile.exists()) {
                            System.out.println("Directory: " + relativePath + " ("+operation+")");
                            break;
                        } else {
                            File[] sFiles = file.listFiles();
                            if (sFiles != null) analyseFolders(sFiles, root); // Calls same method again.
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        SymlinkInstaller installer;
        try {
            installer = new SymlinkInstaller("C:\\Users\\rpmlo\\Desktop\\Steinberg Retrologue");
            installer.createInstaller();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
