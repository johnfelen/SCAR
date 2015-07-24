package com.scar.android;

//Used for Editing/Viewing Fragments for Server adding/editing
public interface StoreFrag {
    int getType();
    String getLabel();
    String getHost();
    String getPort();
    String getUsername();
    String getPassword();

    void setLabel(String a);
    void setHost(String a);
    void setPort(String a);
    void setUsername(String a);
    void setPassword(String a);
}
