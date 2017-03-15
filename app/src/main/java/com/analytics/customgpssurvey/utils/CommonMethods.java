package com.analytics.customgpssurvey.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.analytics.customgpssurvey.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonMethods {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    /**
     * This method will read the given file and return its content in String form.
     * @param filePath path of the file to be read.
     * @return String data, content of the file.
     * @throws IOException it may throw IOException if there is problem in reading file.
     */
    public static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();

        InputStreamReader reader = new InputStreamReader(new FileInputStream(
                new File(filePath)), Charset.forName("ISO-8859-1"));
        while (reader.ready()) {
            fileData.append((char) reader.read());
        }
        return fileData.toString().trim();
    }

    /**
     * This method will read all the content of the inputStream and return with consumed content.
     * @param inputStream open inputString that is having content to be processed.
     * @return return the content of the inputString.
     * @throws IOException if the content of the inputStream if consumed or flushed it may return IOException.
     */
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /**
     * This method will convert given string to MD5 string
     * @param value string to be converted to MD5 String.
     * @return Updated MD5 string of the given value.
     */
    public static String md5ForPassword(String value) {
        try {
            String s = value;
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            Locale defaultLocale = Locale.getDefault();
            return hexString.toString().toUpperCase(defaultLocale);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This method will open the given URL in default browser of the device
     * @param context context of the current activity
     * @param url string url to open in browser
     */
    public static void openUrlInBrowser(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    /**
     * This method will return the root folder for the project on Storage directory
     * @param c context instance for local reference
     * @return File object containing project root folder
     */
    public static File getProjectDirectory(Context c){
        // Find the SD Card path
        File projRootPath = new File(Environment.getExternalStorageDirectory() + "/" + c.getString(R.string.app_name));
        if(!projRootPath.exists()){
            projRootPath.mkdirs();
        }
        return projRootPath;
    }

    /**
     * This method will send the broadcast to finish all the activities because of session expired
     * @param context context reference
     */
    public static void sendSessionExpire(Context context){
        Intent intent = new Intent("sessionExpired");
        // add data
        intent.putExtra("message", "KillActivity");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * This method will save the given bitmap in internal storage with the given name
     * @param context
     * @param bmp bitmap object to save as image
     * @param fileName name of the image file
     * @return absolute path of the image
     */
    public static String saveImageLocally(Context context, Bitmap bmp, String fileName){
        FileOutputStream fos = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(context.getFilesDir(), fileName+".jpg");
        try {
            file.deleteOnExit();
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(bytes.toByteArray());
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * <p>This method will change the drawable's visible content to given color.
     * This will Parse the given color string, and set the corresponding converted image to imageView.
     * If the string cannot be parsed, throws an IllegalArgumentException exception.
     * Supported formats are: #RRGGBB #AARRGGBB or one of the following names: 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta', 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey', 'aqua', 'fuchsia', 'lime', 'maroon', 'navy', 'olive', 'purple', 'silver', 'teal'.
     * </p>
     * @param context for reference
     * @param imageView on which the new drawable has to be applied
     * @param drawableID to apply color on
     * @param colorString Target color to be drown on drawable
     * @throws IllegalArgumentException If the string cannot be parsed
     */
    public static void setDrawableCustomColor(Context context, ImageView imageView, int drawableID, String colorString)
            throws IllegalArgumentException {
        Drawable d = context.getResources().getDrawable(drawableID);
        d.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(d);
    }

    /**
     * <p>This method will change the drawable's visible content to given color.
     * This will Parse the given color string, and set the corresponding converted image to imageView.
     * If the string cannot be parsed, throws an IllegalArgumentException exception.
     * Supported formats are: #RRGGBB #AARRGGBB or one of the following names: 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta', 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey', 'aqua', 'fuchsia', 'lime', 'maroon', 'navy', 'olive', 'purple', 'silver', 'teal'.
     * </p>
     * @param context for reference
     * @param drawableID to apply color on
     * @param colorString Target color to be drown on drawable
     * @throws IllegalArgumentException If the string cannot be parsed
     */
    public static Drawable getDrawableCustomColor(Context context, int drawableID, int colorString) throws IllegalArgumentException {
        Drawable d = context.getResources().getDrawable(drawableID);
        d.setColorFilter(colorString, PorterDuff.Mode.SRC_ATOP);
        return d;
    }

    /**
     * This method will check that GPS is enabled or not. If gps is enabled it will return TRUE else FALSE
     * @param context for reference
     * @return boolean value true if the GPS is enabled else false
     */
    public static boolean isGPSEnabled(Context context){
        LocationManager service = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /**
     * This method will convert given dp to pixels
     * @param resources object to get display metrix
     * @param dp float value containing DP
     * @return float value containing pixels
     */
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    /**
     * This method will convert given pixels to dp
     * @param resources object to get display metrix
     * @param sp float value containing pixels
     * @return float value containing dp
     */
    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * This method will extract house number from the given address else blank string
     * @param fullAddress address from which you want to extract the house number
     * @return returns extracted house number or "" blank string.
     */
    public static String getHouseNumber(String fullAddress){
        final String HOUSE_NO_REG_EXP = "[^a-z|A-Z|\\d](\\d{1,3}[a-z|A-Z|]?)[^a-z|A-Z|\\d]";
        Pattern pattern = Pattern.compile(HOUSE_NO_REG_EXP);
        Matcher matcher = pattern.matcher(fullAddress);
        matcher.useTransparentBounds(true);
        if (matcher.find()){
            return matcher.group(0);
        }else{
            return "";
        }
    }

    /**
     * This will convert coma separated latlng in integer format object to 16 char string.
     * @param location
     * @return lat,lng
     */
//    public static String getLatLngToString(LatLng location){
//        try {
//            int lat, lng;
//            lat = (int) (location.latitude * 10000000);
//            lng = (int) (location.longitude * 10000000);
//            return lat+","+lng;
//        }catch (Exception e){
//            e.printStackTrace();
//            return "0,0";
//        }
//    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
//    public static boolean checkPlayServices(Activity context) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                context.finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public static void setBadge(Context context, int badgeCount){
//        ShortcutBadger.applyCount(context, badgeCount); //for 1.1.4+
    }

    public static void removeBadge(Context context){
//        ShortcutBadger.removeCount(context);
    }

    public static Bitmap downSampleBitmap(Context context, Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

         BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        options.outHeight = bitmap.getHeight();
        options.outWidth = bitmap.getWidth();
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, options.outWidth/2, options.outHeight/2);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static String encodeToBase64(Bitmap bitmap) {

        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        if (imageEncoded == null) {
            return "";
        }
        return imageEncoded;
    }
    public static String encodeToByteArray(Bitmap bitmap) {

        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();

        String imageEncoded = null;
        try {
            imageEncoded = Base64.encodeToString(bytes, Base64.DEFAULT);
          //  imageEncoded=resizeBase64Image(imageEncoded,bitmap.getWidth(),bitmap.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imageEncoded == null) {
            return "";
        }
        return imageEncoded;
    }
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }
    public static byte[] decodeToByteArray(String imageString){
        try {
            byte[] out = new byte[imageString.length()];
            for (int i = 0; i < imageString.length(); i++) {
                out[i] = (byte) imageString.charAt(i);
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String resizeBase64Image(String base64image,int IMG_WIDTH,int IMG_HEIGHT){
        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        if(image.getHeight() <= 400 && image.getWidth() <= 400){
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, IMG_WIDTH, IMG_HEIGHT, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }
    public static Bitmap getBitmap(File file) {


        Bitmap bitmap = null;
        if (file != null) {
            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(filePath, options);

        }
        return bitmap;
    }
}

