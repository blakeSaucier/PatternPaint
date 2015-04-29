package com.james.android.patternpaint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chiralcode.colorpicker.ColorPickerDialogARGB;
import com.chiralcode.colorpicker.ColorPickerDialogRGB;
import com.james.android.draganddraw.R;

@SuppressLint("NewApi") public class PatternPaintMainActivity extends Activity {
	
	public static final String CACHED_CANVAS_FILE = "cachedCanvas";
	private static final String APP_FOLDER_NAME = "/PatternPaint/";
	private static final String FRIENDLY_FOLDER_PATH = Environment.getExternalStorageDirectory() + APP_FOLDER_NAME;
	private static DrawingVIew mDrawingView;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private boolean mDoubleBackToExitPressedOnce = false;
	
	//  SharedPreferences
	//  Options values
	public static final int DEFAULT_SPLATTER_SIZE = 2;
	public static final int TREE_PATTERN = 0;
	public static final int PARTICLE_PATTERN = 1;
	public static final int TARGET_PATTERN = 2;
	//public static final int RANDOM_GROWTH_PATTERN = 3;
	public static final int DLA_PATTERN = 3;
	public static final int UNCHOSEN_PATTERN = -1;
	public static final int PIXEL_BOX = 0;
	public static final int PIXEL_CIRCLE = 1;
	public static final int PIXEL_SPLATTER = 2;
	public static final int GRAVITY_UP = 0;
	public static final int GRAVITY_DOWN = 1;
	public static final int GRAVITY_LEFT = 2;
	public static final int GRAVITY_RIGHT = 3;
	
	public static final int DEFAULT_TREE_MAX_ARC_LENGTH = 25;
	public static final int DEFAULT_TREE_ARC_ANGLE = 8;
	public static final float DEFAULT_TREE_SHRINK_RATE = 0.97f;
	public static final int DEFAULT_TREE_START_SIZE = 40;
	public static final int DEFAULT_TREE_END_SIZE = 10;

	
	public static final boolean DEFAULT_CLEAR_SCREEN = false;
	public static final boolean DEFAULT_SAVE_SCREEN = false;
	public static final boolean DEFAULT_PAUSE_SCREEN = false;
	public static final boolean DEFAULT_UNDO_PATTERN = false;
	public static final boolean DEFAULT_PIXEL_OUTLINE = false;
	public static final boolean DEFAULT_READY_TO_CACHE_DRAWING = false;
	public static final boolean DEFAULT_READY_TO_LOAD_CACHED_DRAWING = false;
	public static final float DEFAULT_SEEKER_FORCE = 1f;
	public static final float DEFAULT_SEEKER_MAXSPEED = 20f;

	
	// SharedPreferences Keys
	public static final String KEY_PARTICLE_LENGTH = "particle_length";
	public static final String KEY_SPLATTER_SIZE = "splatter_size";
	public static final String KEY_UNDO_PATTERN = "undo_pattern";
	
	public static final String KEY_TREE_MAX_ARC_LENGTH = "max_arc_length";
	public static final String KEY_TREE_ARC_ANGLE = "arc_angle";
	public static final String KEY_TREE_SHRINK_RATE = "shrink_rate";
	public static final String KEY_TREE_START_SIZE = "start_size";
	public static final String KEY_TREE_END_SIZE = "end_size";
	
	public static final String KEY_SEEKER_FORCE = "seeker_force";
	public static final String KEY_SEEKER_MAXSPEED = "seeker_max_speed";
	
	public static final String KEY_ALPHA_VALUE = "alpha_value";
	public static final String KEY_BACKGROUND_COLOR = "background_color";
	public static final String KEY_PIXEL_COLOR = "box_color";
	public static final String KEY_PIXEL_TYPE = "pixel_type";
	public static final String KEY_PIXEL_OUTLINE = "pixel_outline";
	public static final String KEY_GRAVITY_DIRECTION = "gravity_direction";
	public static final String KEY_CLEAR_SCREEN = "clear_screen";
	public static final String KEY_FLATTEN_CANVAS = "flatten_canvas";
	public static final String READY_TO_CACHE_DRAWING = "cache_drawing";
	public static final String READY_TO_LOAD_CACHED_DRAWING = "load_cached_drawing";
	public static final String KEY_SAVE_SCREEN = "save_screen";
	public static final String KEY_PAUSE_SCREEN = "pause_screen";
	public static final String KEY_PATTERN = "pattern";
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//gravity pop up list
	public enum DRAWER_PATTERN_ITEMS {
		TREE("Tree"),
		PARTICLE("Particle"),
		SEEKER("Seeker"),
		RANDOM_GROWTH("Random Growth"),
		DLA("Diffusion-Limited-Aggregation");
		
