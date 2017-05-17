package org.wfp.offlinepayment.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import org.wfp.offlinepayment.asynctasks.SendLogAsync;

/**
 * Crash Error Reporter
 * 
 * Captures any Uncaught exception to capture the conditions that caused the event.
 * The event will be written to the Log file and can be retrieved through LogCat.
 * In addition, the report will attempt to send an e-mail to <string name="CrashErrorReport_MailTo">xyz@GMail.com</string>
 * through the default mail application 
 *  * 
 */

public class ErrorLogUtility implements Thread.UncaughtExceptionHandler {
    
    private static final String LOG_TAG = "ErrorLogUtility";

    private Thread.UncaughtExceptionHandler mDfltExceptionHandler;
    private static ErrorLogUtility S_mInstance;
    private static Context mCurContext;

    // Even though these should be private variables, 
    // they are public to avoid creating getters and setters
    String mPkg_VersionName;
    String mPkg_PackageName;
    String mCtx_FilePath;
    
    String mPkg_OSBld_PhoneModel;
    String mPkg_OSBld_AndroidVersion;
    String mPkg_OSBld_Board;
    String mPkg_OSBld_Brand;
    String mPkg_OSBld_Device;
    String mPkg_OSBld_Display;
    String mPkg_OSBld_FingerPrint;
    String mPkg_OSBld_Host; 
    String mPkg_OSBld_ID;
    String mPkg_OSBld_Manufacturer;
    String mPkg_OSBld_Model;
    String mPkg_OSBld_Product;
    String mPkg_OSBld_Tags;
    long   mPkg_OSBld_Time;
    String mPkg_OSBld_Type;
    String mPkg_OSBld_User;

    /** 
     * Manages the uncaught exception.
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.d(LOG_TAG, "@Override uncaughtException");
       
        Date CurDate = new Date();

        String Report = "Error Report collected on : " + CurDate.toString() + "\n\n";
        Report += "Environment Details : \n";
        Report += "===================== \n";
        Report += CreateInformationString();
        
        Report += "Stack : \n";
        Report += "======= \n";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        Report += stacktrace  + "\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = e.getCause();
        while (cause != null) {
            Report += "Cause : \n";
            Report += "======= \n";
            cause.printStackTrace( printWriter );
            Report += result.toString();
            cause = cause.getCause();
        }
        printWriter.close();
        Report += "**** End of current Report ***";
        SaveAsFile(Report);
        
        // Try and send out the report now before calling the default handler
       CheckCrashErrorAndSendLog(mCurContext);
       
       mDfltExceptionHandler.uncaughtException(t, e);
    }
    
    /** 
     * Capture the newly created instance for singleton class management
     */
    public static ErrorLogUtility getInstance() {
        if ( S_mInstance == null )
            S_mInstance = new ErrorLogUtility();
        return S_mInstance;
    }

    public void Init( Context context ) {
        mDfltExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler( this ); 
        mCurContext = context;
    }
    
    /** 
     * Obtains the available internal memory size 
     * @return long  - integer with memory size available
     */
    public long getAvailableInternalMemorySize() { 
        File   path = Environment.getDataDirectory(); 
        StatFs stat = new StatFs(path.getPath()); 
        
        // place in stack variables for debugging purposes.
        long blockSize       = stat.getBlockSize(); 
        long availableBlocks = stat.getAvailableBlocks(); 
        
        return( availableBlocks * blockSize ); 
    } 

    /**
     * Obtains the Total internal memory size
     * @return long - integer with total memory size
     */
    public long getTotalInternalMemorySize() { 
        File path = Environment.getDataDirectory(); 
        StatFs stat = new StatFs(path.getPath());
        
        // place in stack variables for debugging purposes.
        long blockSize = stat.getBlockSize(); 
        long totalBlocks = stat.getBlockCount();
        
        return( totalBlocks * blockSize ); 
    } 
    
