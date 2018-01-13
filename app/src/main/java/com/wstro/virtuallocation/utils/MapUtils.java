package com.wstro.virtuallocation.utils;

public class MapUtils {

    public static class LatLngPoint {
        private double lat;
        private double lng;

        public LatLngPoint(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(long lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(long lng) {
            this.lng = lng;
        }
    }

    private final static double PI = 3.14159265358979324;
    private final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * WGS84转GCJ02
     *
     * @param point WGS84
     * @return
     */
    public static LatLngPoint wgs2gcj(LatLngPoint point) {
        LatLngPoint newpt;
        if (isOutOfChina(point)) {
            return point;
        }
        LatLngPoint d = delta(point);
        newpt = new LatLngPoint(point.getLat() + d.getLat(), point.getLng() + d.getLng());


        return newpt;
    }


    /**
     * 国测局转WGS84
     *
     * @param point
     * @return
     */
    public static LatLngPoint gcj2wgs(LatLngPoint point) {
        double gcjLat = point.getLat();
        double gcjLon = point.getLng();
        double initDelta = 0.01;
        double threshold = 0.000000001;
        double dLat = initDelta;
        double dLon = initDelta;
        double mLat = gcjLat - dLat;
        double mLon = gcjLon - dLon;
        double pLat = gcjLat + dLat;
        double pLon = gcjLon + dLon;
        double wgsLat;
        double wgsLon;
        int i = 0;
        while (true) {
            wgsLat = (mLat + pLat) / 2;
            wgsLon = (mLon + pLon) / 2;
            LatLngPoint tmp = gcj2wgs(new LatLngPoint(wgsLat, wgsLon));//gcj_encrypt  
            dLat = tmp.getLat() - gcjLat;
            dLon = tmp.getLng() - gcjLon;
            if ((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
                break;

            if (dLat > 0) pLat = wgsLat;
            else mLat = wgsLat;
            if (dLon > 0) pLon = wgsLon;
            else mLon = wgsLon;

            if (++i > 10000) break;
        }
        //console.log(i);  
        return new LatLngPoint(wgsLat, wgsLon);
    }

    /**
     * 国测局转百度
     *
     * @param point
     * @return
     */
    public static LatLngPoint gcj2bd(LatLngPoint point) {
        double gcjLon = point.getLng();
        double gcjLat = point.getLat();
        double x = gcjLon;
        double y = gcjLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new LatLngPoint(bdLat, bdLon);
    }

    /**
     * 百度转国测局
     *
     * @param point
     * @return
     */
    public static LatLngPoint bd2gcj(LatLngPoint point) {
        double bdLon = point.getLng();
        double bdLat = point.getLat();
        double x = bdLon - 0.0065;
        double y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gcjLon = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        return new LatLngPoint(gcjLat, gcjLon);//{'lat' : gcjLat, 'lon' : gcjLon};  
    }


    /**
     * wgs转百度
     *
     * @param point
     * @return
     */
    public static LatLngPoint wgs2bd(LatLngPoint point) {
//wgs--gcj  
        LatLngPoint gcjpt = wgs2gcj(point);
//gcj--bd  
        return gcj2bd(gcjpt);
    }


    /**
     * 百度转wgs
     *
     * @param point
     * @return
     */
    public static LatLngPoint bd2wgs(LatLngPoint point) {
//bd---gcj  
        LatLngPoint gcjpt = bd2gcj(point);
//gcj--wgs  
        return gcj2wgs(gcjpt);
    }

    private static boolean isOutOfChina(LatLngPoint point) {
        if (point.getLng() < 72.004 || point.getLng() > 137.8347)
            return true;
        if (point.getLat() < 0.8293 || point.getLat() > 55.8271)
            return true;
        return false;
    }

    private static LatLngPoint delta(LatLngPoint point) {
        LatLngPoint d;
        double a = 6378245.0;//  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
        double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
        double dlat;
        double dlng;
        double radlat;
        double magic;
        double sqrtmagic;

        dlat = transformLat(point.getLng() - 105.0, point.getLat() - 35.0);
        dlng = transformLon(point.getLng() - 105.0, point.getLat() - 35.0);
        radlat = point.getLat() / 180.0 * PI;
        magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);

        d = new LatLngPoint(dlat, dlng);

        return d;
    }


    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }


}  