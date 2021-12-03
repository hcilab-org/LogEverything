package org.hcilab.projects.logeverything.adapter;

import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.db.SensorDatabaseHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class SensorAdapter extends CursorAdapter  {

	private final String TAG = getClass().getName();
	
	private final Activity m_context;
	
	private final String COLUMN_ID;
	private final String COLUMN_NAME;
	private final String COLUMN_ISSELECTED;
	private final String COLUMN_SETTINGSSTATE;
	private final String COLUMN_ISAVAILABLE;
	static class ViewHolder {
		int id;
		TextView text;
		Button settings;
		Switch aSwitch;
		ImageView ivGreen;
		ImageView ivRed;
	}
	
	public SensorAdapter (Activity pContext, Cursor pCursor)
	{
		super(pContext, pCursor, true);
		this.m_context = pContext;
		COLUMN_ID = m_context.getResources().getString(R.string.dbColumnId);
		COLUMN_NAME = m_context.getResources().getString(R.string.dbColumnName);
		COLUMN_ISAVAILABLE = m_context.getResources().getString(R.string.dbColumnIsAvailable);
		COLUMN_ISSELECTED = m_context.getResources().getString(R.string.dbColumnIsEnabled);
		COLUMN_SETTINGSSTATE = m_context.getResources().getString(R.string.dbColumnSettingsState);
		//COLUMN_SETTINGS = m_context.getResources().getString(R.string.dbColumnSettings);
	}

	@Override
	public void bindView(View pConvertView, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) pConvertView.getTag();	
	    holder.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
		holder.text.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
 
		if (cursor.getInt(cursor.getColumnIndex(COLUMN_ISAVAILABLE)) == 0)
		{
	    	holder.text.setTextColor(Color.GRAY);
	    	holder.aSwitch.setEnabled(false);
	    	holder.aSwitch.setChecked(false);
	    	holder.settings.setEnabled(false);
				
		}
		else
		{
			holder.text.setTextColor(Color.BLACK);
	    	holder.aSwitch.setEnabled(true);
	    	holder.settings.setEnabled(true);
	    	
	        if (cursor.getInt(cursor.getColumnIndex(COLUMN_ISSELECTED)) == 1)
	        	holder.aSwitch.setChecked(true);
	        else
	        	holder.aSwitch.setChecked(false);
	        
	        
	        if (cursor.getInt(cursor.getColumnIndex(COLUMN_SETTINGSSTATE)) == 0)
	        	holder.settings.setVisibility(View.INVISIBLE);
	        else
	        {
	        	holder.settings.setVisibility(View.VISIBLE);
	        }
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.abstract_sensor_layout, parent, false);
        
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.text = retView.findViewById(R.id.label);
		viewHolder.settings = retView.findViewById(R.id.buttonSettings);
		viewHolder.aSwitch = retView.findViewById(R.id.check);
		viewHolder.ivGreen = retView.findViewById(R.id.imageviewGreen);
		viewHolder.ivGreen.setVisibility(View.INVISIBLE);
		viewHolder.ivRed = retView.findViewById(R.id.imageviewRed);
		viewHolder.ivRed.setVisibility(View.INVISIBLE);
		
		viewHolder.aSwitch.setOnCheckedChangeListener(
			new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {					
					 if (0 == (new SensorDatabaseHelper(m_context)).updateIsChecked(viewHolder.id, buttonView.isChecked()))
						 Log.e(TAG, "Error update DB");
				}
			}
		);
		
		retView.setTag(viewHolder);
		
        return retView;
	}

	
	 /*@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
	    View view = null;
	    if (convertView == null) {
			LayoutInflater inflator = m_context.getLayoutInflater();
			view = inflator.inflate(R.layout.abstract_sensor_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.settings = (Button) view.findViewById(R.id.buttonSettings);
			
			viewHolder.checkbox.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						AbstractSensor s = (AbstractSensor) viewHolder.checkbox.getTag();
						s.setSelected(buttonView.isChecked());
											
					}
				}
			);
			
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
	    } else {
	    	view = convertView;
	    	((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
	    }
	    ViewHolder holder = (ViewHolder) view.getTag();
	    holder.text.setText(list.get(position).getSensorName());
	    if (list.get(position).isAvailable(m_context))
	    {
	    	holder.text.setTextColor(Color.BLACK);
	    	holder.checkbox.setEnabled(true);
	    	holder.checkbox.setChecked(list.get(position).isSelected());
	    }
	    else
	    {
	    	holder.text.setTextColor(Color.GRAY);
	    	holder.checkbox.setEnabled(false);
	    	holder.checkbox.setChecked(false);
	    }
	    
	    if (list.get(position).getSettingsView(this.m_context) == null)
	    {
	    	holder.settings.setVisibility(View.INVISIBLE);
	    }
	    else
	    {
	    	holder.settings.setVisibility(View.VISIBLE);
	    	holder.settings.setText(R.string.settings);
	    	holder.settings.setTag(list.get(position));
	    	holder.settings.setOnClickListener(new View.OnClickListener() {

	    	    @Override
	    	    public void onClick(View v) {
	    	        if(v.getTag() != null)
	    	        {
	    	        	AbstractSensor s = (AbstractSensor)v.getTag();
	    	        	View settings = s.getSettingsView(m_context);
	    				if (settings != null) {
	    					AlertDialog.Builder alertDialog = new AlertDialog.Builder(m_context);
	    					alertDialog.setView(settings);
	    					alertDialog.setTitle(s.getSensorName());
	    					alertDialog.setPositiveButton(R.string.sensor_settings_save, null);
	    					alertDialog.setNegativeButton(R.string.sensor_settings_cancel, null);
	    					alertDialog.show();
	    				}
	    	        }                
	    	    }
	    	});
	    }
	    
	    return view;
	  }*/
}
