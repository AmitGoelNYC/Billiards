package com.example.helloworldall;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;

public abstract class ManagedGameScene extends ManagedScene {
	// Create an easy to manage HUD that we can attach/detach when the game scene is shown or hidden.
	public HUD GameHud = new HUD();
	public ManagedGameScene thisManagedGameScene = this;
	
	public ManagedGameScene() {
		// Let the Scene Manager know that we want to show a Loading Scene for at least 2 seconds.
		this(2f);
	};
	
	public ManagedGameScene(float pLoadingScreenMinimumSecondsShown) {
		super(pLoadingScreenMinimumSecondsShown);
		// Setup the touch attributes for the Game Scenes.
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		// Scale the Game Scenes according to the Camera's scale factor.
		//this.setScale(ResourceManager.getInstance().cameraScaleFactorX, ResourceManager.getInstance().cameraScaleFactorY);
//		this.setPosition(0, ResourceManager.getInstance().cameraHeight/2f);
		this.setPosition(0, 0);
		GameHud.setScaleCenter(0f, 0f);
		GameHud.setScale(2f,2f);
	}
	
	// These objects will make up our loading scene.
	private Text LoadingText;
	private Scene LoadingScene;
	
	@Override
	public Scene onLoadingScreenLoadAndShown() {
		// Setup and return the loading screen.
		LoadingScene = new Scene();
		LoadingScene.setBackgroundEnabled(true);
		LoadingText = new Text(0,0,ResourceManager.mGameFontLarge,"Loading...",
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		LoadingText.setPosition((ResourceManager.getInstance().cameraWidth ) /2, 
				(ResourceManager.getInstance().cameraHeight)/2);
		LoadingScene.attachChild(LoadingText);
		return LoadingScene;
	}

	@Override
	public void onLoadingScreenUnloadAndHidden() {
		// detach the loading screen resources.
		LoadingText.detachSelf();
		LoadingText = null;
		LoadingScene = null;
	}
	
	@Override
	public void onLoadScene() {
		// Load the resources to be used in the Game Scenes.
		ResourceManager.loadGameResources();
		
		
		/*Text MainMenuButtonText = new Text(0,0,ResourceManager.mMenuFont,"MENU",ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		MainMenuButtonText.setAnchorCenter(0, 0);
		GameHud.attachChild(MainMenuButtonText);*/
		/*// Create a Sprite to use as the background.
		this.attachChild(new Sprite(0,0,ResourceManager.gameBackgroundTextureRegion,ResourceManager.getInstance().engine.getVertexBufferObjectManager()));
		this.getLastChild().setScaleCenter(0f,0f);
		this.getLastChild().setScaleX(800f);*/
		
		/*// Setup the HUD Buttons and Button Texts.
		// Take note of what happens when the buttons are clicked.
		ButtonSprite MainMenuButton = new ButtonSprite(0f,0f, 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1), 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		MainMenuButton.setScale(1/ResourceManager.getInstance().cameraScaleFactorX, 1/ResourceManager.getInstance().cameraScaleFactorY);
		MainMenuButton.setPosition((MainMenuButton.getWidth()*MainMenuButton.getScaleX())/2f, (MainMenuButton.getHeight()*MainMenuButton.getScaleY())/2f);
		MainMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Main Menu.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().showMainMenu();
			}});
		
		Text MainMenuButtonText = new Text(MainMenuButton.getWidth()/2,MainMenuButton.getHeight()/2,ResourceManager.fontDefault32Bold,"MENU",ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		MainMenuButton.attachChild(MainMenuButtonText);
		
		ButtonSprite OptionsButton = new ButtonSprite(0f,0f, 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(0), 
				ResourceManager.buttonTiledTextureRegion.getTextureRegion(1), 
				ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		OptionsButton.setScale(1/ResourceManager.getInstance().cameraScaleFactorX, 1/ResourceManager.getInstance().cameraScaleFactorY);
		OptionsButton.setPosition(800f-((OptionsButton.getWidth()*OptionsButton.getScaleX())/2f), (OptionsButton.getHeight()*OptionsButton.getScaleY())/2f);
		OptionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// Play the click sound and show the Options Layer.
				ResourceManager.clickSound.play();
				SceneManager.getInstance().showOptionsLayer(true);
			}});
		
		Text OptionsButtonText = new Text(0,0,ResourceManager.fontDefault32Bold,"OPTIONS",ResourceManager.getInstance().engine.getVertexBufferObjectManager());
		OptionsButtonText.setPosition((OptionsButton.getWidth())/2, (OptionsButton.getHeight())/2);
		OptionsButton.attachChild(OptionsButtonText);*/
	}
	
	@Override
	public void onShowScene() {
		// We want to wait to set the HUD until the scene is shown because otherwise it will appear on top of the loading screen.
		ResourceManager.getInstance().engine.getCamera().setHUD(GameHud);
	}
	
	@Override
	public void onHideScene() {
		ResourceManager.getInstance().engine.getCamera().setHUD(null);
	}
	
	@Override
	public void onUnloadScene() {
		// detach and unload the scene.
		ResourceManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisManagedGameScene.detachChildren();
				thisManagedGameScene.clearEntityModifiers();
				thisManagedGameScene.clearTouchAreas();
				thisManagedGameScene.clearUpdateHandlers();
			}});
	}
}