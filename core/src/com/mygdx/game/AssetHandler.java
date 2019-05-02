package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetHandler {
    public final AssetManager manager = new AssetManager();

    // textures
    public final String guiButtonsPath = "skin/guiButtons.png";
    public final String statusBarPath = "statusBar.png";
    public final String fadePath = "fade.png";
    public final String ammoStandardPath = "ammo-standard.png";
    public final String ammoSpreadPath = "ammo-spread.png";
    public final String ammoLaserPath = "ammo-laser.png";
    public final String ammoAirstrikePath = "ammo-airstrike.png";
    public final String bulletPath = "bullet.png";
    public final String bulletSpreadPath = "bullet-spread.png";
    public final String bulletLaserPath = "bullet-laser.png";
    public final String bulletMissilePath = "bullet-missile.png";
    public final String tankPath = "tank.png";
    public final String barrelPath = "barrel.png";
    public final String airstrikePath = "airstrike.png";
    public final String forestLevelPath = "forest_level.png";
    public final String snowLevelPath = "snow_level.png";
    public final String desertLevelPath = "desert_level.png";
    public final String bgPath = "bg.png";
    public final String hostPath = "host.png";
    public final String connectPath = "connect.png";
    public final String optionsPath = "options.png";
    public final String optionsTitlePath = "optionTitle.png";
    public final String tutorialPath = "tutorialBtn.png";
    public final String backPath = "back.png";
    public final String upBtnPath = "upBtn.png";
    public final String downBtnPath = "downBtn.png";
    public final String leftBtnPath = "leftBtn.png";
    public final String rightBtnPath = "rightBtn.png";
    public final String leftAmmoBtnPath = "leftAmmoBtn.png";
    public final String rightAmmoBtnPath = "rightAmmoBtn.png";
    public final String muteBtnPath = "muteBtn.png";
    public final String unmuteBtnPath = "unmuteBtn.png";
    public final String volumeOnBtnPath = "volumeOnTextBtn.png";
    public final String volumeOffBtnPath = "volumeOffTextBtn.png";
    public final String homeBtnPath = "homeBtn.png";

    // background textures
    public final String desertSkyPath = "backgrounds/desert/sky.png";
    public final String desertRocks1Path = "backgrounds/desert/rocks_1.png";
    public final String desertRocks2Path = "backgrounds/desert/rocks_2.png";
    public final String desertClouds1Path = "backgrounds/desert/clouds_1.png";
    public final String desertClouds2Path = "backgrounds/desert/clouds_2.png";
    public final String desertClouds3Path = "backgrounds/desert/clouds_3.png";
    public final String forestSkyPath = "backgrounds/forest/sky.png";
    public final String forestRocks1Path = "backgrounds/forest/rocks_1.png";
    public final String forestRocks2Path = "backgrounds/forest/rocks_2.png";
    public final String forestClouds1Path = "backgrounds/forest/clouds_1.png";
    public final String forestClouds2Path = "backgrounds/forest/clouds_2.png";
    public final String forestClouds3Path = "backgrounds/forest/clouds_3.png";

    // TODO: use texture atlas for some textures to short down this list
    public final String textureAtlasPath = "output/game.atlas";


    // music
    public final String musicPath = "sounds/level1.ogg";

    // sound effects
    public final String explosionSoundPath = "sounds/explosion-7.mp3";

    // particle effects
    public final String explosionEffectPath = "effects/explosion.p";
    public final String smokeTrailPath = "effects/smoke_trail.p";
    public final String laserTrailPath = "effects/laser_p.p";

    // fonts
    public final String fontPath = "fonts/arial.ttf";

    // skins
    public final String skinJsonPath = "skin/uiskin.json";
    public final String skinAtlasPath = "skin/uiskin.atlas";


    public AssetHandler() {

    }

    public void loadAssets() {
        loadTextures();
        loadMusic();
        loadSoundEffects();
        loadParticleEffects();
        loadSkins();
        loadFonts();
        //manager.finishLoading();
    }

    public void loadTextures() {
        manager.load(textureAtlasPath, TextureAtlas.class);
        manager.load(statusBarPath, Texture.class);
        manager.load(guiButtonsPath, Texture.class);
        manager.load(fadePath, Texture.class);
        manager.load(ammoStandardPath, Texture.class);
        manager.load(ammoSpreadPath, Texture.class);
        manager.load(ammoLaserPath, Texture.class);
        manager.load(ammoAirstrikePath, Texture.class);
        manager.load(bulletPath, Texture.class);
        manager.load(bulletSpreadPath, Texture.class);
        manager.load(bulletLaserPath, Texture.class);
        manager.load(bulletMissilePath, Texture.class);
        manager.load(tankPath, Texture.class);
        manager.load(barrelPath, Texture.class);
        manager.load(airstrikePath, Texture.class);
        manager.load(forestLevelPath, Texture.class);
        manager.load(snowLevelPath, Texture.class);
        manager.load(desertLevelPath, Texture.class);
        manager.load(hostPath, Texture.class);
        manager.load(connectPath, Texture.class);
        manager.load(optionsPath, Texture.class);
        manager.load(optionsTitlePath, Texture.class);
        manager.load(tutorialPath, Texture.class);
        manager.load(backPath, Texture.class);
        manager.load(bgPath, Texture.class);
        manager.load(upBtnPath, Texture.class);
        manager.load(downBtnPath, Texture.class);
        manager.load(leftBtnPath, Texture.class);
        manager.load(rightBtnPath, Texture.class);
        manager.load(leftAmmoBtnPath, Texture.class);
        manager.load(rightAmmoBtnPath, Texture.class);
        manager.load(muteBtnPath, Texture.class);
        manager.load(unmuteBtnPath, Texture.class);
        manager.load(homeBtnPath, Texture.class);
        manager.load(volumeOnBtnPath, Texture.class);
        manager.load(volumeOffBtnPath, Texture.class);
        manager.load(desertSkyPath, Texture.class);
        manager.load(desertRocks1Path, Texture.class);
        manager.load(desertRocks2Path, Texture.class);
        manager.load(desertClouds1Path, Texture.class);
        manager.load(desertClouds2Path, Texture.class);
        manager.load(desertClouds3Path, Texture.class);
        manager.load(forestSkyPath, Texture.class);
        manager.load(forestRocks1Path, Texture.class);
        manager.load(forestRocks2Path, Texture.class);
        manager.load(forestClouds1Path, Texture.class);
        manager.load(forestClouds2Path, Texture.class);
        manager.load(forestClouds3Path, Texture.class);
    }

    public void loadMusic() {
        manager.load(musicPath, Music.class);
    }

    public void loadSoundEffects() {
        manager.load(explosionSoundPath, Sound.class);
    }

    public void loadParticleEffects() {
        manager.load(explosionEffectPath, ParticleEffect.class);
        manager.load(smokeTrailPath, ParticleEffect.class);
        manager.load(laserTrailPath, ParticleEffect.class);
    }

    public void loadFonts() {
        // set loader type
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        // set params and load font
        FreetypeFontLoader.FreeTypeFontLoaderParameter font = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font.fontFileName = "fonts/arial.ttf";
        font.fontParameters.size = 40;
        font.fontParameters.shadowColor = Color.BLACK;
        font.fontParameters.shadowOffsetX = 3;
        font.fontParameters.shadowOffsetY = 3;
        manager.load(fontPath, BitmapFont.class, font);
    }

    public void loadSkins() {
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter(skinAtlasPath);
        manager.load(skinJsonPath, Skin.class, params);
    }

    public void dispose() {
        manager.dispose();
    }
}
