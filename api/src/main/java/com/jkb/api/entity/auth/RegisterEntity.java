package com.jkb.api.entity.auth;

import java.util.List;

/**
 * 注册的请求接口返回的实体类
 * Created by JustKiddingBaby on 2016/8/1.
 */
public class RegisterEntity {


    /**
     * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
     * userInfo : {"id":3,"UID":"1000001","nickname":"lyf","avatar":"","sex":"","name":"","bref_introduction":"","background":"","longitude":"","latitude":"","created_at":"2016-07-27 16:40:27","email":"664275973@qq.com","phone":"","schoolInfo":{"id":1,"sname":"金陵科技学院","badge":"","summary":""}}
     * credential : ["The credential field is required."]
     * identifier : ["The identifier field is required."]
     */

    private String token;
    /**
     * id : 3
     * UID : 1000001
     * nickname : lyf
     * avatar :
     * sex :
     * name :
     * bref_introduction :
     * background :
     * longitude :
     * latitude :
     * created_at : 2016-07-27 16:40:27
     * email : 664275973@qq.com
     * phone :
     * schoolInfo : {"id":1,"sname":"金陵科技学院","badge":"","summary":""}
     */

    private UserInfoBean userInfo;
    private List<String> credential;
    private List<String> identifier;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public List<String> getCredential() {
        return credential;
    }

    public void setCredential(List<String> credential) {
        this.credential = credential;
    }

    public List<String> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<String> identifier) {
        this.identifier = identifier;
    }

    public static class UserInfoBean {
        private int id;
        private String UID;
        private String nickname;
        private String avatar;
        private String sex;
        private String name;
        private String bref_introduction;
        private String background;
        private String longitude;
        private String latitude;
        private String created_at;
        private String email;
        private String phone;
        /**
         * id : 1
         * sname : 金陵科技学院
         * badge :
         * summary :
         */

        private SchoolInfoBean schoolInfo = new SchoolInfoBean();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBref_introduction() {
            return bref_introduction;
        }

        public void setBref_introduction(String bref_introduction) {
            this.bref_introduction = bref_introduction;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public SchoolInfoBean getSchoolInfo() {
            return schoolInfo;
        }

        public void setSchoolInfo(SchoolInfoBean schoolInfo) {
            this.schoolInfo = schoolInfo;
            if (schoolInfo == null) {
                this.schoolInfo = new SchoolInfoBean();
            }
        }

        public static class SchoolInfoBean {
            private int id;
            private String sname;
            private String badge;
            private String summary;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getSname() {
                return sname;
            }

            public void setSname(String sname) {
                this.sname = sname;
            }

            public String getBadge() {
                return badge;
            }

            public void setBadge(String badge) {
                this.badge = badge;
            }

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }
        }
    }
}
