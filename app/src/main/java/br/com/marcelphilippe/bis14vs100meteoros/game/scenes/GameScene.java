package br.com.marcelphilippe.bis14vs100meteoros.game.scenes;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import br.com.marcelphilippe.bis14vs100meteoros.Button.GameButtons;
import br.com.marcelphilippe.bis14vs100meteoros.R;
import br.com.marcelphilippe.bis14vs100meteoros.config.Assets;
import br.com.marcelphilippe.bis14vs100meteoros.control.Runner;
import br.com.marcelphilippe.bis14vs100meteoros.engines.MeteorsEngine;
import br.com.marcelphilippe.bis14vs100meteoros.interfaces.MeteorsEngineDelegate;
import br.com.marcelphilippe.bis14vs100meteoros.interfaces.PauseDelegate;
import br.com.marcelphilippe.bis14vs100meteoros.interfaces.ShootEngineDelegate;
import br.com.marcelphilippe.bis14vs100meteoros.objects.Meteor;
import br.com.marcelphilippe.bis14vs100meteoros.objects.Player;
import br.com.marcelphilippe.bis14vs100meteoros.objects.Score;
import br.com.marcelphilippe.bis14vs100meteoros.objects.Shoot;
import br.com.marcelphilippe.bis14vs100meteoros.screens.FinalScreen;
import br.com.marcelphilippe.bis14vs100meteoros.screens.BackgroundScreen;
import br.com.marcelphilippe.bis14vs100meteoros.screens.GameOverScreen;
import br.com.marcelphilippe.bis14vs100meteoros.screens.PauseScreen;
import br.com.marcelphilippe.bis14vs100meteoros.screens.TitleScreen;
import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenHeight;
import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenResolution;
import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenWidth;

public class GameScene extends CCLayer implements MeteorsEngineDelegate, ShootEngineDelegate, PauseDelegate {

    private BackgroundScreen background;
    private MeteorsEngine meteorsEngine;
    private CCLayer meteorsLayer;
    private List meteorsArray;
    private CCLayer playerLayer;
    private Player player;
    private CCLayer shootsLayer;
    private ArrayList shootsArray;
    private List playersArray;
    private CCLayer scoreLayer;
    private Score score;
    private PauseScreen pauseScreen;
    private CCLayer layerTop;

    private GameScene() {
        this.background = new BackgroundScreen(Assets.BACKGROUND);
        this.background.setPosition(screenResolution(CGPoint.ccp(screenWidth() / 2.0f, screenHeight() / 2.0f)));
        this.addChild(this.background);

        this.meteorsLayer = CCLayer.node();
        this.addChild(this.meteorsLayer);

        this.playerLayer = CCLayer.node();
        this.addChild(this.playerLayer);

        this.shootsLayer = CCLayer.node();
        this.addChild(this.shootsLayer);
        this.setIsTouchEnabled(true);

        this.scoreLayer = CCLayer.node();
        this.addChild(this.scoreLayer);

        this.layerTop = CCLayer.node();
        this.addChild(this.layerTop);

        GameButtons gameButtonsLayer = GameButtons.gameButtons();
        gameButtonsLayer.setDelegate(this);
        this.addChild(gameButtonsLayer);

        this.addGameObjects();
        preloadCache();
        SoundEngine.sharedEngine().playSound(CCDirector.sharedDirector().getActivity(), R.raw.music, true);
    }

    public static CCScene createGame() {
        CCScene scene = CCScene.node();
        GameScene layer = new GameScene();
        scene.addChild(layer);
        return scene;
    }

    @Override
    public void createMeteor(Meteor meteor, float x, float y, float vel, double ang, int vl) {
        this.meteorsLayer.addChild(meteor);
        meteor.start();
        this.meteorsArray.add(meteor);
    }

    @Override
    public void createShoot(Shoot shoot) {
        this.shootsLayer.addChild(shoot);
        shoot.setDelegate(this);
        shoot.start();
        this.shootsArray.add(shoot);
    }

    @Override
    public void removeShoot(Shoot shoot) {
        this.shootsArray.remove(shoot);
    }

    @Override
    public void removeMeteor(Meteor meteor) {
        this.meteorsArray.remove(meteor);
    }

    public void createMeteor(Meteor meteor){
        meteor.setDelegate(this);
        this.meteorsLayer.addChild(meteor);
        meteor.start();
        this.meteorsArray.add(meteor);
    }

    private void addGameObjects() {

        // Meteoros
        this.meteorsArray = new ArrayList();
        this.meteorsEngine = new MeteorsEngine();

        // player
        this.player = new Player();
        this.playerLayer.addChild(this.player);

        this.shootsArray = new ArrayList();
        this.player.setDelegate(this);

        this.playersArray = new ArrayList();
        this.playersArray.add(this.player);

        // placar
        this.score = new Score();
        this.score.setDelegate(this);
        this.scoreLayer.addChild(this.score);
    }

