package com.example.scar2.Logic;

import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import java.security.*;
import javax.crypto.*;
import org.spongycastle.*;  //remove for PC
import javax.crypto.Cipher; //remove for PC
//import org.bouncycastle.jce.provider.BouncyCastleProvider; add this for the PC, also if the bouncy castle jar is in the same directory to compile javac -cp *;(for all jars) *.java

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



    public static String[] makeRandomWordfile(int n)  throws NoSuchAlgorithmException   //Creates a random list of 'n' words
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

        wordList = hashTheWords( wordList );  //get the wordList in all hashed form

        return wordList.toArray( new String[ wordList.size() ] ); //return wordList as an a String array
    }

    /*This static statement must be put into any file using spongy castle crypto so that Android knows to use the spongy castle jars*/
    static
    {
       Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static HashSet<String> hashTheWords( HashSet<String> unHashedWordList ) throws NoSuchAlgorithmException //hashes each part of the hashlist to provide addition security from choosing smaller length words
    {
        //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());   Remove the static portion above this and add something(possibly a constructor) that has this line of code for PC
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");   //used to hash each word
        HashSet<String> wordList = new HashSet<String>( unHashedWordList.size() );  //created a new wordList that will hold all the hashed words

        for( String readyToBeHashed: unHashedWordList )
        {
            sha256.update(readyToBeHashed.getBytes());   //perform the hash
            String check = new String( sha256.digest() );

            while( wordList.contains( check ) ) //checks to make sure the hashed word is not already in the wordList, and will keep rehashing until that is the case NOTE: I do know that there is a very slim chance, however there is still a chance
            {
                sha256.update( check.getBytes() );  //rehash
                check = new String( sha256.digest() );
            }

            wordList.add( new String( sha256.digest() ) );  //add the hash of the word to the new all hash value wordList
        }

      return wordList;
    }
}
