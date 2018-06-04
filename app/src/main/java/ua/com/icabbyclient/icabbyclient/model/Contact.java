package ua.com.icabbyclient.icabbyclient.model;

import java.io.Serializable;

public class Contact implements Serializable{
    private String mFirstName;
    private String mLastName;
    private String mNumberPhone;

    public Contact(final String firstName, final String lastName, final String numberPhone) {
        mFirstName = firstName;
        mLastName = lastName;
        mNumberPhone = numberPhone;

    }
}
