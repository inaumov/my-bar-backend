package mybar.api.bar;

import java.util.EnumSet;

/**
 * Represents mostly used units of measurement in cocktails cooking.
 */
public enum Measurement {

    // related to liquids
    ML("milliliter"),
    OZ("ounce"),
    DROP("drop"),
    DASH("dash"),
    TSP("teaspoon"),

    // related to solid components
    G("gram"),
    PCS("pieces");

    private String fullName;

    Measurement(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static EnumSet<Measurement> liquids() {
        return EnumSet.range(ML, TSP);
    }

    public static EnumSet<Measurement> solidComponents() {
        return EnumSet.of(G, PCS);
    }

}