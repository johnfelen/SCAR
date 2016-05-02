/**
 * Generic exception for invalid inputs into LogicLibrary
 */
package scar;

//Used on the front end to help distinguish issues
public class InvalidInputException extends Exception {
  public InvalidInputException(String msg) {
    super(msg);
  }

  public InvalidInputException(String msg, Throwable thr) {
    super(msg, thr);
  }
}
