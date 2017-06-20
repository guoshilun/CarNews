package com.jmgsz.lib.adv.bean;

import java.util.List;

/**API广告接口返回
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

        private AdMaterialBean ad_material;
        private int ad_type;
        private int adid;
        private String landing_page;
        private int sid;

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

        public static class AdMaterialBean {
            /**
             * click_url : http://c.mjmobi.com/cli?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QACC4Dii82Or7h9uu7cwBMMiK66XKKzoOCJ4gEKzmBBi83gYg_iFA2ARYHmIcaHR0cDovL2N5LnNoZW5qaW5na2UxMjAuY29tL2gBcACAAZcPiAGw6gGQAQGYAQawAekH
             * images : ["https://mj-img.oss-cn-hangzhou.aliyuncs.com/ccfd7afc-64b8-4c11-aaf2-14b1b58fa29a.jpg"]
             * show_urls : ["http://s.mjmobi.com/imp?info=ChhDTWlLNjZYS0t4Q3VvTUZRR0kzSjRWMD0QvNjq-4fbru3MARoOCJ4gEKzmBBi83gYg_iEgyIrrpcorKNgEOJcPQLgOmAEGqAHpB7ABAbgBAMABsOoByAEB&seqs=0"]
             */

            private String click_url;
            private List<String> images;
            private List<String> show_urls;

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
        }
    }
}
