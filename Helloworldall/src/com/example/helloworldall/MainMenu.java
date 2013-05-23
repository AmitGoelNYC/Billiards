package com.example.helloworldall;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

public class MainMenu extends ManagedMenuScene {
	
	private static final MainMenu INSTANCE = new MainMenu();
	public static MainMenu getInstance(){
		return INSTANCE;
	}
	
	public MainMenu() {
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	// No loading screen means no reason to use the following methods.
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		return null;
	}
	@Override
	public void onLoadingScreenUnloadAndHidden() {
	}
	
	// The objects that will make up our Main Menu
	private Sprite BackgroundSprite;
	private Sprite BilliardRack; 
	private ButtonSprite PlayButton;
	private ButtonSprite OptionsButton;
//	private Sprite[] CloudSprites;
	private Text TitleText;
	private Text PlayButtonText;
	private Text OptionsButtonText;
	
	@Override
	public void onLoadScene() {
		// Load the menu resources
		ResourceManager.loadMenuResources();
		
		SFXManager.playMusic();
		if(MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_MUSIC_MUTED)>0)
			SFXManager.setMusicMuted(true);
		// If the sound effects are muted in the settings, mute them in the game.
		if(MainActivity.getIntFromSharedPreferences(MainActivity.SHARED_PREFS_SOUNDS_MUTED)>0)
			SFXManager.setSoundMuted(true);
		
		// Create the background
		BackgroundSprite = new Sprite(0,ResourceManager.getInstance().cameraHeight/2f,ResourceManager.mMenuBackgroundTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		BackgroundSprite.setScaleX(ResourceManager.getInstance().cameraWidth);
		//BackgroundSprite.setScaleY(ResourceManager.getInstance().cameraHeight/480f);
		BackgroundSprite.setZIndex(-5000);
		this.attachChild(BackgroundSprite);
		
		/*// Create clouds that move from one side of the screen to the other, and repeat.
		CloudSprites = new Sprite[20];
		for(Sprite curCloudSprite: CloudSprites){
			curCloudSprite = new Sprite(
					MathUtils.random(-(ResourceManager.getInstance().cameraWidth*this.getScaleX())/2,ResourceManager.getInstance().cameraWidth+(ResourceManager.getInstance().cameraWidth*this.getScaleX())/2),
					MathUtils.random(-(ResourceManager.getInstance().cameraWidth*this.getScaleY())/2,ResourceManager.getInstance().cameraHeight + (ResourceManager.getInstance().cameraHeight*this.getScaleY())/2),
					ResourceManager.cloudTextureRegion,
					ResourceManager.getInstance().engine.getVertexBufferObjectManager()) {
				private float XSpeed = MathUtils.random(0.2f, 2f);
				private boolean initialized = false;
				@Override
				protected void onManagedUpdate(final float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if(!initialized) {
						initialized = true;
						this.setScale(XSpeed/2);
						this.setZIndex(-4000+Math.round(XSpeed*1000f));
						MainMenu.getInstance().sortChildren();
					}
					if(this.getX()<-(this.getWidth()*this.getScaleX())/2) {
						XSpeed = MathUtils.random(0.2f, 2f);
						this.setScale(XSpeed/2);
						this.setPosition(ResourceManager.getInstance().cameraWidth+(this.getWidth()*this.getScaleX())/2, MathUtils.random(-(this.getHeight()*this.getScaleY())/2,ResourceManager.getInstance().cameraHeight + (this.getHeight()*this.getScaleY())/2));
						
						this.setZIndex(-4000+Math.round(XSpeed*1000f));
						MainMenu.getInstance().sortChildren();
					}
					this.setPosition(this.getX()-(XSpeed*(pSecondsElapsed/0.016666f)), this.getY());
				}
			};
			this.attachChild(curCloudSprite);
		}*/
		
		// Create a Play button. Notice that the Game scenes, unlike menus, are not referred to in a static way.
/*		PlayButton = new ButtonSprite(
				(ResourceManager.getInstance().cameraWidth-ResourceManager.mMenuButtonPlay.getWidth())/2f,
				(ResourceManager.getInstance().cameraHeight-ResourceManager.mMenuButtonPlay.getHeight())*(1f/3f), 
				ResourceManager.mMenuButtonPlay,ResourceManager.getInstance().engine.getVertexBufferObjectManager());*/
		PlayButton = new ButtonSprite(ResourceManager.buttonTiledTextureRegion.getWidth()-40f,
				(ResourceManager.getInstance().cameraHeight-ResourceManager.buttonTiledTextureRegion.getHeight())*(1f/5f), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1), 
				ResourceManager.buttonTiledTextureRegion,ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		PlayButtonText = new Text(0, 0, ResourceManager.mMenuFont, "PLAY", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		PlayButtonText.setPosition(PlayButton.getWidth()/2f, PlayButton.getHeight()/2f-2.5f);
		PlayButtonText.setScale(0.75f);
		PlayButton.attachChild(PlayButtonText);
		
		PlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,	float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Create a new GameLevel and show it using the SceneManager. And play a click.
				SceneManager.getInstance().showScene(new GameLevel());
				SFXManager.playClick(1,1,"menuClick");
			}});
		this.registerTouchArea(PlayButton);
		this.attachChild(PlayButton);
		// Create an Option button. Notice that the SceneManager is being told to not pause the scene while the OptionsLayer is open.
		OptionsButton = new ButtonSprite(ResourceManager.getInstance().cameraWidth-ResourceManager.buttonTiledTextureRegion.getWidth()+40f,PlayButton.getY(), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1), 
				ResourceManager.buttonTiledTextureRegion, 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		OptionsButtonText = new Text(0,0,ResourceManager.mMenuFont,"OPTIONS",ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		OptionsButtonText.setPosition(OptionsButton.getWidth()/2, OptionsButton.getHeight()/2-2.5f);
		OptionsButtonText.setScale(0.75f);
		OptionsButton.attachChild(OptionsButtonText);
		this.attachChild(OptionsButton);
		OptionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Show the OptionsLayer and play a click.
				SceneManager.getInstance().showOptionsLayer(false);
				SFXManager.playClick(1,1,"menuClick");
			}});
		this.registerTouchArea(OptionsButton);
		
		// Create a title
		TitleText = new Text(0, 0, ResourceManager.mMenuFont, "Pool Hall Legends", ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		TitleText.setPosition(((ResourceManager.getInstance().cameraWidth))/2 , 
				(ResourceManager.getInstance().cameraHeight)/1.2f);
		TitleText.setColor(0.153f, 0.290f, 0.455f);
		this.attachChild(TitleText);
		

		BilliardRack = new Sprite(ResourceManager.getInstance().cameraWidth/2 , ResourceManager.getInstance().cameraHeight/2,
				ResourceManager.mMenuBilliardsTextureRegion,
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		BilliardRack.setScale(0.7f, 0.7f);
		this.attachChild(BilliardRack);
	}
	
	@Override
	public void onShowScene() {
	}
	@Override
	public void onHideScene() {
	}
	@Override
	public void onUnloadScene() {
	}
}