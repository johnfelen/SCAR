package com.example.scar2.Logic;

public class GetFile {
    private byte[] wordFile;
    private String
        fn,
        password;
    private int
        buffer,
        k,
        n;

    public GetFile(byte[] wordFile, String fn, String password, int buffer, int k, int n) {
        this.wordFile = wordFile;
        this.fn = fn;
        this.password = password;
        this.buffer = buffer;
        this.k = k;
        this.n = n;
    }


    //Get as many blocks from servers, decrypt, apply rs, remove padding
    public void get() {
        Hash hash=new Hash();
        hash.recursiveKey(n, fn, password);
        ArrayList<String> hashArr=hash.getArr();

        int numOfServ=2;
        int x=hashArr.size();
        String[][] matrix=new String[x][x];
    }
}
