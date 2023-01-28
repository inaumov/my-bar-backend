package mybar.app.obfuscation;

import com.google.common.base.Strings;

public class UsernameObfuscator implements IObfuscator {

    /**
     * Method obfuscates username
     * If username is more than 4 symbols method obfuscate first 3 and the last one symbols: username -> use***m
     * Else last symbol is obfuscated by "***" : user -> use***
     */
    public String obfuscate(String username) {
        if (Strings.isNullOrEmpty(username)) {
            return EMPTY_STRING;
        }
        int nameLength = username.length();
        if (nameLength > 4) {
            String obfuscatedString = Strings.repeat("*", nameLength - 4);
            return new StringBuffer(username).replace(3, nameLength - 1, obfuscatedString).toString();
        } else {
            String obfuscatedString = "***";
            return new StringBuffer(username).replace(nameLength - 1, nameLength, obfuscatedString).toString();
        }
    }

}