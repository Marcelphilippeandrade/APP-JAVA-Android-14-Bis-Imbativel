package br.com.marcelphilippe.bis14vs100meteoros.screens;


import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import br.com.marcelphilippe.bis14vs100meteoros.Button.MenuButtons;
import br.com.marcelphilippe.bis14vs100meteoros.config.Assets;
import br.com.marcelphilippe.bis14vs100meteoros.screens.BackgroundScreen;

import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenHeight;
import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenResolution;
import static br.com.marcelphilippe.bis14vs100meteoros.config.DeviceSettings.screenWidth;


public class TitleScreen extends CCLayer {

    private BackgroundScreen background;

    public CCScene scene() {
        CCScene scene = CCScene.node();
        scene.addChild(this);
        return scene;
    }

    public TitleScreen() {
        this.background = new BackgroundScreen(Assets.BACKGROUND);
        this.background.setPosition(screenResolution(CGPoint.ccp(
                screenWidth()/2.0f,
                screenHeight()/2.0f
                )));
        this.addChild(this.background);

        CCSprite title = CCSprite.sprite(Assets.LOGO);
        title.setPosition(screenResolution(
                CGPoint.ccp( screenWidth() /2 , screenHeight() - 130 )));
        this.addChild(title);

        MenuButtons menuLayer = new MenuButtons();
        this.addChild(menuLayer);
    }
}
