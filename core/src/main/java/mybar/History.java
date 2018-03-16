package mybar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private String name;
    private int stars;
    private String username;

    @Override
    public String toString() {
        return "History [name=" + name + ", stars=" + stars + ", username=" + username + "]";
    }

}