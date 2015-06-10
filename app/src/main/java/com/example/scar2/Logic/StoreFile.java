package com.example.scar2.Logic;

public class StoreFile {
    private byte[] data;
    private byte[] wordFile;
    private String
        fn,
        password;
    private int
        buffer,
        k,
        n;

    public StoreFile(byte[] data, byte[] wordFile, String fn, String password, int buffer, int k, int n) {
        this.data = data;
        this.wordFile = wordFile;
        this.fn = fn;
        this.password = password;
        this.buffer = buffer;
        this.k = k;
        this.n = n;
    }


    // Prepare data for RS, Apply RS, Apply Encryption, Store blocks properly
    public void store() {

    }
}
