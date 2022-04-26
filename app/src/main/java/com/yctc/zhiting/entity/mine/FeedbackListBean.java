package com.yctc.zhiting.entity.mine;

import java.util.List;

public class FeedbackListBean {

    private List<FeedbacksBean> feedbacks;

    public List<FeedbacksBean> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbacksBean> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public static class FeedbacksBean {
        /**
         * id : -4.701433504976946E7
         * feedback_type : -1251959.1812849492
         * type : -5755913.493944615
         * description : non
         * created_at : -7588694.8982298225
         */

        private int id;
        private int feedback_type; // 类型(1:遇到问题, 2:提建议)
        /**
         * 类型, 1: 应用问题  2: 注册和登录问题  3: 用户数据问题 4: 设备问题 5: 场景问题 6: 其他问题 7: 应用功能建议 8: 设备功能建议 9: 场景功能建议 10: 其他功能建议
         */
        private int type;
        private String description;  // 描述
        private long created_at;  // 创建时间

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

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

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }
    }
}
