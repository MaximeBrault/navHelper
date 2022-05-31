package com.adapei.navhelper;

import timber.log.Timber;

/**
 * Small class to create a phone number with an owner
 */
public class PhoneNumber {

    private String number;
    private String name;

    /**
     * Create a new object. number must be specified
     * @param  number the number as a string
     * @param  name   the name of the people who own the number
     */
    public PhoneNumber(String number, String name) {
        if(number != null) {
            this.number = number;
            if(name != null) this.name = name;
            else this.name = "";
        } else {
            Timber.e("PhoneNumber : Null number");
        }
    }

    /**
     * Get the number
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Get the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Display in a nice way the number. Used by the param spinner
     * @return the string "number - name"
     */
    public String toString() {
        return this.number + " - " + this.name;
    }
}
