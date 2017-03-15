package com.analytics.customgpssurvey.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;


import com.analytics.customgpssurvey.R;

import java.util.ArrayList;
import java.util.List;


public class AccessPermissions {
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    /**
     * This method will check for app permissions and if permissions are not granted, it will request for permission.
     *
     * This mthod will return true if all the permissions are granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    public static boolean checkOrRequestAllPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION, activity))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE, activity))
            permissionsNeeded.add("Write External Storage");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA, activity))
            permissionsNeeded.add("CAMERA");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE, activity))
            permissionsNeeded.add("Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = activity.getString(R.string.message_grant_permission_to) + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++){
                    message = message + ", " + permissionsNeeded.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message);
                builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(noListener!=null){
                            noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                        }
                    }
                });

                builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(myAppSettings);
                        }
                        dialog.dismiss();
                        if(yesListener!=null){
                            yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                        }
                    }
                });
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }

            activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * This method will return true if the
     * @param permissionsList
     * @param permission
     * @param activity
     * @return
     */
    private static boolean addPermission(List<String> permissionsList, String permission, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!activity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    /**
     * This method will check for GPS permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if GPS permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestGPSPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isGPSPermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "GPS";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }

        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check for GPS permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if GPS permission is granted else it will return false and request user for permission.
     * @param fragment Fragment reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    public static boolean checkOrRequestGPSPermission(final Fragment fragment, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isGPSPermissionsGranted(fragment.getContext())) {
            return true;
        }

        if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Need Rationale
            String message = fragment.getString(R.string.message_grant_permission_to) + "GPS";
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
            builder.setMessage(message);
            builder.setPositiveButton(fragment.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(fragment.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + fragment.getContext().getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        fragment.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }

        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check that GPS permission is given or not
     * @param context reference of the context to check permission
     * @return returns true if the gps permission is given else return false.
     */
    public static boolean isGPSPermissionsGranted(final Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }
    /**
     * This method will check for Write External Storage permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if Write External Storage permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestWritePermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isWritePermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "Write External Storage";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        activity.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check that Write External permission is given or not
     * @param activity reference of the activity to check permission
     * @return returns true if the Write External permission is given else return false.
     */
    public static boolean isWritePermissionsGranted(final Activity activity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }



    /**
     * This method will check that Write External permission is given or not
     * @param context reference of the activity to check permission
     * @return returns true if the Write External permission is given else return false.
     */
    public static boolean isWritePermissionsGranted(final Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This method will check for Write External Storage permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if Write External Storage permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestReadPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isReadPermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "Read External Storage";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }
    /**
     * This method will check that Read External permission is given or not
     * @param activity reference of the activity to check permission
     * @return returns true if the Read External permission is given else return false.
     */
    public static boolean isReadPermissionsGranted(final Activity activity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * This method will check that Write External permission is given or not
     * @param context reference of the activity to check permission
     * @return returns true if the Write External permission is given else return false.
     */
    public static boolean isReadPermissionsGranted(final Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    /**
     * This method will check for Camera permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if Camera permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestCameraPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isCameraPermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "CAMERA";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        activity.requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check that CAMERA permission is given or not
     * @param activity reference of the activity to check permission
     * @return returns true if the CAMERA permission is given else return false.
     */
    public static boolean isCameraPermissionsGranted(final Activity activity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This method will check for ACTION MANAGE OVERLAY PERMISSION permission and if the permission is not granted it will ask user to provide the permission for system window
     * @param activity for reference.
     * @param requestCode request code to handle in onActivityResult callback
     */
    public static void requestWindowService(final Activity activity, final int requestCode){
        if(!canDrawOverApps(activity)) {
            String message = activity.getString(R.string.message_grant_permission_to) + "ACTION MANAGE OVERLAY PERMISSION";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, requestCode);
                }
            });

            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * This method will check that app is allowed to draw window Service over other app or not.
     * @param activity for reference
     * @return false if the permission is not allowed and true if the device version is lower than marshmallow and permission is given.
     */
    public static boolean canDrawOverApps(final Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(activity);
        }
        return true;
    }

    /**
     * This method will check for Camera permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if Camera permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestAudioRecordPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isCameraPermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "RECORD AUDIO";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        activity.requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check that CAMERA permission is given or not
     * @param activity reference of the activity to check permission
     * @return returns true if the CAMERA permission is given else return false.
     */
    public static boolean isAudioRecordPermissionsGranted(final Activity activity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This method will check for Write External Storage permissions and if permissions is not granted, it will request for permission.
     *
     * This mthod will return true if Write External Storage permission is granted else it will return false and request user for permission.
     * @param activity Activity reference
     * @param yesListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.SUCCESS_CODE will be received for which value. If not require to handle callback pass null.
     * @param noListener this is an instance of DialogInterface.OnClickListener where in response, dialog will be null and GlobalObjects.FAILURE_CODE will be received for which value. If not require to handle callback pass null.
     * @return true if all the permissions are already granted else false.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkOrRequestContactPermission(final Activity activity, final DialogInterface.OnClickListener yesListener, final DialogInterface.OnClickListener noListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isWritePermissionsGranted(activity)) {
            return true;
        }

        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            // Need Rationale
            String message = activity.getString(R.string.message_grant_permission_to) + "Read Contacts";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message);
            builder.setPositiveButton(activity.getString(R.string.dialog_confirm_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(noListener!=null){
                        noListener.onClick(null, GlobalObjects.FAILURE_CODE);
                    }
                }
            });

            builder.setNegativeButton(activity.getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(myAppSettings);
                    }
                    dialog.dismiss();
                    if(yesListener!=null){
                        yesListener.onClick(null, GlobalObjects.SUCCESS_CODE);
                    }
                }
            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        activity.requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        return false;
    }

    /**
     * This method will check that Write External permission is given or not
     * @param activity reference of the activity to check permission
     * @return returns true if the Write External permission is given else return false.
     */
    public static boolean isContactPermissionsGranted(final Activity activity){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }



    /**
     * This method will check that Write External permission is given or not
     * @param context reference of the activity to check permission
     * @return returns true if the Write External permission is given else return false.
     */
    public static boolean isContactPermissionsGranted(final Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }
}
