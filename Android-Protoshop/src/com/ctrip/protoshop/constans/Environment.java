package com.ctrip.protoshop.constans;




public enum Environment {

    OUTERNET("http://api.protoshop.io", false, false), INNERNET("http://protoshop.ctripqa.com/ProtoShop", false, true),
    TEST("http://protoshop.ctripqa.com/debugProtoShop", true, true);

    private String host;
    private boolean isNeedLog;
    private boolean isNeedDomain;

    private Environment(String host, boolean isNeedLog, boolean isNeedDomain) {
        this.host = host;
        this.isNeedLog = isNeedLog;
        this.isNeedDomain = isNeedDomain;
    }

    public String getHost() {
        return host;
    }

    public boolean isNeedLog() {
        return isNeedLog;
    }

    public boolean isNeedDomain() {
        return isNeedDomain;
    }

}
