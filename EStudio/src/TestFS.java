public class TestFS {

    /**
     * @param args
     */
    public static void main(String[] args) {
        double[] startPoint = lonLat2Mercator(131.01917631407187,22.03760253932877);
        double[] endPoint = lonLat2Mercator(132.04572716289854,22.987861636689825);
        System.out.println("" + startPoint[0]+","+startPoint[1]+","+endPoint[0]+","+endPoint[1]);
        System.out.println(startPoint[1]-2500015.80728665);
        System.out.println(endPoint[1]-2613871.34966916);
        System.out.println(startPoint[0]-430850.940144156-10000000);
        System.out.println(endPoint[0]-545126.111659018-10000000);
    }

    /*
     * <ows:WGS84BoundingBox>
<ows:LowerCorner>131.01917631407187 22.03760253932877</ows:LowerCorner>
<ows:UpperCorner>132.04572716289854 22.987861636689825</ows:UpperCorner>
</ows:WGS84BoundingBox>
<ows:BoundingBox>
<ows:LowerCorner>430850.940144156 2500015.80728665</ows:LowerCorner>
<ows:UpperCorner>545126.111659018 2613871.34966916</ows:UpperCorner>
</ows:BoundingBox>
     */
    
    public static double[] lonLat2Mercator(double lon, double lat) {
        double[] xy = new double[2];

        double x = lon * 20037508.342789 / 180;

        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);

        y = y * 20037508.34789 / 180;

        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    // Ä«¿¨ÍÐ×ª¾­Î³¶È

    public static double[] Mercator2lonLat(double mercatorX, double mercatorY) {
        double[] xy = new double[2];
        double x = mercatorX / 20037508.34 * 180;

        double y = mercatorY / 20037508.34 * 180;

        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);

        xy[0] = x;
        xy[1] = y;
        return xy;

    }

}
