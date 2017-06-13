package com.jmgzs.carnews.base;

import android.content.Intent;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.jmgzs.carnews.bean.Photo;

import java.io.InputStream;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class PhotoLoader extends BaseGlideUrlLoader<Photo> {

    public static class Factory implements ModelLoaderFactory<Photo, InputStream> {
        private final ModelCache<Photo, GlideUrl> modelCache = new ModelCache<>(500);

        @Override
        public ModelLoader<Photo, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new PhotoLoader(multiFactory.build(GlideUrl.class, InputStream.class), modelCache);
        }

        @Override
        public void teardown() {

        }
    }

    public PhotoLoader(ModelLoader<GlideUrl, InputStream> urlLoader, ModelCache<Photo, GlideUrl> cache) {
        super(urlLoader, cache);
    }

    @Override
    protected String getUrl(Photo photo, int width, int height, Options options) {
        return photo.getUrl();
    }

    @Override
    public boolean handles(Photo photo) {
        return true;
    }
}
