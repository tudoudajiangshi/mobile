package cn.nipc.mobiletool.networktrafficmonitor;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.nipc.mobiletool.R;

public class PreferenceHead extends Preference{
	
	private OnClickListener onBackButtonClickListener;

	public PreferenceHead(Context context) {
		super(context);
		setLayoutResource(R.layout.preference_head);
	}
	@Override
	protected void onBindView(View view) 
	{
		super.onBindView(view);
		ImageView iv =  (ImageView) view.findViewById(R.id.net_monitor_back);
		iv.setClickable(true);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(onBackButtonClickListener != null){
					onBackButtonClickListener.onClick(v);
				}		
			}
		});
	}
	public void setOnBackButtonClickListener(OnClickListener onClickListener) {
		this.onBackButtonClickListener = onClickListener;
	}


}
