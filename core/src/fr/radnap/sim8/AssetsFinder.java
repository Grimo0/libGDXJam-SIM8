package fr.radnap.sim8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * @author Radnap
 * @see <a href="http://stackoverflow.com/questions/31630244/is-there-any-way-to-load-an-entire-directory-at-once-using-libgdx">Noone's stackoverflow post</a>
 */
public class AssetsFinder {

	private AssetManager assetManager;
	private FileHandleResolver resolver;

	public class AssetDescriptor {
		public String folder;
		public String extension;
		public Class<?> assetType;

		public AssetDescriptor(String folder, String extension, Class<?> assetType) {
			this.folder = folder;
			this.extension = extension;
			this.assetType = assetType;
		}
	}

	private Array<AssetDescriptor> assets = new Array<AssetDescriptor>();


	public AssetsFinder(AssetManager assetManager, FileHandleResolver resolver) {
		this.assetManager = assetManager;
		this.resolver = resolver;

		assets.add(new AssetDescriptor("music", null, Music.class));
		assets.add(new AssetDescriptor("sounds", ".mp3", Sound.class)); // You could remove all but this one (Why ?)
		assets.add(new AssetDescriptor("sounds", ".wav", Sound.class)); // You could remove all but this one (Why ?)
		assets.add(new AssetDescriptor("skin", "skin.json", Skin.class));
		assets.add(new AssetDescriptor("atlas", ".atlas", TextureAtlas.class));
//		assets.add(new AssetDescriptor("texture", null, Texture.class));
		assets.add(new AssetDescriptor("texture", ".jpg", Texture.class));
		assets.add(new AssetDescriptor("texture", ".png", Texture.class));
		assets.add(new AssetDescriptor("fonts", null, BitmapFont.class));
		assets.add(new AssetDescriptor("freetype", ".ttf", FreeTypeFontGenerator.class));
		assets.add(new AssetDescriptor("effect", null, ParticleEffect.class));
		assets.add(new AssetDescriptor("pixmap", null, Pixmap.class));
		assets.add(new AssetDescriptor("region", null, PolygonRegion.class));
		assets.add(new AssetDescriptor("model", null, Model.class));
		assets.add(new AssetDescriptor("level", ".tmx", TiledMap.class));
	}


	public boolean load(String folderName) {
		FileHandle folder = resolver.resolve(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			SIM8.debug("Folder " + folder.path() + " doesn't exist, can't load");
			return false;
		}
		SIM8.debug("Load folder " + folder.path());

		for (AssetDescriptor descriptor : assets) {
			if (descriptor.folder != null) {
				FileHandle descriptorFolder = folder.child(descriptor.folder);
				if (descriptorFolder.exists()) {
					for (FileHandle asset : descriptorFolder.list()) {
						if (descriptor.extension != null && asset.name().endsWith(descriptor.extension)) {
							assetManager.load(asset.path(), descriptor.assetType);
						}
					}
				}

			}
			if (descriptor.extension != null) {
				for (FileHandle asset : folder.list()) {
					if (asset.name().endsWith(descriptor.extension)) {
						assetManager.load(asset.path(), descriptor.assetType);
					}
				}
			}
		}
		return true;
	}

	public void unload(String folderName) {
		FileHandle folder = resolver.resolve(folderName);
		if (!folder.exists()) {
			SIM8.debug("Folder " + folder.path() + " doesn't exist, can't unload");
			return;
		}
		SIM8.debug("Unload folder " + folder.path());

		for (AssetDescriptor descriptor : assets) {
			if (descriptor.folder != null) {
				FileHandle descriptorFolder = folder.child(descriptor.folder);
				if (descriptorFolder.exists()) {
					for (FileHandle asset : descriptorFolder.list()) {
						if (assetManager.isLoaded(asset.path())) {
							assetManager.unload(asset.path());
						}
					}
				}

			}
			if (descriptor.extension != null) {
				for (FileHandle asset : folder.list()) {
					if (asset.name().endsWith(descriptor.extension) && assetManager.isLoaded(asset.path())) {
						assetManager.unload(asset.path());
					}
				}
			}
		}
	}
}