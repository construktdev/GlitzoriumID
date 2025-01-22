package de.construkter.glitzoriumLink.api;

import java.util.HashMap;

public class LinkingAPI {
    public static HashMap<String, String> playerCodes;
    public static HashMap<String, String> playerNames;

    public static String generateAuthCode() {
        return String.valueOf((int) (Math.random() * 10000));
    }
}
