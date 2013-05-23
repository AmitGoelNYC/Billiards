package com.example.helloworldall;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;
import android.annotation.SuppressLint;
import android.util.DisplayMetrics;

public class MainActivity extends BaseGameActivity {
	private Camera mCamera;
	private final int CAMERA_WIDTH = 800;
	private final int CAMERA_HEIGHT = 480;
    
	// ====================================================
	// ACCESS TO SHARED RESOURCES
	// ====================================================
	
	// The following list of Strings are keys within the Shared Preferences.
	public static final String SHARED_PREFS_MAIN = "PoolHallLegendsSettings";
	public static final String SHARED_PREFS_MUSIC_MUTED = "mute.music";
	public static final String SHARED_PREFS_SOUNDS_MUTED = "mute.sounds";
	public static final String SHARED_PREFS_TABLE_COLOR = "table.color";
	
	// ====================================================
	// HANDLE THE BACK BUTTON
	// ====================================================
	@Override
	public void onBackPressed() {
		// If the resource manager has been setup...
		if(ResourceManager.getInstance().engine!=null){
			// if a layer is shown...
			if(SceneManager.getInstance().isLayerShown)
				// hide the layer.
				SceneManager.getInstance().currentLayer.onHideLayer();
			// or if a game level is shown...
			else if(SceneManager.getInstance().mCurrentScene.getClass().equals(GameLevel.class)) {
				// unload the level and go back to the Main Menu.
				//((GameLevel)SceneManager.getInstance().mCurrentScene).disposeLevel();
				SceneManager.getInstance().showMainMenu();
			}
			// or if the Main Menu is already shown...
			else
				System.exit(0);
		}
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,ScreenOrientation.LANDSCAPE_FIXED, 
				new FillResolutionPolicy(), this.mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		ResourceManager.getInstance().setup(this,this.getEngine(), this.getApplicationContext(), 
				CAMERA_WIDTH,CAMERA_HEIGHT,0,0);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		mEngine.registerUpdateHandler(new FPSLogger());
		ResourceManager.loadMenuResources();
		SceneManager.getInstance().showScene(new SplashScreens());
        pOnCreateSceneCallback.onCreateSceneFinished(mEngine.getScene());
    }

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	// Methods to write/read Integers in the Shared Preferences.
	@SuppressLint("NewApi")
	public static int writeIntToSharedPreferences(final String pStr, final int pValue) {
		// The apply() method requires API level 9 in the manifest.
		ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).edit().putInt(pStr, pValue).apply();
		return pValue;
	}
	public static int getIntFromSharedPreferences(final String pStr) {
		return ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).getInt(pStr, 0);
	}
	
	// Methods to write/read Booleans in the Shared Preferences
	@SuppressLint("NewApi")
	public static void writeBooleanToSharedPreferences(final String pStr, final boolean pValue) {
		// The apply() method requires API level 9 in the manifest.
		ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).edit().putBoolean(pStr, pValue).apply();
	}
	public static boolean getBooleanFromSharedPreferences(final String pStr) {
		return ResourceManager.getActivity().getSharedPreferences(SHARED_PREFS_MAIN, 0).getBoolean(pStr, false);
	}

	public synchronized void onResumeGame(){
		super.onResumeGame();
		/*if(ResourceManager.mMusic != null && !ResourceManager.mMusic.isPlaying() && mSplashFinished){
			ResourceManager.mMusic.play();
		}*/
	}
	public synchronized void onPauseGame(){
		super.onPauseGame();
		/*if(ResourceManager.mMusic != null && ResourceManager.mMusic.isPlaying()){
			ResourceManager.mMusic.pause();
		}*/
	}
}