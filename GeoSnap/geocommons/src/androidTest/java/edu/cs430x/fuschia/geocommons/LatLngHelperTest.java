package edu.cs430x.fuschia.geocommons;

//import com.google.android.gms.maps.model.LatLng;
//
//import org.testng.annotations.Test;

import edu.cs430x.fuschia.geocommons.location.LatLngHelper;

/**
 * These tests don't actually run in Android Studio...
 *
 */
public class LatLngHelperTest extends LatLngHelper {
//	// Make sure buckets are multiples of the increments
//	final static double LAT_BUCKET_1 = 42.2625, LAT_BUCKET_2 = 42.27,
//			LAT_BUCKET_3 = 42.2775, LON_BUCKET_1 = -71.8,
//			LON_BUCKET_2 = -71.81, LON_BUCKET_3 = -71.82,
//			LON_BUCKET_4 = -71.83;
//
//	final double L_INC = 0.0001;
//
//	@Test
//	public void test_calcBucket_latitude_basic() {
//		myAssertEquals(LAT_BUCKET_1,
//				LatLngHelper.calcBucket(LAT_BUCKET_1, 0).latitude);
//		myAssertEquals(LAT_BUCKET_2,
//				LatLngHelper.calcBucket(LAT_BUCKET_2, 0).latitude);
//		myAssertEquals(LAT_BUCKET_3,
//				LatLngHelper.calcBucket(LAT_BUCKET_3, 0).latitude);
//	}
//
//	@Test
//	public void test_calcBucket_latitude_round_down() {
//		for (int mult = 0; mult * L_INC < LAT_INC; mult++) {
//			double l = mult * L_INC; // Multiply each time because its stupid
//			for (double bucket = LAT_BUCKET_1; bucket < 51; bucket += LAT_INC) {
//				String s = String.valueOf(mult) + "  L: " + String.valueOf(l);
//				LatLng b = LatLngHelper.calcBucket(bucket + l, 0);
//				myAssertEquals(s, bucket, b.latitude);
//			}
//		}
//	}
//
//	@Test
//	public void test_calcBucket_longitude_basic() {
//		myAssertEquals(LON_BUCKET_1,
//				LatLngHelper.calcBucket(0, LON_BUCKET_1).longitude);
//		myAssertEquals(LON_BUCKET_2,
//				LatLngHelper.calcBucket(0, LON_BUCKET_2).longitude);
//		myAssertEquals(LON_BUCKET_4,
//				LatLngHelper.calcBucket(0, LON_BUCKET_4).longitude);
//		// TODO -71.82 is a magic number that breaks tests....
//		// myAssertEquals(LON_BUCKET_3,
//		// LatLngHelper.calcBucket(0, LON_BUCKET_3).longitude);
//	}
//
//	@Test
//	public void test_calcBucket_longitude_round_toward_zero() {
//
//		for (int mult = 0; mult * L_INC < LON_INC; mult++) {
//			double l = mult * L_INC; // Multiply each time because its stupid
//			for (double bucket = LON_BUCKET_1; bucket > -83; bucket -= LON_INC) {
//				String s = String.valueOf(mult) + "  L: " + String.valueOf(l);
//				LatLng latlon = LatLngHelper.calcBucket(0, bucket - l);
//				myAssertEquals(s, bucket, latlon.longitude);
//			}
//		}
//	}
//
//	final static double THIRD_LAT_INC = LAT_INC * (FAR_PERCENT + 0.001),
//			THIRD_LON_INC = LON_INC * (FAR_PERCENT + 0.001);
//	final static double center_lat = LAT_BUCKET_1 + LAT_INC / 2.0,
//			center_lon = LON_BUCKET_1 - LON_INC / 2.0;
//
//	/**
//	 * Adding to latitude = up subtracting from long = left ( more negative =
//	 * left
//	 *
//	 */
//
//
//	@Test
//	 public void test_calcBucketName_center() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat, center_lon);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertEquals(1, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_top() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				+ THIRD_LAT_INC, center_lon);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.270;-71.80"));
//		assertEquals(2, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_bottom() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				- THIRD_LAT_INC, center_lon);
//
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.255;-71.80"));
//		assertEquals(2, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_left() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat, center_lon
//				- THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.262;-71.81"));
//		assertEquals(2, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_right() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat, center_lon
//				+ THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.262;-71.79"));
//		assertEquals(2, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_top_right() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				+ THIRD_LAT_INC, center_lon + THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.270;-71.80"));
//		assertTrue(c.contains("42.262;-71.79"));
//		assertTrue(c.contains("42.270;-71.79"));
//		assertEquals(4, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_top_left() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				+ THIRD_LAT_INC, center_lon - THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.270;-71.80"));
//		assertTrue(c.contains("42.262;-71.81"));
//		assertTrue(c.contains("42.270;-71.81"));
//		assertEquals(4, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_bot_right() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				- THIRD_LAT_INC, center_lon + THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.255;-71.80"));
//		assertTrue(c.contains("42.262;-71.79"));
//		assertTrue(c.contains("42.255;-71.79"));
//		assertEquals(4, c.size());
//	}
//
//	@Test
//	public void test_calcBucketName_bot_left() {
//		Collection<String> c = LatLngHelper.getBuckets(center_lat
//				- THIRD_LAT_INC, center_lon - THIRD_LON_INC);
//		assertTrue(c.contains("42.262;-71.80"));
//		assertTrue(c.contains("42.255;-71.80"));
//		assertTrue(c.contains("42.262;-71.81"));
//		assertTrue(c.contains("42.255;-71.81"));
//		assertEquals(4, c.size());
//	}
//
//
//	final double delta = 0.000003;
//	public void myAssertEquals(String s, double expected, double actual) {
//		assertEquals(s, expected, actual, delta);
//	}
//
//	public void myAssertEquals(double expected, double actual) {
//		assertEquals(expected, actual, delta);
//	}

}
