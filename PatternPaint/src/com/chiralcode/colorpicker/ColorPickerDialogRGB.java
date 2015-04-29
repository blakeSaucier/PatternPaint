package com.chiralcode.colorpicker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ColorPickerDialogRGB extends AlertDialog {

	private ColorPicker colorPickerView;
    private final OnColorSelectedListener onColorSelectedListener;

    public ColorPickerDialogRGB(Context context, int initialColor, ColorPickerDialogRGB.OnColorSelectedListener onColorSelectedListener) {
        super(context);

        this.onColorSelectedListener = onColorSelectedListener;
        
        // Color Picker
        // Had to manually set the ID of the View in order to position the Seek Bar with a relative Layout.
        colorPickerView = new ColorPicker(context);
        colorPickerView.setColor(initialColor);
        colorPickerView.setId(999);
      

        // setup layout parameters
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setVerticalScrollBarEnabled(true);
        LayoutParams ColorPickerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        ColorPickerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);        
        colorPickerView.setLayoutParams(ColorPickerLayoutParams);

        // add views to Relative Layout
        relativeLayout.addView(colorPickerView);

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), onClickListener);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), onClickListener);

        setView(relativeLayout);

    }

    private OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case BUTTON_POSITIVE:
            	int selectedColor = colorPickerView.getColor();
            	Paint color = new Paint();
            	color.setColor(selectedColor);
               	onColorSelectedListener.onColorSelected(color);
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
}
