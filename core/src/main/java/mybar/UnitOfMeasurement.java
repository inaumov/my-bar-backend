package mybar;

import java.util.EnumSet;

/**
 * Units of measurement that are mostly used in cocktails cooking.
 */
public enum UnitOfMeasurement {

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

    UnitOfMeasurement(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static EnumSet<UnitOfMeasurement> liquids() {
        return EnumSet.range(ML, TSP);
    }

    public static EnumSet<UnitOfMeasurement> solidComponents() {
        return EnumSet.of(G, PCS);
    }

}