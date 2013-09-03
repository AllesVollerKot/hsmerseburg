package smk;

import aenderungen.Aenderungsitem;

public class SMKAenderung extends Aenderungsitem {
	private String text2;

	public SMKAenderung() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SMKAenderung(int id, String titel, String text, String link, String text2) {
		super(id, titel, text, link);
		this.text2 = text2;
	}

	public SMKAenderung(String titel, String text, String link, String text2) {
		super(titel, text, link);
		this.text2 = text2;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

}