		private String name;
		
		private DRAWER_PATTERN_ITEMS(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	private static final String[] DRAWER_ITEMS = {"Tree", "Particle", "Seeker", "Diffusion-Limited Aggregation"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("LIFECYCLE", "On CREATE called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_activity);
		mDrawingView = (DrawingVIew) findViewById(R.id.drawing_view);
		
		setupDrawer();
	}

	private void setupDrawer() {
		//Navigation Drawer setup
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		
		mTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.left_drawer));
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, DRAWER_ITEMS));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(
				this, 
				mDrawerLayout, 
				R.drawable.ic_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close) 
		{

			public void onDrawerClosed(View view) 
			{
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.drawer_open);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// TODO Auto-generated method stub
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}	
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		int patternSetting = getIntSharedPreference(KEY_PATTERN, UNCHOSEN_PATTERN);
		switch (patternSetting) {
			case TREE_PATTERN:
				inflater.inflate(R.menu.tree_menu, menu);
				break;
			case PARTICLE_PATTERN:
				inflater.inflate(R.menu.particle_menu, menu);
				break;
			case TARGET_PATTERN:
				inflater.inflate(R.menu.seeker_menu, menu);
				break;
			case DLA_PATTERN:
				inflater.inflate(R.menu.drag_and_draw, menu);
				break;
			case UNCHOSEN_PATTERN:
				inflater.inflate(R.menu.drag_and_draw, menu);
				break;
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		int pixelTypeSetting = getIntSharedPreference(KEY_PIXEL_TYPE, PIXEL_CIRCLE);

		if (isDrawerOpen) {
			menu.clear();
		} else {
		switch (pixelTypeSetting) {
			case PIXEL_BOX:
				menu.findItem(R.id.splatter_size).setVisible(false);
				break;
			case PIXEL_CIRCLE:
				menu.findItem(R.id.splatter_size).setVisible(false);
				break;
			case PIXEL_SPLATTER:
				menu.findItem(R.id.splatter_size).setVisible(true);
				break;
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		
		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
	}

	private void selectItem(int position) {
		switch (position) {
			case TREE_PATTERN:
				setIntSharedPreference(KEY_PATTERN, TREE_PATTERN);
				break;
			case PARTICLE_PATTERN:
				setIntSharedPreference(KEY_PATTERN, PARTICLE_PATTERN);
				break;
			case TARGET_PATTERN:
				setIntSharedPreference(KEY_PATTERN, TARGET_PATTERN);
				break;
			case DLA_PATTERN:
				setIntSharedPreference(KEY_PATTERN, DLA_PATTERN);
				break;
		}
		mDrawerList.setItemChecked(position, true);
		setTitle(DRAWER_ITEMS[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch(item.getItemId()) {
			case R.id.undo_option:
				Log.i("OPTIONS UNDO", "UNDO PATTERN");
				setBoolSharedPreference(KEY_UNDO_PATTERN, true);
				return true;
			case R.id.pause:
				Log.i("OPTIONS PAUSE", "SCREEN PAUSED");
				setBoolSharedPreference(KEY_PAUSE_SCREEN, true);
				return true;
			case R.id.random_growth:
				Log.i("OPTIONS", "RANDOM GROWTH SETTINGS");
				displayRandomGrowthDialog();
				return true;
			case R.id.seeker_settings:
				Log.i("OPTIONS MENU", "SEEKER SETTINGS");
				displaySeekerOptionsDialog();
				return true;
			case R.id.tree_settings:
				Log.i("OPTIONS MENU", "TREE SETTINGS");
				displayTreeOptionsDialog();
				return true;
			case R.id.gravity:
				Log.i("OPTIONS MENU", "GRAVITY SELECTED");
				displayGravityDialog();
				return true;
			case R.id.pixel:
				Log.i("OPTIONS MENU", "PIXEL SETTINGS");
				displayPixelOptions();
				return true;
			case R.id.splatter_size:
				displaySplatterSizePicker();
				return true;
			case R.id.particle_length:
				displayParticleLengthDialog();
				return true;
			case R.id.color:
				Log.i("OPTIONS MENU", "COLOR SELECTED");
				displayARGBColorPicker(KEY_PIXEL_COLOR);
				return true;
			case R.id.background_color:
				Log.i("OPTIONS MENU", "BACKGROUND COLOR SELECTED");
				displayRGBColorPicker(KEY_BACKGROUND_COLOR);
				return true;
			case R.id.clear_screen:
				Log.i("OPTIONS MENU", "CLEAR FLAG SET");
				displayClearScreenDialog();
				return true;
			case R.id.flatten_canvas:
				Log.i("OPTIONS MENU", "Flattenning screen...");
				displayFlattenCanvasDialog();
				return true;
			case R.id.save_screen:
				Log.i("OPTIONS MENU", "SAVE FLAG SET");
				displaySaveDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	@Override
	protected void onPause() {
		Log.i("LIFECYCLE", "On Pause called");
		if (!mDoubleBackToExitPressedOnce) {
			setBoolSharedPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING, true);

		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i("LIFECYCLE", "On DESTROY called");
		setBoolSharedPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING, false);
		super.onDestroy();
	}

	public static int getDrawingViewHeight() {
		return mDrawingView.getHeight();
	}
	
	public static int getDrawingViewWidth() {
		return mDrawingView.getWidth();
	}
	
	private void displayRandomGrowthDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();		
		final View randomGrowthOptionsView = inflater.inflate(R.layout.random_growth_options, null);
		
		alertDialogBuilder.setView(randomGrowthOptionsView);
		alertDialogBuilder.setTitle(R.string.random_growth_option);
		
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();

	}

	private void displayTreeOptionsDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();		
		final View treeOptionsView = inflater.inflate(R.layout.tree_options, null);
		
		alertDialogBuilder.setView(treeOptionsView);
		alertDialogBuilder.setTitle(R.string.tree_options);
		
		final TextView arcLengthValue = (TextView) treeOptionsView.findViewById(R.id.max_arc_length_value);
		final SeekBar arcLengthSeekBar = (SeekBar) treeOptionsView.findViewById(R.id.max_arc_length_seekbar);
		final TextView arcAngleValue = (TextView) treeOptionsView.findViewById(R.id.arc_angle_value);
		final SeekBar arcAngleSeekBar = (SeekBar) treeOptionsView.findViewById(R.id.arc_angle_seekbar);
		final TextView shrinkRateValue = (TextView) treeOptionsView.findViewById(R.id.shrink_rate_value);
		final SeekBar shrinkRateSeekBar = (SeekBar) treeOptionsView.findViewById(R.id.shrink_rate_seekbar);
		final TextView startSizeValue = (TextView) treeOptionsView.findViewById(R.id.start_size_value);
		final SeekBar startSizeSeekBar = (SeekBar) treeOptionsView.findViewById(R.id.start_size_seekbar);
		final TextView endSizeValue = (TextView) treeOptionsView.findViewById(R.id.end_size_value);
		final SeekBar endSizeSeekBar = (SeekBar) treeOptionsView.findViewById(R.id.end_size_seekbar);
		
		arcLengthValue.setText(String.valueOf(getIntSharedPreference(KEY_TREE_MAX_ARC_LENGTH, DEFAULT_TREE_MAX_ARC_LENGTH)));
		arcLengthSeekBar.setProgress(getIntSharedPreference(KEY_TREE_MAX_ARC_LENGTH, DEFAULT_TREE_MAX_ARC_LENGTH) - 1);
		arcAngleValue.setText(String.valueOf(getIntSharedPreference(KEY_TREE_ARC_ANGLE, DEFAULT_TREE_ARC_ANGLE)));
		arcAngleSeekBar.setProgress(getIntSharedPreference(KEY_TREE_ARC_ANGLE, DEFAULT_TREE_ARC_ANGLE) - 1);
		shrinkRateValue.setText(getShrinkRateValue());
		shrinkRateSeekBar.setProgress(getShrinkRateProgress());
		startSizeValue.setText(String.valueOf(getIntSharedPreference(KEY_TREE_START_SIZE, DEFAULT_TREE_START_SIZE)));
		startSizeSeekBar.setProgress(getStartSize());
		endSizeValue.setText(String.valueOf(getIntSharedPreference(KEY_TREE_END_SIZE, DEFAULT_TREE_END_SIZE)));
		endSizeSeekBar.setProgress(getIntSharedPreference(KEY_TREE_END_SIZE, DEFAULT_TREE_END_SIZE));
		
		arcLengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				arcLengthValue.setText(String.valueOf(progress + 1));
			}
		});
		
		arcAngleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				arcAngleValue.setText(String.valueOf(progress + 1));
			}
		});
		
		shrinkRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				shrinkRateValue.setText(getShrinkRateValueFromSeekBar(progress));
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
		});
		
		startSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				startSizeValue.setText(String.valueOf(progress + 20));
			}
		});
		
		endSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				endSizeValue.setText(String.valueOf(progress));
			}
		});
		
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setIntSharedPreference(KEY_TREE_ARC_ANGLE, arcAngleSeekBar.getProgress() + 1);
				setIntSharedPreference(KEY_TREE_MAX_ARC_LENGTH, arcLengthSeekBar.getProgress() + 1);
				setFloatSharedPreference(KEY_TREE_SHRINK_RATE, getShrinkRateFromSeekBar(shrinkRateSeekBar.getProgress()));
				setIntSharedPreference(KEY_TREE_START_SIZE, startSizeSeekBar.getProgress() + 20);
				setIntSharedPreference(KEY_TREE_END_SIZE, endSizeSeekBar.getProgress());
			}
		});
		
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	private String getShrinkRateValue() {
		float shrinkRate = getShrinkRateValueFromPrefs();
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(shrinkRate);
	}

	private float getShrinkRateValueFromPrefs() {
		return getFloatSharedPreference(KEY_TREE_SHRINK_RATE, DEFAULT_TREE_SHRINK_RATE);
	}

	private int getStartSize() {
		return getIntSharedPreference(KEY_TREE_START_SIZE, DEFAULT_TREE_START_SIZE) - 20;
	}

	private int getShrinkRateProgress() {
		return (int) (getShrinkRateValueFromPrefs() * 100) - 90;
	}

	private String getShrinkRateValueFromSeekBar(int seekBarProgress) {
		DecimalFormat df = new DecimalFormat("#.##");
		float shrinkRate = (seekBarProgress * 0.01f) + 0.90f;
		return df.format(shrinkRate);
	}
	
	private float getShrinkRateFromSeekBar(int seekBarProgress) {
		return (float) ((seekBarProgress * 0.01) + 0.90f);
	}

	private void displayParticleLengthDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();
		final View particleLengthDialog = inflater.inflate(R.layout.particle_dialog, null);
		alertDialogBuilder.setView(particleLengthDialog);
		alertDialogBuilder.setTitle(R.string.particle_length_option);
		
		final SeekBar particleSizeSeekBar = (SeekBar) particleLengthDialog.findViewById(R.id.particle_length_seekbar);
		final TextView particleSizeText = (TextView) particleLengthDialog.findViewById(R.id.particle_length_textview);
		
		int particleLength = getIntSharedPreference(KEY_PARTICLE_LENGTH, 50);
		particleSizeSeekBar.setProgress(particleLength - 1);
		particleSizeText.setText("Particle Length: " + String.valueOf(particleLength));
		
		particleSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
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
				particleSizeText.setText("Particle Length: " + String.valueOf(progress + 1));
			}
		});
		
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setIntSharedPreference(KEY_PARTICLE_LENGTH, particleSizeSeekBar.getProgress() + 1);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		AlertDialog particleAlertDialog = alertDialogBuilder.create();
		particleAlertDialog.show();
		
	}

	private void displayFlattenCanvasDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		alertDialogBuilder.setMessage(R.string.flatten_screen_warning);
		alertDialogBuilder.setPositiveButton("Flatten", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setBoolSharedPreference(KEY_FLATTEN_CANVAS, true);
			}
		});
		
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	private void displaySplatterSizePicker() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();
		final View splatterOptionsAlertDialog = inflater.inflate(R.layout.splatter_dialog, null);
		alertDialogBuilder.setView(splatterOptionsAlertDialog);
		alertDialogBuilder.setTitle(R.string.splatter_size_option);

		final SeekBar splatterSizeSeekBar = (SeekBar) splatterOptionsAlertDialog.findViewById(R.id.splatter_slider);
		final TextView splatterSizeText = (TextView) splatterOptionsAlertDialog.findViewById(R.id.splatter_size_text);
		
		int splatterSize = getIntSharedPreference(KEY_SPLATTER_SIZE, DEFAULT_SPLATTER_SIZE);
		
		splatterSizeSeekBar.setProgress(splatterSize - 1);
		splatterSizeText.setText("Splatter Size: " + String.valueOf(splatterSize));
		
		splatterSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
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
				splatterSizeText.setText("Splatter Size: " + String.valueOf(progress + 1));
			}
		});
		
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setIntSharedPreference(KEY_SPLATTER_SIZE, splatterSizeSeekBar.getProgress() + 1);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog splatterDialog = alertDialogBuilder.create();
		splatterDialog.show();
		
	}

	private void displayClearScreenDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		alertDialogBuilder.setMessage("Clear Screen?");
		alertDialogBuilder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setBoolSharedPreference(KEY_CLEAR_SCREEN, true);
			}
		});
		
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}

	private void displaySeekerOptionsDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();
		final View optionsAlertDialogView = inflater.inflate(R.layout.seeker_options, null);
		
		alertDialogBuilder.setView(optionsAlertDialogView);
		alertDialogBuilder.setTitle(R.string.seeker_option);
		
		// get a reference to the SeekBars and TextViews
		final TextView forceValue = (TextView) optionsAlertDialogView.findViewById(R.id.force_title);
		final TextView maxSpeedValue = (TextView) optionsAlertDialogView.findViewById(R.id.maxSpeed_title);
		final SeekBar forceSeekBar = (SeekBar) optionsAlertDialogView.findViewById(R.id.force_slider);
		final SeekBar maxSpeedSeekBar = (SeekBar) optionsAlertDialogView.findViewById(R.id.maxSpeed_slider);
		
		float seekerForce = getFloatSharedPreference(KEY_SEEKER_FORCE, DEFAULT_SEEKER_FORCE);
		float seekerMaxSpeed = getFloatSharedPreference(KEY_SEEKER_MAXSPEED, DEFAULT_SEEKER_MAXSPEED);
		forceSeekBar.setProgress(convertForceToInt(seekerForce));
		maxSpeedSeekBar.setProgress(convertMaxSpeedToInt(seekerMaxSpeed));
		
		forceValue.setText("Turning Force: " + String.valueOf(seekerForce));
		maxSpeedValue.setText("Max Speed: " + String.valueOf(seekerMaxSpeed));
		
		forceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
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
				forceValue.setText("Turning Force: " + String.valueOf(getForceFloatFromInt(progress)));
			}


		});
		
		maxSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
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
				maxSpeedValue.setText("Max Speed: " + String.valueOf(getMaxSpeedFloatFromInt(progress)));
				
			}
		});
		
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Save Force and maxSpeed to preferences
				setFloatSharedPreference(KEY_SEEKER_FORCE, getForceFloatFromInt(forceSeekBar.getProgress()));
				setFloatSharedPreference(KEY_SEEKER_MAXSPEED, getMaxSpeedFloatFromInt(maxSpeedSeekBar.getProgress()));
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
		
	}
	
	protected float getMaxSpeedFloatFromInt(int progress) {
		float maxSpeed = (progress / 2f);
		return maxSpeed;
	}
	
	private float getForceFloatFromInt(int progress) {
		float force = (progress / 2) / 10f;
		
		return force;
	}
	
	private int convertMaxSpeedToInt(float seekerMaxSpeed) {
		int maxSpeed = (int) (seekerMaxSpeed * 2);
		return maxSpeed;
	}

	private int convertForceToInt(float seekerForce) {
		int force = (int) (seekerForce * 10) * 2;
		return force;
	}
	
	private void displayRGBColorPicker(final String keyBackgroundColor) {
		int initialColor = getIntSharedPreference(keyBackgroundColor, Color.WHITE);
        ColorPickerDialogRGB colorPickerDialog = new ColorPickerDialogRGB(this, initialColor, new ColorPickerDialogRGB.OnColorSelectedListener() {
			
			@Override
			public void onColorSelected(Paint color) {
				setIntSharedPreference(keyBackgroundColor, color.getColor());
				showToast(color.getColor());
			}
		});
        colorPickerDialog.show();
	}

	private void displayARGBColorPicker(final String key) {
		int initialColor = getIntSharedPreference(key, Color.WHITE);
        ColorPickerDialogARGB colorPickerDialog = new ColorPickerDialogARGB(this, initialColor, new ColorPickerDialogARGB.OnColorSelectedListener() {

            @Override
            public void onColorSelected(Paint color) {
            	setIntSharedPreference(key, color.getColor());
                showToast(color.getColor());
            }
        });
        colorPickerDialog.show();
	}
	
    private void showToast(int color) {
        String rgbString = "A: " + Color.alpha(color) + " R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
    }

	private void displayPixelOptions() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		alertDialogBuilder.setTitle(R.string.pixel_option);
		LayoutInflater inflater = getLayoutInflater();
		final View pixelOptionsView = inflater.inflate(R.layout.pixel_options_dialog, null);
		alertDialogBuilder.setView(pixelOptionsView);
		
		final CheckBox pixelOutlineCheckBox = (CheckBox) pixelOptionsView.findViewById(R.id.outline_checkbox);
		final RadioGroup pixelTypeRadioGroup = (RadioGroup) pixelOptionsView.findViewById(R.id.pixel_options_radio);
		
		pixelOutlineCheckBox.setChecked(getBoolSharedPreference(KEY_PIXEL_OUTLINE, DEFAULT_PIXEL_OUTLINE));
		int selectedPixelType = getIntSharedPreference(KEY_PIXEL_TYPE, PIXEL_BOX);
		switch (selectedPixelType) {
			case PIXEL_BOX:
				pixelTypeRadioGroup.check(R.id.radio_box);
				break;
			case PIXEL_CIRCLE:
				pixelTypeRadioGroup.check(R.id.radio_circle);
				break;
			case PIXEL_SPLATTER:
				pixelTypeRadioGroup.check(R.id.radio_splatter);
				break;
			default:
				break;
		}
		
		pixelOutlineCheckBox.setChecked(getBoolSharedPreference(KEY_PIXEL_OUTLINE, DEFAULT_PIXEL_OUTLINE));
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedID = pixelTypeRadioGroup.getCheckedRadioButtonId();
				switch (selectedID) {
					case R.id.radio_box:
						setIntSharedPreference(KEY_PIXEL_TYPE, PIXEL_BOX);
						break;
					case R.id.radio_circle:
						setIntSharedPreference(KEY_PIXEL_TYPE, PIXEL_CIRCLE);
						break;
					case R.id.radio_splatter:
						setIntSharedPreference(KEY_PIXEL_TYPE, PIXEL_SPLATTER);
						break;
					default:
						break;
				}
				setBoolSharedPreference(KEY_PIXEL_OUTLINE, pixelOutlineCheckBox.isChecked());
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});

		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();	
	}
    
	private void displayGravityDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		alertDialogBuilder.setTitle(R.string.gravity_option);
		final View gravityRadioView = getLayoutInflater().inflate(R.layout.gravity_options_dialog, null);
		alertDialogBuilder.setView(gravityRadioView);
		
		final RadioGroup gravityRadioGroup = (RadioGroup) gravityRadioView.findViewById(R.id.gravity_options_radio);
		int gravityChoice = getIntSharedPreference(KEY_GRAVITY_DIRECTION, GRAVITY_DOWN);
		switch (gravityChoice) {
			case GRAVITY_UP:
				gravityRadioGroup.check(R.id.gravity_radio_up);
				break;
			case GRAVITY_DOWN:
				gravityRadioGroup.check(R.id.gravity_radio_down);
				break;
			case GRAVITY_LEFT:
				gravityRadioGroup.check(R.id.gravity_radio_left);
				break;
			case GRAVITY_RIGHT:
				gravityRadioGroup.check(R.id.gravity_radio_right);
				break;
		}
		alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedRadioButton = gravityRadioGroup.getCheckedRadioButtonId();
				switch (selectedRadioButton) {
					case R.id.gravity_radio_up:
						setIntSharedPreference(KEY_GRAVITY_DIRECTION, GRAVITY_UP);
						break;
					case R.id.gravity_radio_down:
						setIntSharedPreference(KEY_GRAVITY_DIRECTION, GRAVITY_DOWN);
						break;
					case R.id.gravity_radio_left:
						setIntSharedPreference(KEY_GRAVITY_DIRECTION, GRAVITY_LEFT);
						break;
					case R.id.gravity_radio_right:
						setIntSharedPreference(KEY_GRAVITY_DIRECTION, GRAVITY_RIGHT);
						break;
				}
			}
		});
		
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});

		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}
	
	private void saveScreen(String fileName, String imageType) {
		createStorageFolder(); 
		File image = new File(storageFolderLocation() + File.separator + fileName + "." + imageType);
		FileOutputStream outStream = null;
		
		try {
			outStream = new FileOutputStream(image);
			if (imageType.equals("png")) {
				mDrawingView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outStream);
			} else if (imageType.equals("jpg")) {
				mDrawingView.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			} else {
				throw new FileNotFoundException("unknown file type");
			}
			mediaScan(image);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeOutputStream(outStream);
		}
	}
	
	public void mediaScan(File file) {
	    MediaScannerConnection.scanFile(this,
	            new String[] { file.getAbsolutePath() }, null,
	            new MediaScannerConnection.OnScanCompletedListener() {
	                @Override
	                public void onScanCompleted(String path, Uri uri) {
	                    Log.i("MediaScanWork", "file " + path
	                            + " was scanned seccessfully: " + uri);
	                }
	            });
	}


	private void createStorageFolder() {
		File folder = new File(storageFolderLocation());
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	private String storageFolderLocation() {
		return Environment.getExternalStorageDirectory().toString() + APP_FOLDER_NAME;
	}
		
	private String buildDefaulFilename() {
		Time currentTime = new Time();
		currentTime.setToNow();		
		return currentTime.format2445();
	}

	private void closeOutputStream(FileOutputStream outStream) {
		if (outStream != null) {
			try {
				outStream.flush();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void displaySaveDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		LayoutInflater inflater = getLayoutInflater();
		final View saveScreenDialog = inflater.inflate(R.layout.save_dialog, null);
		alertDialogBuilder.setView(saveScreenDialog);
		alertDialogBuilder.setTitle("Save Drawing to File");
		
		final TextView filePathView = (TextView) saveScreenDialog.findViewById(R.id.file_path);
		final EditText fileNameView = (EditText) saveScreenDialog.findViewById(R.id.file_name);
		final Spinner imageTypeSpinner = (Spinner) saveScreenDialog.findViewById(R.id.image_type_spinner);	
		
		filePathView.setText(FRIENDLY_FOLDER_PATH);
		fileNameView.setText(buildDefaulFilename());
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, 
				R.array.image_file_type_options, 
				android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		imageTypeSpinner.setAdapter(adapter);
				
		alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//saveScreen(fileNameView.getText().toString());
				String selectedFileType = imageTypeSpinner.getSelectedItem().toString().toLowerCase();
				Log.i("FILENAME", fileNameView.getText().toString() + selectedFileType);
				saveScreen(fileNameView.getText().toString(), selectedFileType);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onBackPressed() {
	    if (mDoubleBackToExitPressedOnce) {
			setBoolSharedPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING, false);
			Log.i("LIFECYCLE", " Quitting with back button");
	        super.onBackPressed();
	        return;
	    }

	    this.mDoubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            mDoubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	} 
	
	private void setIntSharedPreference(String key, int value) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public void setBoolSharedPreference(String key, boolean value) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	private float getFloatSharedPreference(String key, float defaultValue) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		float preference = settings.getFloat(key, defaultValue);
		return preference;
	}
	
	private boolean getBoolSharedPreference(String key, boolean defaultValue) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean preference = settings.getBoolean(key, defaultValue);
		return preference;
	}
	
	private int getIntSharedPreference(String key, int defaultValue) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		int preference = settings.getInt(key, defaultValue);
		return preference;
	}
	
	private void setFloatSharedPreference(String key, float value) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
}