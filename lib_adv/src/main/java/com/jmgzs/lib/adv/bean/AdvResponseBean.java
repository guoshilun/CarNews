package com.jmgzs.lib.adv.bean;

import java.util.List;

/**
 * API广告接口返回
 * Created by Wxl on 2017/6/20.
 */

public class AdvResponseBean {

    /**
     * ad_info : [{"ad_material":{"click_url":"http://c.mjmobi.com/cli?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QACC4Dii82Or7h9uu7cwBMMiK66XKKzoOCJ4gEKzmBBi83gYg_iFA2ARYHmIcaHR0cDovL2N5LnNoZW5qaW5na2UxMjAuY29tL2gBcACAAZcPiAGw6gGQAQGYAQawAekH","images":["https://mj-img.oss-cn-hangzhou.aliyuncs.com/ccfd7afc-64b8-4c11-aaf2-14b1b58fa29a.jpg"],"show_urls":["http://s.mjmobi.com/imp?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QvNjq-4fbru3MARoOCJ4gEKzmBBi83gYg_iEgyIrrpcorKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABsOoByAEB&seqs=0"]},"ad_type":2,"adid":4350,"landing_page":"http://cy.shenjingke120.com/","sid":0}]
     * id : ebb7fbcb-01da-4255-8c87-98eedbcd2909
     */

    private String id;
    private List<AdInfoBean> ad_info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AdInfoBean> getAd_info() {
        return ad_info;
    }

    public void setAd_info(List<AdInfoBean> ad_info) {
        this.ad_info = ad_info;
    }

    public static class AdInfoBean {
        /**
         * ad_material : {"click_url":"http://c.mjmobi.com/cli?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QACC4Dii82Or7h9uu7cwBMMiK66XKKzoOCJ4gEKzmBBi83gYg_iFA2ARYHmIcaHR0cDovL2N5LnNoZW5qaW5na2UxMjAuY29tL2gBcACAAZcPiAGw6gGQAQGYAQawAekH","images":["https://mj-img.oss-cn-hangzhou.aliyuncs.com/ccfd7afc-64b8-4c11-aaf2-14b1b58fa29a.jpg"],"show_urls":["http://s.mjmobi.com/imp?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QvNjq-4fbru3MARoOCJ4gEKzmBBi83gYg_iEgyIrrpcorKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABsOoByAEB&seqs=0"]}
         * ad_type : 2
         * adid : 4350
         * landing_page : http://cy.shenjingke120.com/
         * sid : 0
         */

        private AdMaterialBean ad_material = new AdMaterialBean();
        private int ad_type;
        private int adid;
        private String landing_page;
        private int sid;
        private String html_snippet;

        public AdMaterialBean getAd_material() {
            return ad_material;
        }

        public void setAd_material(AdMaterialBean ad_material) {
            this.ad_material = ad_material;
        }

        public int getAd_type() {
            return ad_type;
        }

        public void setAd_type(int ad_type) {
            this.ad_type = ad_type;
        }

        public int getAdid() {
            return adid;
        }

        public void setAdid(int adid) {
            this.adid = adid;
        }

        public String getLanding_page() {
            return landing_page;
        }

        public void setLanding_page(String landing_page) {
            this.landing_page = landing_page;
        }

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public String getHtml_snippet() {
            return html_snippet;
        }

        public void setHtml_snippet(String html_snippet) {
            this.html_snippet = html_snippet;
        }

        public static class AdMaterialBean {
            /**
             * click_url : http://c.mjmobi.com/cli?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QACC4Dii82Or7h9uu7cwBMMiK66XKKzoOCJ4gEKzmBBi83gYg_iFA2ARYHmIcaHR0cDovL2N5LnNoZW5qaW5na2UxMjAuY29tL2gBcACAAZcPiAGw6gGQAQGYAQawAekH
             * images : ["https://mj-img.oss-cn-hangzhou.aliyuncs.com/ccfd7afc-64b8-4c11-aaf2-14b1b58fa29a.jpg"]
             * show_urls : ["http://s.mjmobi.com/imp?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QvNjq-4fbru3MARoOCJ4gEKzmBBi83gYg_iEgyIrrpcorKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABsOoByAEB&seqs=0"]
             */

            private String title;
            private String content;
            private String desc;
            private String icon;
            //="http://img.hb.aicdn.com/e92cb14d3507e32f9a8e5409c0630d575681bcb7e6bab-mbZKoR_fw658";
            private String app_name;
            private String app_pkg_name;
            private String app_download_url;

            private String click_url;
            //="http://c.mjmobi.com/cli?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QACC4Dii82Or7h9uu7cwBMMiK66XKKzoOCJ4gEKzmBBi83gYg_iFA2ARYHmIcaHR0cDovL2N5LnNoZW5qaW5na2UxMjAuY29tL2gBcACAAZcPiAGw6gGQAQGYAQawAekH";
            private String brand;
            private String subtitle;
            private List<String> images;
            private List<String> show_urls;
            private List<String> download_complete_url;
            private List<String> installed_url;

            public String getClick_url() {
                return click_url;
            }

            public void setClick_url(String click_url) {
                this.click_url = click_url;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }

            public List<String> getShow_urls() {
                return show_urls;
            }

            public void setShow_urls(List<String> show_urls) {
                this.show_urls = show_urls;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getApp_name() {
                return app_name;
            }

            public void setApp_name(String app_name) {
                this.app_name = app_name;
            }

            public String getApp_pkg_name() {
                return app_pkg_name;
            }

            public void setApp_pkg_name(String app_pkg_name) {
                this.app_pkg_name = app_pkg_name;
            }

            public String getApp_download_url() {
                return app_download_url;
            }

            public void setApp_download_url(String app_download_url) {
                this.app_download_url = app_download_url;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getSubtitle() {
                return subtitle;
            }

            public void setSubtitle(String subtitle) {
                this.subtitle = subtitle;
            }

            public List<String> getDownload_complete_url() {
                return download_complete_url;
            }

            public void setDownload_complete_url(List<String> download_complete_url) {
                this.download_complete_url = download_complete_url;
            }

            public List<String> getInstalled_url() {
                return installed_url;
            }

            public void setInstalled_url(List<String> installed_url) {
                this.installed_url = installed_url;
            }
        }
    }
}
