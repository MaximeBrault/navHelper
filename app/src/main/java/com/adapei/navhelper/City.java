package com.adapei.navhelper;

/**
 * Cities used.
 */
public enum City {
    LORIENT ("Lorient"),
    AURAY ("Auray");

    private String name;

    /**
     * Constructor of the enum city.
     * @param name
     */
    City(String name) {
        this.name = name;
    }

    /**
     * Get the name of the city.
     * @return
     */
    public String getName() {
        return this.name;
    }

    @Override
    /**
     * ToString of a city.
     */
    public String toString() {
        return "City{" +
                "name='" + this.name + '\'' +
                '}';
    }

    /**
     * Returns the default city.
     * @return City.LORIENT
     */
    public static City DEFAULT()
    {
        return City.LORIENT;
    }
}
