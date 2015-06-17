package com.example.scar2.Logic;

import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

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



  //Creates a random list of 'n' words
  public static String[] makeRandomWordfile(int n)
  {
      HashSet<String> wordList = new HashSet<String>(n);  //create a HashSet of size n

      //AssetManager manager = context.getAssests(); //Next two lines are for opening a file on android instead of the line right below it
      //Scanner file = new Scanner( new FileInputStream( manager.open("dictionary.txt") ) );

      Scanner file = new Scanner( new FileInputStream("dictionary.txt") );  //for opening a file on PC
      ArrayList<String> dictionary = new ArrayList<String>();
      while( file.hasNext() ) //read in every word of the dictionary into dictionary ArrayList
      {
         dictionary.add( file.nextLine() );
      }

      Random PRNG = new Random();
      int currentIndex;
      while( wordList.size() < n )  //keep going through words of the dictionary until wordList is at least n large
      {
          currentIndex = PRNG.nextInt( n );  //get the next int from PRNG between 0 and n
          if( !wordList.contains( dictionary.get( currentIndex ) ) )  //if wordList does not already include the randomly selected word, add it
          {
              wordList.add( dictionary.get( currentIndex ) );
          }
      }

      return wordList.toArray( new String[ wordList.size() ] ); //return wordList as an a String array
  }
}
