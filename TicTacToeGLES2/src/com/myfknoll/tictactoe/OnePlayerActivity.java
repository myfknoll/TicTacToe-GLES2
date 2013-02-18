package com.myfknoll.tictactoe;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.debug.Debug;

public class OnePlayerActivity extends LayoutGameActivity implements IOnSceneTouchListener {
    // ===========================================================
    // Constants
    // ===========================================================

	public static final int CAMERA_WIDTH = 360;
    public static final int CAMERA_HEIGHT = 360;

    /**
     * The squares in order of importance...
     */
    final static int moves[] = {4, 0, 2, 6, 8, 1, 3, 5, 7};

    static final int DONE = (1 << 9) - 1;
    static final int OK = 0;
    static final int WIN = 1;
    static final int LOSE = 2;
    static final int STALEMATE = 3;


    // ===========================================================
    // Fields
    // ===========================================================

    protected Camera mCamera;

    protected Scene mScene;

    protected Sound mBeepSound;
    protected Sound mDingSound;
    protected Sound mReturnSound;
    protected Sound mYahoo1Sound;
    protected Sound mYahoo2Sound;

    protected BitmapTextureAtlas mCircleTextureAtlas;
    protected BitmapTextureAtlas mCrossTextureAtlas;
    protected ITextureRegion mCrossTextureRegion;
    protected ITextureRegion mCircleTextureRegion;


    /**
     * White's current position. The computer is white.
     */
    int white;

    /**
     * Black's current position. The user is black.
     */
    int black;

    /**
     * Who goes first in the next game?
     */
    boolean first = true;


    /**
     * The winning positions.
     */
    static boolean won[] = new boolean[1 << 9];


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
    protected int getLayoutID() {
            return R.layout.game;
    }

    @Override
    protected int getRenderSurfaceViewID() {
            return R.id.onePlayer_rendersurfaceview;
    }

	@Override
	public EngineOptions onCreateEngineOptions() {
		 this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

         EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
         options.getAudioOptions().setNeedsSound(true);
         return options;
	}