    public void onEnter() {
        super.onEnter();

        // Configura o status do jogo
        Runner.check().setIsGamePlaying(true);
        Runner.check().setIsGamePaused(false);

        // pause
        SoundEngine.sharedEngine().setEffectsVolume(1f);
        SoundEngine.sharedEngine().setSoundVolume(1f);

        this.schedule("checkHits");
        this.startEngines();
        this.startGame();
    }

    private void startEngines() {
        this.addChild(this.meteorsEngine);
        this.meteorsEngine.setDelegate(this);
    }

    public boolean shoot(){
        player.shoot();
        return true;
    }

    public void moveLeft() {
        player.moveLeft();
    }

    public void moveRight() {
        player.moveRight();
    }

    public CGRect getBoarders(CCSprite object){
        CGRect rect = object.getBoundingBox();
        CGPoint GLpoint = rect.origin;
        CGRect GLrect = CGRect.make(GLpoint.x, GLpoint.y, rect.size.width, rect.size.height);
        return GLrect;
    }

    private boolean checkRadiusHitsOfArray(List<? extends CCSprite> array1, List<? extends CCSprite> array2, GameScene gameScene, String hit) {
        boolean result = false;

        for (int i = 0; i < array1.size(); i++) {
            // Pega objeto do primeiro array
            CGRect rect1 = getBoarders(array1.get(i));

            for (int j = 0; j < array2.size(); j++) {
                // Pega objeto do segundo array
                CGRect rect2 = getBoarders(array2.get(j));

                // Verifica colisão
                if (CGRect.intersects(rect1, rect2)) {
                    System.out.println("Colision Detected: " + hit);
                    result = true;

                    Method method;

                    try {
                        method = GameScene.class.getMethod(hit, CCSprite.class, CCSprite.class);
                        method.invoke(gameScene, array1.get(i), array2.get(j));
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public void checkHits(float dt) {
        this.checkRadiusHitsOfArray(this.meteorsArray, this.shootsArray, this, "meteoroHit");

        this.checkRadiusHitsOfArray(this.meteorsArray, this.playersArray, this, "playerHit");
    }

    public void meteoroHit(CCSprite meteor, CCSprite shoot) {
        ((Meteor) meteor).shooted();
        ((Shoot) shoot).explode();
        this.score.increase();
    }

    public void playerHit(CCSprite meteor, CCSprite player){
        ((Meteor) meteor).shooted();
        ((Player) player).explode();
        CCDirector.sharedDirector().replaceScene(new GameOverScreen().scene());
    }

    private void preloadCache() {
        SoundEngine.sharedEngine().preloadEffect(CCDirector.sharedDirector().getActivity(), R.raw.shoot);
        SoundEngine.sharedEngine().preloadEffect(CCDirector.sharedDirector().getActivity(), R.raw.bang);
        SoundEngine.sharedEngine().preloadEffect(CCDirector.sharedDirector().getActivity(), R.raw.over);
    }

    public void startGame() {
        // Captura o acelerometro
        player.catchAccelerometer();
    }

    public void startFinalScreen() {
        CCDirector.sharedDirector().replaceScene(new FinalScreen().scene());
    }

    @Override
    public void resumeGame() {

        if (Runner.check().isGamePaused() || !Runner.check().isGamePlaying()) {
            // Continua o jogo
            this.pauseScreen = null;
            Runner.setIsGamePaused(false);
            this.setIsTouchEnabled(true);
            SoundEngine.sharedEngine().playSound(CCDirector.sharedDirector().getActivity(), R.raw.music, true);
        }
    }

    @Override
    public void quitGame() {
        SoundEngine.sharedEngine().setEffectsVolume(0f);
        SoundEngine.sharedEngine().setSoundVolume(0f);

        CCDirector.sharedDirector().replaceScene(new TitleScreen().scene());
    }

    @Override
    public void pauseGameAndShowLayer() {
        if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {
            this.pauseGame();
        }

        if (Runner.check().isGamePaused() && Runner.check().isGamePlaying() && this.pauseScreen == null) {
            this.pauseScreen = new PauseScreen();
            this.layerTop.addChild(this.pauseScreen);
            this.pauseScreen.setDelegate(this);
        }
    }

    private void pauseGame() {
        if (!Runner.check().isGamePaused() && Runner.check().isGamePlaying()) {
            SoundEngine.sharedEngine().pauseSound();
            Runner.setIsGamePaused(true);
        }
    }
}
