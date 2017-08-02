package com.jmgzs.lib.adv.bean;

import com.google.gson.Gson;

/**
 * Created by Wxl on 2017/6/19.
 */

public class AdvBean {

    private String exposure_url;
    private String btn_url;
    private Image image;
    private Gl gl;

    public static AdvBean getDataByStr(String json) {
        return new Gson().fromJson(json, AdvBean.class);
    }

    public String getExposure_url() {
        return exposure_url;
    }

    public void setExposure_url(String exposure_url) {
        this.exposure_url = exposure_url;
    }

    public String getBtn_url() {
        return btn_url;
    }

    public void setBtn_url(String btn_url) {
        this.btn_url = btn_url;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Gl getGl() {
        return gl;
    }

    public void setGl(Gl gl) {
        this.gl = gl;
    }

    public static class Image {
        private String url;
        private String width;
        private String height;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }
    }

    public static class Gl {
        private String title;
        private String detail;
        private String desc;
        private Logo logo;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Logo getLogo() {
            return logo;
        }

        public void setLogo(Logo logo) {
            this.logo = logo;
        }

        public static class Logo {
            private String url;
            private int w;
            private int h;
            private int scale;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getW() {
                return w;
            }

            public void setW(int w) {
                this.w = w;
            }

            public int getH() {
                return h;
            }

            public void setH(int h) {
                this.h = h;
            }

            public int getScale() {
                return scale;
            }

            public void setScale(int scale) {
                this.scale = scale;
            }
        }
    }
}
