package com.example.scar2.Logic;

public class RS {

    //Only used for decoding to give indices for blocks given
    //These would be created during the process of retrieving data
    //from servers based on their position in the HashChain
    public class Block {
        public byte[] data;
        public int ind;

        public Block(byte[] d, int i) {
            data = d;
            ind = i;
        }
    }

    public byte[][] encode(byte[] data, int k, int n) { return null; }
    public byte[] decode(Block[] chunks, int k) { return null; }
}
