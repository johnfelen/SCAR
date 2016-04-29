package com.scar.android;

//Used for Editing/Viewing Fragments for Server adding/editing
public interface StoreFrag {
    int getType();
    String getLabel();
    String getHostName();
    String getPort();
    byte[] getUsername();
    byte[] getPassword();

    void setLabel(String a);
    void setHost(String a);
    void setPort(String a);
    void setUsername(byte[] a);
    void setPassword(byte[] a);
}
