package event;

import java.io.Serializable;

public class EventItem implements Serializable, Comparable<EventItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1134151487097547912L;
	private String veranstaltungTitel;
	private String veranstaltungLink;
	private String veranstaltungOrt;
	private String veranstaltungDatumBeginn;
	private String veranstaltungDatumEnde;
	private String veranstaltungZeitBeginn;
	private String veranstaltungZeitEnde;
	private int veranstaltungsId;

	public EventItem() {
		veranstaltungTitel = "";
		veranstaltungLink = "";
		veranstaltungOrt = "";
		veranstaltungDatumBeginn = "";
		veranstaltungDatumEnde = "";
		veranstaltungZeitBeginn = "";
		veranstaltungZeitEnde = "";
		veranstaltungsId = 0;
	}

	public EventItem(int id, String titel, String ort, String link, String beginnzeit, String endzeit, String beginndatum, String enddatum) {
		veranstaltungTitel = titel;
		veranstaltungLink = link;
		veranstaltungOrt = ort;
		veranstaltungZeitBeginn = beginnzeit;
		veranstaltungZeitEnde = endzeit;
		veranstaltungDatumBeginn = beginndatum;
		veranstaltungDatumEnde = enddatum;
		veranstaltungsId = id;
	}

	public String getVeranstaltungTitel() {
		return veranstaltungTitel;
	}

	public void setVeranstaltungTitel(String veranstaltungTitel) {
		this.veranstaltungTitel = veranstaltungTitel;
	}

	public int getId() {
		return veranstaltungsId;
	}

	public void setId(int id) {
		this.veranstaltungsId = id;
	}

	@Override
	public String toString() {
		return veranstaltungTitel;
	}

	public String getVeranstaltungLink() {
		return veranstaltungLink;
	}

	public void setVeranstaltungLink(String veranstaltungLink) {
		this.veranstaltungLink = veranstaltungLink;
	}

	public String getVeranstaltungOrt() {
		return veranstaltungOrt;
	}

	public void setVeranstaltungOrt(String veranstaltungOrt) {
		this.veranstaltungOrt = veranstaltungOrt;
	}

	public String getVeranstaltungDatumBeginn() {
		return veranstaltungDatumBeginn;
	}

	public void setVeranstaltungDatumBeginn(String veranstaltungDatumBeginn) {
		this.veranstaltungDatumBeginn = veranstaltungDatumBeginn;
	}

	public String getVeranstaltungDatumEnde() {
		return veranstaltungDatumEnde;
	}

	public void setVeranstaltungDatumEnde(String veranstaltungDatumEnde) {
		this.veranstaltungDatumEnde = veranstaltungDatumEnde;
	}

	public String getVeranstaltungZeitBeginn() {
		return veranstaltungZeitBeginn;
	}

	public void setVeranstaltungZeitBeginn(String veranstaltungZeitBeginn) {
		this.veranstaltungZeitBeginn = veranstaltungZeitBeginn;
	}

	public String getVeranstaltungZeitEnde() {
		return veranstaltungZeitEnde;
	}

	public void setVeranstaltungZeitEnde(String veranstaltungZeitEnde) {
		this.veranstaltungZeitEnde = veranstaltungZeitEnde;
	}

	@Override
	public int compareTo(EventItem another) {
		int result = 0;

		if (veranstaltungsId < another.getId()) {
			result = -1;
		} else {
			if (veranstaltungsId > another.getId()) {
				result = +1;
			}
		}
		return result;
	}

}