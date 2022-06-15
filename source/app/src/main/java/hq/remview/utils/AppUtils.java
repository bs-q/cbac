package hq.remview.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hq.remview.data.model.api.obj.RestaurantSetting;
import timber.log.Timber;

public class AppUtils {
    public static final String DATE_REQUEST_FORMAT = "yyyy-MM-dd";

    private AppUtils() {
        // This class is not publicly instantiable
    }

    public static void openPlayStoreForApp(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static int getAppVersionCode(Context context) {
        PackageInfo pInfo = null;
        int versionCode = 0;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                versionCode = pInfo.versionCode;
            } else {
                versionCode = (int) pInfo.getLongVersionCode();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static String getAppVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(context), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
           LogService.e(e);
            return "";
        }
    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(context), 0);
            return  (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            LogService.e(e);
            return null;
        }

    }

    public static void openApp(Context context, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(context.getPackageName(), className);
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    public static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void unInstallApp(Context context, String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getApkFilePackage(Context context, File apkFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo.packageName;
        }
        return null;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String formatDate(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static double converDouble(RestaurantSetting setting, String doub){
        String des = doub.replace(setting.getNumberGroup(), "")
                .replace(setting.getNumberDecimal(), ".")
                .replace("%", "").replace(setting.getCurrency(), "");
        try {
            return Double.parseDouble(des);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getDoubleTwoDecimal(RestaurantSetting setting, double d)
    {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(setting.getNumberDecimal().charAt(0));
        decimalFormatSymbols.setGroupingSeparator(setting.getNumberGroup().charAt(0));
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);

        int i=(int)(d);
        double k = d-(double)i;
        if(k>0){
            return decimalFormat.format(Math.abs(d));
        }else{
            String rs =decimalFormat.format(Math.abs(d));
            return rs.substring(0,rs.indexOf(setting.getCurrency()));
        }

    }

    public static int convertToCent(double b){
        int i=(int)(b);
        double k = b-(double)i;
        if(k>0.5 && k<1){
            i+=1;
        }
        return i;
    }

    public static String formatDoubleMoneyHasCurrency(double d, RestaurantSetting setting)
    {
        double db = (double)Math.round(d*100)/100;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(setting.getNumberDecimal().charAt(0));
        decimalFormatSymbols.setGroupingSeparator(setting.getNumberGroup().charAt(0));
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        return decimalFormat.format(db) +" "+ setting.getCurrency();
    }

    public static int parseInt(String input){
        try {
            return  Integer.parseInt(input);
        }catch (Exception e){
            return 0;
        }
    }
    public static Set<Integer> getPermissionSet(Integer x)
    {
        ArrayList<Integer> v = new ArrayList<>();
        Set<Integer> permission = new HashSet<>();
        while (x > 0)
        {
            Timber.d(x.toString());
            v.add(x % 2);
            x = x / 2;
        }
        for (int i = 0; i < v.size(); i++)
        {

            if (v.get(i) == 1)
            {
                permission.add((int)Math.pow(2,i));
            }
        }
        return permission;
    }

}
