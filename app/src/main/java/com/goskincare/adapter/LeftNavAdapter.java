package com.goskincare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goskincare.R;

/**
 * The Adapter class for the ListView displayed in the left navigation drawer.
 */
public class LeftNavAdapter extends BaseAdapter
{

	/** The items. */
	private String items[] = {"Fast Order!", "Magic Order!", "My Profile", "Order History", "Terms of Use", "Help", "Log out"};
	private String itemInfos[] = {"Place an order, real fast!", "Set a repeat order, like magic!", "", "", "", "", ""};

	/** The context. */
	private Context context;
	/** The selected. */
	private int selected;
	/** The icons. */
	private int icons[] = { R.drawable.ic_fast_order, R.drawable.ic_magic_order,
			R.drawable.ic_profile, R.drawable.ic_history, R.drawable.ic_terms, R.drawable.ic_help };

	private int icons_sel[] = {R.drawable.ic_fast_order_sel, R.drawable.ic_magic_order_sel, R.drawable.ic_profile_sel,
			R.drawable.ic_history_sel, R.drawable.ic_terms_sel, R.drawable.ic_help_sel};

	/**
	 * Setup the current selected position of adapter.
	 * 
	 * @param position
	 *            the new selection
	 */
	public void setSelection(int position)
	{
		selected = position;
		notifyDataSetChanged();
	}

	/**
	 * Instantiates a new left navigation adapter.
	 * 
	 * @param context
	 *            the context of activity
	 * @param items
	 *            the array of items to be displayed on ListView
	 */
	public LeftNavAdapter(Context context, String items[])
	{
		this.context = context;
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return items.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int arg0)
	{
		return items[arg0];
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cell_left_nav_item, null);

		ImageView imgvLogo = (ImageView)convertView.findViewById(R.id.imgvLogo);
		TextView tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
		TextView tvIfo = (TextView)convertView.findViewById(R.id.tvInfo);
		ImageView imgvDividor = (ImageView)convertView.findViewById(R.id.imgvDividor);

		tvTitle.setText(items[position]);
		tvIfo.setText(itemInfos[position]);

		if(position == items.length - 1){
			tvTitle.setTextColor(context.getResources().getColor(R.color.gray_lt));
			imgvLogo.setVisibility(View.INVISIBLE);
			imgvDividor.setVisibility(View.INVISIBLE);
		}else{
			tvTitle.setTextColor(context.getResources().getColor(R.color.black));
			imgvLogo.setVisibility(View.VISIBLE);
			imgvDividor.setVisibility(View.VISIBLE);
			imgvLogo.setImageResource(icons[position]);

			if(position == selected){
				tvTitle.setTextColor(context.getResources().getColor(R.color.tint));
				imgvLogo.setImageResource(icons_sel[position]);
			}
		}

		return convertView;
	}

}
