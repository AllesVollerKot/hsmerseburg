package aenderungen;

public class Aenderungsitem  implements Comparable<Aenderungsitem>{
	private String newsTitle;
	private String newsLink;
	private String newsText;
	private int id;
	
	public Aenderungsitem() {
		newsTitle = "";
		newsLink = "";
		newsText = "";
		id = 0;
	}
	public Aenderungsitem(String titel, String text, String link) {
		newsTitle = titel;
		newsLink = link;
		newsText = text;
		id = 0;
	}
	
	public Aenderungsitem(int id, String titel, String text, String link) {
		newsTitle = titel;
		newsLink = link;
		newsText = text;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return newsTitle;
	}

	public void setTitle(String title) {
		newsTitle = title;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public String getNewsLink() {
		return newsLink;
	}

	public String getNewsText() {
		return newsText;
	}

	public void setLink(String link) {
		newsLink = link;
	}

	public void setText(String text) {
		newsText = text;
	}
	@Override
	public int compareTo(Aenderungsitem another) {
		int result = 0;
		
		if(id<another.getId()){
			result = -1;
		}
		else{
			if(id>another.id){
				result = +1;
			}
		}		
		return result;
	}
}