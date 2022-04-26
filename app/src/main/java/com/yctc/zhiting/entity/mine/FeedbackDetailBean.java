package com.yctc.zhiting.entity.mine;

import java.util.List;

public class FeedbackDetailBean {

    /**
     * feedback_type : -4619781.070935994
     * type : -1.3288993682101682E7
     * description : labore elit
     * contact_information : sint do Ut cillum
     * is_auth : true
     * phone_model : consequat amet
     * files : [{"id":6.136589670908284E7,"file_name":"ut velit dolor eiusmod","file_type":"non","file_url":"consectetur"},{"id":-3.5013192861914136E7,"file_name":"esse","file_type":"et voluptate in","file_url":"non quis labore"},{"id":6.6419847987467706E7,"file_name":"Duis cupidatat sint","file_type":"nostrud nisi commodo aute","file_url":"proident U"}]
     */

    private int feedback_type;
    private int type;
    private String description;
    private String contact_information;
    private boolean is_auth;
    private String phone_model;
    private List<FilesBean> files;

    public int getFeedback_type() {
        return feedback_type;
    }

    public void setFeedback_type(int feedback_type) {
        this.feedback_type = feedback_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact_information() {
        return contact_information;
    }

    public void setContact_information(String contact_information) {
        this.contact_information = contact_information;
    }

    public boolean isIs_auth() {
        return is_auth;
    }

    public void setIs_auth(boolean is_auth) {
        this.is_auth = is_auth;
    }

    public String getPhone_model() {
        return phone_model;
    }

    public void setPhone_model(String phone_model) {
        this.phone_model = phone_model;
    }

    public List<FilesBean> getFiles() {
        return files;
    }

    public void setFiles(List<FilesBean> files) {
        this.files = files;
    }

    public static class FilesBean {
        /**
         * id : 6.136589670908284E7
         * file_name : ut velit dolor eiusmod
         * file_type : non
         * file_url : consectetur
         */

        private double id;
        private String file_name;
        private String file_type;
        private String file_url;

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }
    }
}
