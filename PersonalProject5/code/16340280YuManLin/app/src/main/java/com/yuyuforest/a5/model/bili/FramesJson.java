package com.yuyuforest.a5.model.bili;

public class FramesJson {
    private int code;
    private String message;
    private int ttl;
    private Data data;
    public class Data{
        private String pvdata;
        private int img_x_len;
        private int img_y_len;
        private int img_x_size;
        private int img_y_size;
        private String[] image;
        private int[] index;

        public int getImg_x_len() {
            return img_x_len;
        }

        public int getImg_y_len() {
            return img_y_len;
        }

        public int getImg_x_size() {
            return img_x_size;
        }

        public int getImg_y_size() {
            return img_y_size;
        }

        public String[] getImage() {
            return image;
        }

        public int getCount() {
            return index == null ? 0 : index.length - 2;
        }
    }

    public Data getData() {
        return data;
    }
}