    void CollectPackageInformation( Context context )     {
        Log.d(LOG_TAG, "@CollectPackageInformation");

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo    pi = pm.getPackageInfo(context.getPackageName(), 0);
            
            mPkg_VersionName        = pi.versionName;
            mPkg_PackageName        = pi.packageName;
            mCtx_FilePath           = context.getFilesDir().getAbsolutePath();
            
            mPkg_OSBld_PhoneModel         = android.os.Build.MODEL;
            mPkg_OSBld_AndroidVersion     = android.os.Build.VERSION.RELEASE;
            mPkg_OSBld_Board              = android.os.Build.BOARD;
            mPkg_OSBld_Brand             = android.os.Build.BRAND;
            mPkg_OSBld_Device             = android.os.Build.DEVICE;
            mPkg_OSBld_Display             = android.os.Build.DISPLAY;
            mPkg_OSBld_FingerPrint         = android.os.Build.FINGERPRINT;
            mPkg_OSBld_Host             = android.os.Build.HOST;
            mPkg_OSBld_ID                 = android.os.Build.ID;
            mPkg_OSBld_Model             = android.os.Build.MODEL;
            mPkg_OSBld_Product             = android.os.Build.PRODUCT;
            mPkg_OSBld_Tags             = android.os.Build.TAGS;
            mPkg_OSBld_Time             = android.os.Build.TIME;
            mPkg_OSBld_Type             = android.os.Build.TYPE;
            mPkg_OSBld_User             = android.os.Build.USER;
        } catch( Exception e ) {
            Log.e(LOG_TAG, "!Error CollectPackageInformation: " + e.getMessage());
            // e.printStackTrace()
        }
    }
    
    /**
     * Assemble the package information in a string format
     * @return String - Package information collected
     */
    private String CreateInformationString() {
        CollectPackageInformation( mCurContext );

        String ReturnVal = "";
        ReturnVal  = "  Version  : " + mPkg_VersionName + "\n";
        ReturnVal += "  Package  : " + mPkg_PackageName + "\n";
        ReturnVal += "  FilePath : " + mCtx_FilePath    + "\n\n";
        ReturnVal += "  Package Data \n";
        ReturnVal += "      Phone Model : " + mPkg_OSBld_PhoneModel     + "\n";
        ReturnVal += "      Android Ver : " + mPkg_OSBld_AndroidVersion + "\n";
        ReturnVal += "      Board       : " + mPkg_OSBld_Board          + "\n";
        ReturnVal += "      Brand       : " + mPkg_OSBld_Brand          + "\n";
        ReturnVal += "      Device      : " + mPkg_OSBld_Device         + "\n";
        ReturnVal += "      Display     : " + mPkg_OSBld_Display        + "\n";
        ReturnVal += "      Finger Print: " + mPkg_OSBld_FingerPrint    + "\n";
        ReturnVal += "      Host        : " + mPkg_OSBld_Host           + "\n";
        ReturnVal += "      ID          : " + mPkg_OSBld_ID             + "\n";
        ReturnVal += "      Model       : " + mPkg_OSBld_Model          + "\n";
        ReturnVal += "      Product     : " + mPkg_OSBld_Product        + "\n";
        ReturnVal += "      Tags        : " + mPkg_OSBld_Tags           + "\n";
        ReturnVal += "      Time        : " + mPkg_OSBld_Time           + "\n";
        ReturnVal += "      Type        : " + mPkg_OSBld_Type           + "\n";
        ReturnVal += "      User        : " + mPkg_OSBld_User           + "\n";
        ReturnVal += "  Internal Memory\n";
        ReturnVal += "      Total    : " + (getTotalInternalMemorySize()     /1024) + "k\n";
        ReturnVal += "      Available: " + (getAvailableInternalMemorySize() /1024) + "k\n\n";

        return ReturnVal;
    }
    
    /**
     *Saves the Crash Report to a File with the name stack-timestamp.stacktrace in SD card
     * 
           */
    private void SaveAsFile( String ErrorContent ) {
        try    {
            long timestamp = System.currentTimeMillis();
            String ErrFileName = "stack-" + timestamp + ".stacktrace";
            
            FileOutputStream trace = mCurContext.openFileOutput( ErrFileName, Context.MODE_PRIVATE);
            trace.write(ErrorContent.getBytes());
            trace.flush();
            trace.close();
            Log.e(LOG_TAG, "!Error Report: " + ErrFileName + "\n" + ErrorContent);
        } catch( Exception e ) {
            Log.e(LOG_TAG, "!Error SaveAsFile: " + e.getMessage());
        }
    }

    /**
     * Returns an array containing the names of available crash report files.
     * 
     * @return an array containing the names of available crash report files.
     */
    private String[] GetCrashErrorFileList() {
        File dir = mCurContext.getFilesDir();

        Log.d(LOG_TAG, "Looking for error files in " + dir.getAbsolutePath());

        // Filter for "stack trace" files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".stacktrace");
            }
        };
        return dir.list(filter);
    }
    
    /**
     * Checks to see if there are any crash reports to send and sends them if they exist
     * Once finished with the report, then the report file is deleted from the system
     * 
     * @param _context
     */
    public void CheckCrashErrorAndSendLog(Context _context ) 
    {
        try {
            if( null == mCtx_FilePath ) {
                mCtx_FilePath = _context.getFilesDir().getAbsolutePath();
            }
            String[] reportFilesList = GetCrashErrorFileList();
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(reportFilesList));
            if((null != reportFilesList) && (0 < reportFilesList.length)) {
                
                String line;
                String WholeErrorText = "";
                int curIndex = 0;
                final int MaxSendMail = 5;
                
                for ( String curString : sortedFiles ) 
                {
                    if ( curIndex++ <= MaxSendMail ) 
                    {
                        WholeErrorText+="New Trace collected :\n";
                        WholeErrorText+="=====================\n ";
                        String filePath = mCtx_FilePath + "/" + curString;
                        BufferedReader input = new BufferedReader(new FileReader(filePath));
                        while (( line = input.readLine()) != null) {
                            WholeErrorText += line + "\n";
                        }
                        input.close();
                    }

                    // DELETE FILES !!!!
                    File curFile = new File( mCtx_FilePath + "/" + curString );
                    curFile.delete();
                }
                // SendCrashErrorMail( _context , WholeErrorText,_context.getString(R.string.CrashErrorReport_MailTo) );

                new SendLogAsync(_context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WholeErrorText);
                
            }
        } catch( Exception e ) {
            Log.e(LOG_TAG, "!Error CheckCrashErrorAndSendMail: " + e.getMessage());
            // e.printStackTrace();
        }
    }
    
    /**
     * Send out the crash error report via e-mail
     * 
     * @param _context
     * @param ErrorContent
     */
    private void SendCrashErrorMail( Context _context, String ErrorContent,String mailTo)    {
//        Log.d(LOG_TAG, "SendCrashErrorMail: " + _context.getString(R.string.CrashErrorReport_MailTo)); 
//        
//        //Toast.makeText(_context, _context.getString(R.string.CrashErrorReport_ToastText), Toast.LENGTH_LONG).show();
//
//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sendIntent.putExtra(Intent.EXTRA_EMAIL,    new String[]{_context.getString(R.string.CrashErrorReport_MailTo)});
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, _context.getString(R.string.CrashErrorReport_MailSubject));
//        sendIntent.putExtra(Intent.EXTRA_TEXT, ErrorContent + "\n");
//        sendIntent.setType("message/rfc822");
//        _context.startActivity( sendIntent );
    }
    
// End Class    
}