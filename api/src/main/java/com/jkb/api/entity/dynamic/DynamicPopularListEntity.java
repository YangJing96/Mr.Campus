package com.jkb.api.entity.dynamic;

import java.util.List;

/**
 * 热门动态列表的数据实体类
 * Created by JustKiddingBaby on 2016/9/30.
 */

public class DynamicPopularListEntity {


    /**
     * total : 80
     * per_page : 10
     * current_page : 1
     * last_page : 8
     * next_page_url : http://bsapi.lyfsmile.cn/api/v1/getPopular/1?page=2
     * prev_page_url :
     * from : 1
     * to : 10
     * data : [{"popular_type":"circle","circle_id":47,"circle_name":"Damion Jones","circle_picture":"http://lorempixel.com/640/480/?58883","circle_introduction":"Voluptatem laboriosam aut doloremque quo. Quia dolorem velit accusantium dolorum temporibus debitis. Cumque non non nostrum et esse quis ut. Nisi vel autem et assumenda.","circle_type":"学生会","circle_created_at":"2016-08-03 16:24:09","has_subscribe":0,"count_of_dynamic":4,"count_of_subscription":39,"circle_longitude":163.54388,"circle_latitude":-75.638815,"creator_id":208,"creator_nickname":"lamar.corkery","creator_avatar":"http://lorempixel.com/640/480/?14669","school_id":1,"school_name":"金陵科技学院","school_badge":"http://image.lyfsmile.cn/school/jlkj.jpg"}]
     */

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private String next_page_url;
    private String prev_page_url;
    private int from;
    private int to;
    /**
     * popular_type : circle
     * circle_id : 47
     * circle_name : Damion Jones
     * circle_picture : http://lorempixel.com/640/480/?58883
     * circle_introduction : Voluptatem laboriosam aut doloremque quo. Quia dolorem velit accusantium dolorum temporibus debitis. Cumque non non nostrum et esse quis ut. Nisi vel autem et assumenda.
     * circle_type : 学生会
     * circle_created_at : 2016-08-03 16:24:09
     * has_subscribe : 0
     * count_of_dynamic : 4
     * count_of_subscription : 39
     * circle_longitude : 163.54388
     * circle_latitude : -75.638815
     * creator_id : 208
     * creator_nickname : lamar.corkery
     * creator_avatar : http://lorempixel.com/640/480/?14669
     * school_id : 1
     * school_name : 金陵科技学院
     * school_badge : http://image.lyfsmile.cn/school/jlkj.jpg
     */

    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(String prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String popular_type;
        private int circle_id;
        private String circle_name;
        private String circle_picture;
        private String circle_introduction;
        private String circle_type;
        private String circle_created_at;
        private int has_subscribe;
        private int count_of_dynamic;
        private int count_of_subscription;
        private double circle_longitude;
        private double circle_latitude;
        private int creator_id;
        private String creator_nickname;
        private String creator_avatar;
        private int school_id;
        private String school_name;
        private String school_badge;

        public String getPopular_type() {
            return popular_type;
        }

        public void setPopular_type(String popular_type) {
            this.popular_type = popular_type;
        }

        public int getCircle_id() {
            return circle_id;
        }

        public void setCircle_id(int circle_id) {
            this.circle_id = circle_id;
        }

        public String getCircle_name() {
            return circle_name;
        }

        public void setCircle_name(String circle_name) {
            this.circle_name = circle_name;
        }

        public String getCircle_picture() {
            return circle_picture;
        }

        public void setCircle_picture(String circle_picture) {
            this.circle_picture = circle_picture;
        }

        public String getCircle_introduction() {
            return circle_introduction;
        }

        public void setCircle_introduction(String circle_introduction) {
            this.circle_introduction = circle_introduction;
        }

        public String getCircle_type() {
            return circle_type;
        }

        public void setCircle_type(String circle_type) {
            this.circle_type = circle_type;
        }

        public String getCircle_created_at() {
            return circle_created_at;
        }

        public void setCircle_created_at(String circle_created_at) {
            this.circle_created_at = circle_created_at;
        }

        public int getHas_subscribe() {
            return has_subscribe;
        }

        public void setHas_subscribe(int has_subscribe) {
            this.has_subscribe = has_subscribe;
        }

        public int getCount_of_dynamic() {
            return count_of_dynamic;
        }

        public void setCount_of_dynamic(int count_of_dynamic) {
            this.count_of_dynamic = count_of_dynamic;
        }

        public int getCount_of_subscription() {
            return count_of_subscription;
        }

        public void setCount_of_subscription(int count_of_subscription) {
            this.count_of_subscription = count_of_subscription;
        }

        public double getCircle_longitude() {
            return circle_longitude;
        }

        public void setCircle_longitude(double circle_longitude) {
            this.circle_longitude = circle_longitude;
        }

        public double getCircle_latitude() {
            return circle_latitude;
        }

        public void setCircle_latitude(double circle_latitude) {
            this.circle_latitude = circle_latitude;
        }

        public int getCreator_id() {
            return creator_id;
        }

        public void setCreator_id(int creator_id) {
            this.creator_id = creator_id;
        }

        public String getCreator_nickname() {
            return creator_nickname;
        }

        public void setCreator_nickname(String creator_nickname) {
            this.creator_nickname = creator_nickname;
        }

        public String getCreator_avatar() {
            return creator_avatar;
        }

        public void setCreator_avatar(String creator_avatar) {
            this.creator_avatar = creator_avatar;
        }

        public int getSchool_id() {
            return school_id;
        }

        public void setSchool_id(int school_id) {
            this.school_id = school_id;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public String getSchool_badge() {
            return school_badge;
        }

        public void setSchool_badge(String school_badge) {
            this.school_badge = school_badge;
        }
    }
}
