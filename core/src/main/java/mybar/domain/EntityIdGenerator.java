package mybar.domain;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

@Slf4j
public class EntityIdGenerator implements IdentifierGenerator {

    private static final String[] SALT_CHARS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "G", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};//Alphabet&Digit
    private static final int LENGTH = 6;

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object entity) throws HibernateException {
        String entityClassName = entity.getClass().getSimpleName();
        String prefix = entityClassName.toLowerCase() + "-";
        String generatedId = prefix + randomSalt(LENGTH);
        log.info("Generate id [{}] for given entity [{}]", generatedId, entityClassName);
        return generatedId;
    }

    private String randomSalt(int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALT_CHARS.length);
            salt.append(SALT_CHARS[index]);
        }
        return salt.toString();
    }

}