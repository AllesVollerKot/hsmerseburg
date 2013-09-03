package datenUndHelfer;

import com.example.hsmerseburg.Activity_MainActivity.OnListItemClickListener;

public interface MultiInterface extends OnListItemClickListener{

	void itemClicked(int position, String fragment, String url);

}