    @Override
    public void onCreateResources(
    		final OnCreateResourcesCallback pOnCreateResourcesCallback)
    		throws Exception {


    	   this.mCircleTextureAtlas = new BitmapTextureAtlas(getTextureManager(),128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    	   this.mCrossTextureAtlas = new BitmapTextureAtlas(getTextureManager(),128,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
           BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
           this.mCircleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCircleTextureAtlas, this, "circle.png", 0, 0);
           this.mCrossTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCrossTextureAtlas, this, "cross.png", 0, 0);

           this.mEngine.getTextureManager().loadTexture(mCircleTextureAtlas);
           this.mEngine.getTextureManager().loadTexture(mCrossTextureAtlas);

        SoundFactory.setAssetBasePath("mfx/");
        try {
                this.mBeepSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "beep.ogg");
                this.mDingSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "ding.ogg");
                this.mYahoo1Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "yahoo1.ogg");
                this.mYahoo2Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "yahoo2.ogg");
                this.mReturnSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "return.ogg");

        } catch (final IOException e) {
                Debug.e(e);
        }

        pOnCreateResourcesCallback.onCreateResourcesFinished();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback)
    		throws Exception {

            this.mEngine.registerUpdateHandler(new FPSLogger());

            mScene = new Scene();
            this.mScene.setBackground(new Background(0, 0, 0));

            LoadNewGame();

            mScene.setOnSceneTouchListener(this);

            pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    public void LoadNewGame(){
    	 int xoff = CAMERA_WIDTH / 3;
     	int yoff = CAMERA_HEIGHT / 3;

     	int lineWidth=5;
     	Line line1 = new Line(xoff, 0, xoff, CAMERA_HEIGHT,lineWidth,getVertexBufferObjectManager());
     	Line line2 = new Line(2*xoff, 0, 2*xoff, CAMERA_HEIGHT,lineWidth,getVertexBufferObjectManager());
     	Line line3 = new Line(0, yoff,CAMERA_WIDTH, yoff,lineWidth,getVertexBufferObjectManager());
     	Line line4 = new Line(0, 2*yoff, CAMERA_WIDTH, 2*yoff,lineWidth,getVertexBufferObjectManager());


     	line1.setColor(1,1,1);
     	line2.setColor(1,1,1);
     	line3.setColor(1,1,1);
     	line4.setColor(1,1,1);

         mScene.attachChild(line1);
         mScene.attachChild(line2);
         mScene.attachChild(line3);
         mScene.attachChild(line4);
    }


	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

		if(pSceneTouchEvent.getAction()==TouchEvent.ACTION_DOWN){

		int x = (int) pSceneTouchEvent.getX();
		int y = (int) pSceneTouchEvent.getY();

		switch (status()) {
		  case WIN:
		  case LOSE:
		  case STALEMATE:

			  OnePlayerActivity.this.mReturnSound.play();

			  runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						/* Now it is save to remove the entity! */
						  mScene.detachChildren();
						  LoadNewGame();


						    white = black = 0;
						    if (first) {
						    	int random = (int)(Math.random() * 9);
						    	white |= 1 << random;

								Sprite circle = new Sprite(
										(random%3)*(CAMERA_WIDTH/3)+(CAMERA_WIDTH/3-mCircleTextureRegion.getWidth())/2,
										(random/3)*(CAMERA_HEIGHT/3)+(CAMERA_HEIGHT/3-mCircleTextureRegion.getHeight())/2,
										 mCircleTextureRegion,getVertexBufferObjectManager());
								mScene.attachChild(circle);
						    }
						    first = !first;

					}
				});

			return true;
		}
		// Figure out the row/column
		int c = (x * 3) / CAMERA_WIDTH;
		int r = (y * 3) / CAMERA_HEIGHT;
		if (yourMove(c + r * 3)) {

			Sprite cross = new Sprite(
					c*(CAMERA_WIDTH/3)+(CAMERA_WIDTH/3-mCrossTextureRegion.getWidth())/2,
					r*(CAMERA_HEIGHT/3)+(CAMERA_HEIGHT/3-mCrossTextureRegion.getHeight())/2,
					mCrossTextureRegion,getVertexBufferObjectManager());
			mScene.attachChild(cross);


                switch (status()) {
                case WIN:
                    OnePlayerActivity.this.mYahoo1Sound.play();
                    break;
                case LOSE:
                    OnePlayerActivity.this.mYahoo2Sound.play();
                    break;
                case STALEMATE:
                    break;
                default:
                    if (myMove()) {
                        switch (status()) {
                        case WIN:
                            OnePlayerActivity.this.mYahoo1Sound.play();
                            break;
                        case LOSE:
                            OnePlayerActivity.this.mYahoo2Sound.play();
                            break;
                        case STALEMATE:
                            break;
                        default:
                            OnePlayerActivity.this.mDingSound.play();
                        }
                    } else {
                        OnePlayerActivity.this.mBeepSound.play();
                    }
                }
            } else {
                OnePlayerActivity.this.mBeepSound.play();
            }

            return true;
        } else {
            return false;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================


    /**
     * Mark all positions with these bits set as winning.
     */
    static void isWon(final int pos) {
        for (int i = 0; i < DONE; i++) {
            if ((i & pos) == pos) {
                won[i] = true;
            }
        }
    }

    /**
     * Initialize all winning positions.
     */
    static {
        isWon((1 << 0) | (1 << 1) | (1 << 2));
        isWon((1 << 3) | (1 << 4) | (1 << 5));
        isWon((1 << 6) | (1 << 7) | (1 << 8));
        isWon((1 << 0) | (1 << 3) | (1 << 6));
        isWon((1 << 1) | (1 << 4) | (1 << 7));
        isWon((1 << 2) | (1 << 5) | (1 << 8));
        isWon((1 << 0) | (1 << 4) | (1 << 8));
        isWon((1 << 2) | (1 << 4) | (1 << 6));
    }

    /**
     * User move.
     * @return true if legal
     */
    boolean yourMove(final int m) {
        if ((m < 0) || (m > 8)) {
            return false;
        }
        if (((black | white) & (1 << m)) != 0) {
            return false;
        }
        black |= 1 << m;
        return true;
    }

	/**
	 * Computer move.
	 *
	 * @return true if legal
	 */
	boolean myMove() {
		if ((black | white) == DONE) {
			return false;
		}
		int best = bestMove(white, black);

		Sprite circle = new Sprite(
				(best%3)*(CAMERA_WIDTH/3)+(CAMERA_WIDTH/3-mCircleTextureRegion.getWidth())/2,
				(best/3)*(CAMERA_HEIGHT/3)+(CAMERA_HEIGHT/3-mCircleTextureRegion.getHeight())/2
				,mCircleTextureRegion,getVertexBufferObjectManager());
		mScene.attachChild(circle);


		white |= 1 << best;
		return true;
    }

    int bestMove(final int white, final int black) {
        int bestmove = -1;

        loop: for (int i = 0; i < 9; i++) {
            int mw = moves[i];
            if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
                int pw = white | (1 << mw);
                if (won[pw]) {
                    // white wins, take it!
                    return mw;
                }
                for (int mb = 0; mb < 9; mb++) {
                    if (((pw & (1 << mb)) == 0) && ((black & (1 << mb)) == 0)) {
                        int pb = black | (1 << mb);
                        if (won[pb]) {
                            // black wins, take another
                            continue loop;
                        }
                    }
                }
                // Neither white nor black can win in one move, this will do.
                if (bestmove == -1) {
                    bestmove = mw;
                }
            }
        }
        if (bestmove != -1) {
            return bestmove;
        }

        // No move is totally satisfactory, try the first one that is open
        for (int i = 0; i < 9; i++) {
            int mw = moves[i];
            if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
                return mw;
            }
        }

        // No more moves
        return -1;
    }

    /**
     * Figure what the status of the game is.
     */
    int status() {
        if (won[white]) {
            return WIN;
        }
        if (won[black]) {
            return LOSE;
        }
        if ((black | white) == DONE) {
            return STALEMATE;
        }
        return OK;
    }

    @Override
    public void onPopulateScene(final Scene pScene, final OnPopulateSceneCallback pOnPopulateSceneCallback)
            throws Exception {
        // TODO Auto-generated method stub

        pOnPopulateSceneCallback.onPopulateSceneFinished();

    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}