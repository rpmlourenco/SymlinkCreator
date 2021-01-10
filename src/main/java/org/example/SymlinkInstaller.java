package org.example;

import java.io.IOException;

public class SymlinkInstaller {

    public static void main(String[] args) {

        final String dir = System.getProperty("user.dir");
        System.out.println("The current dir = " + dir);
        System.out.println("Press ENTER to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
