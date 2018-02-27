package mybar.app.obfuscation;

public interface IObfuscator {

    IObfuscator USERNAME_OBFUSCATOR = new UsernameObfuscator();
    IObfuscator EMAIL_OBFUSCATOR = new EmailObfuscator();

    String EMPTY_STRING = "";

    String obfuscate(final String value);

}