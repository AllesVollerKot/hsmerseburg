package datenUndHelfer;

import android.content.Intent;

public class MultiHelper {

	public MultiHelper() {

	}
	
	public static Intent sendMessage(String url, String subject){
		String shareBody = "Hast du das schon gesehen? "+url;
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);		
		return sharingIntent;
	}	

	public static int boolToInt(boolean input) {
		if (input) {
			return 1;
		} else {
			return 0;
		}
	}
}
