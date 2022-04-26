package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class AreaMoveRequest extends Request {

    private String migration_url;
    private String backup_file;
    private String sum;

    public AreaMoveRequest(String migration_url, String backup_file, String sum) {
        this.migration_url = migration_url;
        this.backup_file = backup_file;
        this.sum = sum;
    }

    public String getMigration_url() {
        return migration_url;
    }

    public void setMigration_url(String migration_url) {
        this.migration_url = migration_url;
    }

    public String getBackup_file() {
        return backup_file;
    }

    public void setBackup_file(String backup_file) {
        this.backup_file = backup_file;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
