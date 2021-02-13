package mybar.domain;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.hamcrest.MatcherAssert.assertThat;

public class EntityIdGeneratorTest {

    @Test
    public void testGenerateId() throws Exception {

        EntityIdGenerator entityIdGenerator = new EntityIdGenerator();
        Serializable generate = entityIdGenerator.generate(null, new Object());
        String cocktailId = generate.toString();
        assertThat(cocktailId, Matchers.startsWith("object-"));
        assertThat(cocktailId.length(), Matchers.equalTo("object-".length() + 6));
    }

}