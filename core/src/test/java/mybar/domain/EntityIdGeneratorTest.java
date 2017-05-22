package mybar.domain;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;

public class EntityIdGeneratorTest {

    @Test
    public void testGenerateId() throws Exception {

        EntityIdGenerator entityIdGenerator = new EntityIdGenerator();
        Serializable generate = entityIdGenerator.generate(null, new Object());
        String cocktailId = generate.toString();
        Assert.assertThat(cocktailId, Matchers.startsWith("object-"));
        Assert.assertThat(cocktailId.length(), Matchers.equalTo("object-".length() + 6));
    }

}