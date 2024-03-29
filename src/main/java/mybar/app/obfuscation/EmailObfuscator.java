package mybar.app.obfuscation;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailObfuscator implements IObfuscator {

    private static final String OBFUSCATE_SEQUENCE = "***";
    private static final String AT = "@";
    private static final String DOT = ".";

    private static final String EMAIL_REGEXP =
            "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,4})$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEXP);

    public String obfuscate(final String email) {

        if (!StringUtils.hasText(email)) {
            return EMPTY_STRING;
        }

        Matcher emailMatcher = emailPattern.matcher(email);

        if (!emailMatcher.matches()) {
            return email.charAt(0) + OBFUSCATE_SEQUENCE;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(email.charAt(0));
        sb.append(OBFUSCATE_SEQUENCE);
        sb.append(AT);

        String hostName = email.substring(email.indexOf(AT) + 1, email.lastIndexOf(DOT));
        if (hostName.length() == 1 || hostName.length() == 2) {
            sb.append(hostName.charAt(0));
            sb.append(OBFUSCATE_SEQUENCE);
        } else {
            sb.append(hostName.charAt(0));
            sb.append(OBFUSCATE_SEQUENCE);
            sb.append(hostName.substring(hostName.length() - 1));
        }

        sb.append(email.substring(email.lastIndexOf(DOT)));

        return sb.toString();
    }

}