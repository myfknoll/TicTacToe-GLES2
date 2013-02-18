package com.myfknoll.tictactoe;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class MenuActivity extends LayoutGameActivity implements IAccelerationListener,IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 480;

    protected static final int MENU_1PLAYER = 0;
    protected static final int MENU_2PLAYER = 1;
    protected static final int MENU_MORE = 2;
    protected static final int MENU_QUIT = 3;


	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	// ===========================================================
	// Fields
	// ===========================================================

	private ITextureRegion mCircleTextureRegion;
	private ITextureRegion mCrossTextureRegion;

	private BitmapTextureAtlas mCircleTexture;
	private BitmapTextureAtlas mCrossTexture;

	private BitmapTextureAtlas mMenuTexture;
    protected ITextureRegion mMenu1PTextureRegion;
    protected ITextureRegion mMenu2PTextureRegion;
    protected ITextureRegion mMenuMoreTextureRegion;
    protected ITextureRegion mMenuQuitTextureRegion;


    protected Camera mCamera;


	private MenuScene mScene;

	private PhysicsWorld mPhysicsWorld;
	private int mFaceCount = 0;


	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================


	@Override
	public EngineOptions onCreateEngineOptions() {
		 this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

         EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
         options.getAudioOptions().setNeedsSound(true);

         return options;
	}


	@Override
	public void onCreateResources(
			final OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// TODO Auto-generated method stub


		/* Textures. */

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		mCircleTexture =new BitmapTextureAtlas(getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mCrossTexture =new BitmapTextureAtlas(getTextureManager(),128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mCircleTextureRegion=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCircleTexture, this, "circle.png", 0, 0);
		mCrossTextureRegion=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCrossTexture, this, "cross.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(mCircleTexture);
		this.mEngine.getTextureManager().loadTexture(mCrossTexture);


		//Load menu icons
	    this.mMenuTexture = new BitmapTextureAtlas(getTextureManager(),512,512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mMenu1PTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_1player.png", 0, 0);
        this.mMenu2PTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_2player.png", 0, (int) mMenu1PTextureRegion.getHeight());
        this.mMenuMoreTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_more.png", 0, (int) (mMenu2PTextureRegion.getHeight()+mMenu1PTextureRegion.getHeight()));
        this.mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "menu_quit.png", 0, (int) (mMenuMoreTextureRegion.getHeight() +mMenu2PTextureRegion.getHeight()+mMenu1PTextureRegion.getHeight()));
        this.mEngine.getTextureManager().loadTexture(this.mMenuTexture);

        pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		// TODO Auto-generated method stub


		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new MenuScene(this.mCamera);
		this.mScene.setBackground(new Background(0, 0, 0));

		//Create the Background for the menu
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 0,getVertexBufferObjectManager());
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 0,getVertexBufferObjectManager());
		final Rectangle left = new Rectangle(0, 0, 0, CAMERA_HEIGHT,getVertexBufferObjectManager());
		final Rectangle right = new Rectangle(CAMERA_WIDTH , 0, 0, CAMERA_HEIGHT,getVertexBufferObjectManager());

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

        this.mScene.attachChild(ground);
        this.mScene.attachChild(roof);
        this.mScene.attachChild(left);
        this.mScene.attachChild(right);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		addBrick(0,0);
		addBrick(CAMERA_WIDTH/2-mCircleTexture.getWidth()/2,0);
		addBrick(CAMERA_WIDTH-mCircleTexture.getWidth(),0);

		addBrick(0,CAMERA_HEIGHT/2);
		addBrick(CAMERA_WIDTH/2-mCircleTexture.getWidth()/2,CAMERA_HEIGHT/2);
		addBrick(CAMERA_WIDTH-mCircleTexture.getWidth(),CAMERA_HEIGHT/2);

		addBrick(0,CAMERA_HEIGHT-mCircleTexture.getHeight());
		addBrick(CAMERA_WIDTH/2-mCircleTexture.getWidth()/2,CAMERA_HEIGHT-mCircleTexture.getHeight());
		addBrick(CAMERA_WIDTH-mCircleTexture.getWidth(),CAMERA_HEIGHT-mCircleTexture.getHeight());


		//Create the Menu
        SpriteMenuItem onePlayerMenuItem = new SpriteMenuItem(MENU_1PLAYER, this.mMenu1PTextureRegion,getVertexBufferObjectManager());
        onePlayerMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScene.addMenuItem(onePlayerMenuItem);

        SpriteMenuItem towPlayerMenuItem = new SpriteMenuItem(MENU_2PLAYER, this.mMenu2PTextureRegion,getVertexBufferObjectManager());
        towPlayerMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScene.addMenuItem(towPlayerMenuItem);

        SpriteMenuItem MoreMenuItem = new SpriteMenuItem(MENU_MORE, this.mMenuMoreTextureRegion,getVertexBufferObjectManager());
        MoreMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScene.addMenuItem(MoreMenuItem);

        SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mMenuQuitTextureRegion,getVertexBufferObjectManager());
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mScene.addMenuItem(quitMenuItem);

        this.mScene.buildAnimations();
        this.mScene.setOnMenuItemClickListener(this);

        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        enableAccelerationSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        disableAccelerationSensor();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.menu;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.menu_rendersurfaceview;
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem,
            final float pMenuItemLocalX, final float pMenuItemLocalY) {
        switch (pMenuItem.getID()) {
        case MENU_1PLAYER:

            Intent intent = new Intent(this, OnePlayerActivity.class);
            startActivity(intent);

            return true;
        case MENU_2PLAYER:

            intent = new Intent(this, TwoPlayerActivity.class);
            startActivity(intent);
            return true;

        case MENU_MORE:

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=pub:myfknoll"));
            startActivity(intent);

            return true;
        case MENU_QUIT:
            /* End Activity. */
            this.finish();
            return true;
        default:
            return false;
        }
    }

	// ===========================================================
	// Methods
	// ===========================================================

    private void addBrick(final float pX, final float pY) {
        this.mFaceCount++;
        Debug.d("Faces: " + this.mFaceCount);

        final Sprite brick;
        final Body body;

        if (this.mFaceCount % 2 == 0) {
            brick = new Sprite(pX, pY, this.mCircleTextureRegion, getVertexBufferObjectManager());
            body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, brick, BodyType.DynamicBody, FIXTURE_DEF);
		} else {
			brick = new Sprite(pX, pY, this.mCrossTextureRegion,getVertexBufferObjectManager());
			body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, brick, BodyType.DynamicBody, FIXTURE_DEF);
		}

        this.mScene.attachChild(brick);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(brick, body, true, true));
    }

    @Override
    public void onPopulateScene(final Scene pScene, final OnPopulateSceneCallback pOnPopulateSceneCallback)
            throws Exception {

        pOnPopulateSceneCallback.onPopulateSceneFinished();

    }

    @Override
    public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccelerationChanged(final AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
