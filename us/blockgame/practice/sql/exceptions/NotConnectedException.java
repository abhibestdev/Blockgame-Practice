package us.blockgame.practice.sql.exceptions;

@SuppressWarnings("serial")
public class NotConnectedException extends Exception {
	public NotConnectedException(String error) {
		super(error);
	}
}
