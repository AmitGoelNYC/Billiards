package com.tcr.poolhalllegends;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;


public class OptionsLayer extends ManagedLayer{
	
	private static final OptionsLayer INSTANCE = new OptionsLayer();
	public static OptionsLayer getInstance(){
		return INSTANCE;
	}
	private static final String mMusicEnabledText= "Mute Music";
	private static final String mSoundEnabledText= "Mute Sounds";
	private static final String mBackgroundText= "Table Cloth";
	
	private GrowToggleButton SoundEnabledButton;
	private GrowToggleButton MusicEnabledButton;
	private GrowToggleRectangle BackgroundButton;
	
	
	// Animates the layer to slide in from the top.
	IUpdateHandler SlideIn = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if(OptionsLayer.getInstance().getY()>ResourceManager.getInstance().cameraHeight/2f) {
				OptionsLayer.getInstance().setPosition(OptionsLayer.getInstance().getX(), Math.max(OptionsLayer.getInstance().getY()-(3600*(pSecondsElapsed)),ResourceManager.getInstance().cameraHeight/2f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
			}
		}
		/*public void onUpdate(float pSecondsElapsed) {
			if(OptionsLayer.getInstance().getY()>1) {
				if(OptionsLayer.getInstance().getY()>){
					
				}
				OptionsLayer.getInstance().setPosition(OptionsLayer.getInstance().getX(), Math.max(OptionsLayer.getInstance().getY()-(3600*(pSecondsElapsed)),0));
				float  test = OptionsLayer.getInstance().getY()-(3600*(pSecondsElapsed));
				Log.d("yeman","text = " + test);
				Log.d("yeman","psecondsElapsed " + pSecondsElapsed);
				Log.d("yeman","OptionsLayer.getInstance().getY() " + OptionsLayer.getInstance().getY());
			} else {
				
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
			}
		}*/
		@Override public void reset() {}
	};
	
	// Animates the layer to slide out through the top and tell the SceneManager to hide it when it is off-screen;
	IUpdateHandler SlideOut = new IUpdateHandler() {
		@Override
		public void onUpdate(float pSecondsElapsed) {
			if(OptionsLayer.getInstance().getY()<ResourceManager.getInstance().cameraHeight/2f+480f) {
				OptionsLayer.getInstance().setPosition(OptionsLayer.getInstance().getX(), Math.min(OptionsLayer.getInstance().getY()+(3600*(pSecondsElapsed)),ResourceManager.getInstance().cameraHeight/2f+480f));
			} else {
				OptionsLayer.getInstance().unregisterUpdateHandler(this);
				SceneManager.getInstance().hideLayer();
			}
		}
		@Override public void reset() {}
	};
	
	@Override
	public void onLoadLayer() {
		// Create and attach a background that hides the Layer when touched.
		final float BackgroundX = 0f, BackgroundY = 0f;
		final float BackgroundWidth = 760f; 
		final float BackgroundHeight = 440f;
		Rectangle smth = new Rectangle(BackgroundX,BackgroundY,BackgroundWidth,BackgroundHeight,ResourceManager.getInstance().engine.getVertexBufferObjectManager()) ;
		smth.setColor(0f, 0f, 0f, 0.85f);
		this.attachChild(smth);
		this.registerTouchArea(smth);
		
		// Create the OptionsLayerTitle text for the Layer.
		Text OptionsLayerTitle = new Text(0,0,ResourceManager.mMenuFont,"OPTIONS",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		OptionsLayerTitle.setPosition(0f,BackgroundHeight/2f-OptionsLayerTitle.getHeight());
		this.attachChild(OptionsLayerTitle);
		
		// Let the player know how to get out of the blank Options Layer
		Text OptionsLayerSubTitle = new Text(0,0,ResourceManager.mMenuFont,"Tap to return",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				SceneManager.getInstance().currentLayer.onHideLayer();
				SFXManager.playClick(1f, 1f,"menuClick");
                return true;
            }	
		};
			
		OptionsLayerSubTitle.setScale(0.75f);
		OptionsLayerSubTitle.setPosition(0f,
				-BackgroundHeight/2f+OptionsLayerSubTitle.getHeight());
		this.registerTouchArea(OptionsLayerSubTitle);
		this.attachChild(OptionsLayerSubTitle);
		
		Text MusicEnabledText = new Text(0,0,ResourceManager.mStrokeFont, mMusicEnabledText+" :",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		MusicEnabledText.setPosition(-BackgroundWidth*.2f,
				BackgroundHeight*.2f);
		this.attachChild(MusicEnabledText);
		
		this.MusicEnabledButton = new GrowToggleButton(BackgroundWidth*.2f, 
				MusicEnabledText.getY(), ResourceManager.muteTiledTextureRegion, SFXManager.isMusicMuted()) {
			@Override
			public boolean checkState() {
				return !SFXManager.isMusicMuted();
			}
			
			@Override
			public void onClick() {
				SFXManager.toggleMusicMuted();
			}
		};
		
		this.attachChild(this.MusicEnabledButton);
		this.registerTouchArea(this.MusicEnabledButton);
		
		Text SoundEnabledText = new Text(0,0,ResourceManager.mStrokeFont, mSoundEnabledText+" :",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		SoundEnabledText.setPosition(-BackgroundWidth*0.2f+14f,
				MusicEnabledText.getY()-MusicEnabledButton.getHeight()*1.4f);
		this.attachChild(SoundEnabledText);
		
		this.SoundEnabledButton = new GrowToggleButton(BackgroundWidth*.2f, 
				SoundEnabledText.getY()+5f, ResourceManager.muteTiledTextureRegion, SFXManager.isSoundMuted()) {
			@Override
			public boolean checkState() {
				return !SFXManager.isSoundMuted();
			}
			
			@Override
			public void onClick() {
				SFXManager.toggleSoundMuted();
			}
		};
		
		this.attachChild(this.SoundEnabledButton);
		this.registerTouchArea(this.SoundEnabledButton);

		Text BackgroundColorText = new Text(0,0,ResourceManager.mStrokeFont, mBackgroundText+" :",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		BackgroundColorText.setPosition(-BackgroundWidth*0.2f,
				SoundEnabledButton.getY()-SoundEnabledButton.getHeight()*1.4f);
		this.attachChild(BackgroundColorText);
		
		final int color = MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR);
		this.BackgroundButton = new GrowToggleRectangle(BackgroundWidth*.2f, 
				BackgroundColorText.getY(),80,40, color) {
			@Override
			public void onClick() {
				int newColor = MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR) +1;
				if(newColor<=GrowToggleRectangle.getColorLength()-1){
					GrowToggleRectangle.setCurrentColor(newColor);
					MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR,newColor);
				}else{
					GrowToggleRectangle.setCurrentColor(0);
					MainActivity.writeIntToSharedPreferences(MainActivity.SHARED_PREFS_TABLE_COLOR,0);
				}
			}
		};
		this.attachChild(this.BackgroundButton);
		this.registerTouchArea(this.BackgroundButton);
		
		this.setPosition(ResourceManager.getInstance().cameraWidth/2f, ResourceManager.getInstance().cameraHeight/2f+480f);
	}

	@Override
	public void onShowLayer() {
		this.registerUpdateHandler(SlideIn);
	}

	@Override
	public void onHideLayer() {
		this.registerUpdateHandler(SlideOut);
	}
	@Override
	public void onUnloadLayer() {
	}
}