package com.yuyuforest.a5.model.bili;

public class VideoJson {
    private Boolean status;
    private Data data;
    public class Data  {
        private int aid;
		private String cover;
		private String title;
		private String content;
        private String duration;
        private String create;
		private int play;
		private int video_review;

        public int getAid() {
            return aid;
        }

        public String getCover() {
            return cover;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getDuration() {
            return duration;
        }

        public String getCreate() {
            return create;
        }

        public int getPlay() {
            return play;
        }

        public int getReview() {
            return video_review;
        }
    }

    public Boolean getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }
}
