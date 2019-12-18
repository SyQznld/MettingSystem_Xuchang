package com.appler.mettingsystem_xuchang.metting;

public class MeetingData {

    private String mettingName;
    private String mettingPath;

    public MeetingData() {
    }

    public String getMettingName() {
        return mettingName;
    }

    public void setMettingName(String mettingName) {
        this.mettingName = mettingName;
    }

    public String getMettingPath() {
        return mettingPath;
    }

    public void setMettingPath(String mettingPath) {
        this.mettingPath = mettingPath;
    }

    @Override
    public String toString() {
        return "MettingData{" +
                "mettingName='" + mettingName + '\'' +
                ", mettingPath='" + mettingPath + '\'' +
                '}';
    }
}
