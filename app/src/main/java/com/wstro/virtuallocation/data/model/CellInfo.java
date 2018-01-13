package com.wstro.virtuallocation.data.model;

/**
 * @author pengl
 */

public class CellInfo {

    /**
     * mnc : 1
     * lac : 41093
     * ci : 3865320
     * acc : 1177
     * location : {"lon":116.343278,"lat":39.531734}
     */

    private int mnc;
    private int lac;
    private int ci;
    private int acc;
    private LocationBean location;

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public static class LocationBean {
        /**
         * lon : 116.343278
         * lat : 39.531734
         */

        private double lon;
        private double lat;

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }
}
