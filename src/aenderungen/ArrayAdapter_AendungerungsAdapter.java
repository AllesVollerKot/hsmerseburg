package aenderungen;

import java.util.ArrayList;

import com.example.hsmerseburg.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapter_AendungerungsAdapter extends ArrayAdapter<Aenderungsitem> {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<Aenderungsitem> news;
	

	public ArrayAdapter_AendungerungsAdapter(Context context, ArrayList<Aenderungsitem> news) {
		super(context, 0, news);
		this.context = context;
		this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.news = news;
	}

	private class Holder {
		public TextView textview;
	}
	
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Aenderungsitem item = this.news.get(position);
		final Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item, null);
			holder = new Holder();
			holder.textview = (TextView) convertView.findViewById(R.id.list_item_text);	
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
		holder.textview.setTypeface(tf);
		holder.textview.setText(item.getNewsTitle());
		
		Animation animation = null;
		animation = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
		animation.setDuration(500);
		convertView.startAnimation(animation);
		animation = null;
		return convertView;
	}
}
