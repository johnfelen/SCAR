package com.scar.android;

//Used for Editing/Viewing Fragments for Server adding/editing

/**
 * Interface to capture all information about a server for displaying
 * and modification/addition processes
 */
public interface StoreFrag {
    int getType();
    String getLabel();
    String getHost();
    String getPort();
    byte[] getUsername();
    byte[] getPassword();

    void setLabel(String a);
    void setHost(String a);
    void setPort(String a);
    void setUsername(byte[] a);
    void setPassword(byte[] a);
}
