package event;

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

public class ArrayAdapter_EventAdapter extends ArrayAdapter<EventItem> {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<EventItem> events;

	public ArrayAdapter_EventAdapter(Context context, ArrayList<EventItem> events) {
		super(context, 0, events);
		this.context = context;
		this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.events = events;
	}

	private class Holder {
		public TextView textview;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EventItem item = this.events.get(position);
		final Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item, null);
			holder = new Holder();
			holder.textview = (TextView) convertView.findViewById(R.id.list_item_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}	
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
		holder.textview.setTypeface(tf);
		holder.textview.setText(item.getVeranstaltungTitel());

		Animation animation = null;
		animation = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
		animation.setDuration(500);
		convertView.startAnimation(animation);
		animation = null;
		return convertView;
	}
}
