package com.chiralcode.colorpicker;


import com.james.android.patternpaint.PatternPaintMainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ColorPickerDialogARGB extends AlertDialog {

    private static final int MAX_ALPHA = 255;
	private ColorPicker colorPickerView;
    private SeekBar alphaSeekBarView;
    private TextView alphaBarTitle;
    private final TextView alphaBarValue;
    private int alphaValue;
    private final OnColorSelectedListener onColorSelectedListener;

    public ColorPickerDialogARGB(Context context, int initialColor, OnColorSelectedListener onColorSelectedListener) {
        super(context);

        this.onColorSelectedListener = onColorSelectedListener;
        
        // Color Picker
        // Had to manually set the ID of the View in order to position the Seek Bar with a relative Layout.
        colorPickerView = new ColorPicker(context);
        colorPickerView.setColor(initialColor);
        colorPickerView.setId(999);
        
        // TextView Title
        alphaBarTitle = new TextView(context);
        alphaBarTitle.setText("Alpha: ");       
        alphaBarTitle.setId(1000);
        
        //TextView Alpha Value
        alphaBarValue = new TextView(context);
        alphaValue = Color.alpha(initialColor);
        alphaBarValue.setText(String.valueOf(Color.alpha(initialColor)));
        
        
        // Alpha SeekBar
        alphaSeekBarView = new SeekBar(context);
        alphaSeekBarView.setMax(MAX_ALPHA);
        alphaSeekBarView.setProgress(Color.alpha(initialColor));
        alphaSeekBarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				alphaValue = progress;
				alphaBarValue.setText(String.valueOf(progress));
				setIntSharedPreference(PatternPaintMainActivity.KEY_ALPHA_VALUE, progress);
			}
		});

        // setup layout parameters
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setVerticalScrollBarEnabled(true);
        LayoutParams ColorPickerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams alphaBarParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams alphaBarTitleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams alphaBarValueParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        ColorPickerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        alphaBarTitleParams.addRule(RelativeLayout.BELOW, colorPickerView.getId());
        alphaBarTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        alphaBarValueParams.addRule(RelativeLayout.RIGHT_OF, alphaBarTitle.getId());
        alphaBarValueParams.addRule(RelativeLayout.BELOW, colorPickerView.getId());
        alphaBarParams.addRule(RelativeLayout.BELOW, alphaBarTitle.getId());
        
        colorPickerView.setLayoutParams(ColorPickerLayoutParams);
        alphaBarTitle.setLayoutParams(alphaBarTitleParams);
        alphaBarValue.setLayoutParams(alphaBarValueParams);
        alphaSeekBarView.setLayoutParams(alphaBarParams);

        // add views to Relative Layout
        relativeLayout.addView(colorPickerView);
        relativeLayout.addView(alphaBarTitle);
        relativeLayout.addView(alphaBarValue);
        relativeLayout.addView(alphaSeekBarView);

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), onClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), onClickListener);

        setView(relativeLayout);

    }

    private OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case BUTTON_POSITIVE:
            	int selectedColor = colorPickerView.getColor();
            	Paint colorWithAlpha = new Paint();
            	colorWithAlpha.setColor(selectedColor);
            	colorWithAlpha.setAlpha(alphaValue);
               	onColorSelectedListener.onColorSelected(colorWithAlpha);
                break;
            case BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            }
        }
    };

    public interface OnColorSelectedListener {
        public void onColorSelected(Paint color);
    }
    
    private void setIntSharedPreference(String key, int value) {
		SharedPreferences settings = getContext().getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}


}
