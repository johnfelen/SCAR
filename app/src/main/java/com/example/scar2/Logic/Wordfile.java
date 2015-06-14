package com.example.scar2.Logic;

import java.math.BigInteger;

public class Wordfile {
  private String[] words;
  private boolean[] used;

  public Wordfile(String words[]) {
    this.words = words;
    used = new boolean[words.length];
    for(int i = 0;i<used.length;++i)
      used[i] = false;
  }


  //Converts hash into integer location and then returns the word
  public String get(String hash) {
    BigInteger num = new BigInteger(hash, 16);
    return get(num.mod(words.length).intValue());
  }

  //Tries to get word[i], if not gets next avaiable word
  //Because of how this works the order in which you store the files
  // needs to be the same order that you get the files.
  
  // Basically: Read the hashchain in the same direction everytime.
  public String get(int i) {
    String word = null;
    int oi = i;
    int len = used.length;

    do {
      if(used[i])
        //Keep going till we get an unused word
        i = (i + 1) % len; 
      else {
        word = words[i];
        used[i] = true;
        break;
      }
    } while(oi != i);

    if(word == null)
      throw InvalidInputException("You must have atleast N distinct words in the wordfile");
    return word;
  }
}
