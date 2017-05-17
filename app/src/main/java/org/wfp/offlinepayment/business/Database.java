package org.wfp.offlinepayment.business;

import java.util.ArrayList;

import org.wfp.offlinepayment.exceptions.AuthTokenExpiredException;
import org.wfp.offlinepayment.exceptions.DatabaseUpdateException;
import org.wfp.offlinepayment.exceptions.EmptyAuthTokenException;
import org.wfp.offlinepayment.model.BeneficiaryModel;
import org.wfp.offlinepayment.model.LoginModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class Database extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/org.wfp.offlinepayment/databases/";
	private static final String NAME = "offlinepayment.db";
	private static final int VERSION = 1;

	private static final String TABLE_LOGINDATA = "LOGINDATA";
	private static final String TABLE_BENEFICIARY = "BENEFICIARY";

	public static final String COLUMN_ID = "Id";
    public static final String COLUMN_PAYMENT_ID = "PaymentId";
    public static final String COLUMN_PAYMENT_CYCLE = "PaymentCycle";
    public static final String COLUMN_DISTRICT = "District";
    public static final String COLUMN_TEHSIL = "Tehsil";
    public static final String COLUMN_UC = "UC";
    public static final String COLUMN_VILLAGE = "Village";
    public static final String COLUMN_ADDRESS = "Address";
    public static final String COLUMN_SCHOOL = "School";
    public static final String COLUMN_BENEFICIARY_CNIC = "BeneficiaryCNIC";
    public static final String COLUMN_BENEFICIARY_NAME = "BeneficiaryName";
    public static final String COLUMN_FATHER_NAME = "FatherName";
    public static final String COLUMN_AMONUT = "Amount";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_DATE_DOWNLOADED = "DateDownloaded";
	public static final String COLUMN_IS_PAID = "IsPaid";
	public static final String COLUMN_DATE_PAID = "DatePaid";
    public static final String COLUMN_IS_SYNCED = "IsSynced";
    public static final String COLUMN_DATE_SYNCED = "DateSynced";

	private static final String COLUMN_LOGINDATA_AUTHTOKEN = "AuthToken";
	private static final String COLUMN_LOGINDATA_AUTHTOKEN_EXPIRY = "AuthTokenExpiry";
	private static final String COLUMN_LOGINDATA_USERID = "USER_ID";
	private static final String COLUMN_LOGINDATA_USERNAME = "USERNAME";
	private static final String COLUMN_LOGINDATA_ACTIVE = "ACTIVE";

	public Database(Context context) {
		super(context, NAME, null, VERSION);
	}

	public static Database DbInstance = null;

	public static synchronized Database getInstance(Context context)
	{
		if (DbInstance == null)
		{
			DbInstance = new Database(context.getApplicationContext());
		}
		return DbInstance;
	}

	public synchronized static SQLiteDatabase GetSqliteHelper(Context context)
	{
		return getInstance(context).getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

		String sqllogin = "create table " + TABLE_LOGINDATA + "(" + BaseColumns._ID + " integer primary key autoincrement," +
				COLUMN_LOGINDATA_AUTHTOKEN + " text, " +
				COLUMN_LOGINDATA_AUTHTOKEN_EXPIRY + " text," +
				COLUMN_LOGINDATA_USERNAME + " text," +
				COLUMN_LOGINDATA_ACTIVE + " integer," +
				COLUMN_LOGINDATA_USERID + " integer);";

		String sql = "create table " + TABLE_BENEFICIARY + "(" +
				COLUMN_ID + " text primary key," +
                COLUMN_PAYMENT_ID + " text ," +
                COLUMN_PAYMENT_CYCLE + " text ," +
				COLUMN_DISTRICT + " text," +
				COLUMN_TEHSIL + " text," +
				COLUMN_UC + " text," +
				COLUMN_VILLAGE + " text," +
				COLUMN_ADDRESS + " text," +
				COLUMN_SCHOOL + " text," +
				COLUMN_BENEFICIARY_CNIC + " text," +
				COLUMN_BENEFICIARY_NAME + " text," +
				COLUMN_FATHER_NAME + " text," +
				COLUMN_AMONUT + " integer," +
				COLUMN_STATUS + " integer," +
				COLUMN_DATE_DOWNLOADED + " text," +
				COLUMN_IS_PAID + " integer," +
				COLUMN_DATE_PAID + " text," +
				COLUMN_IS_SYNCED + " integer," +
				COLUMN_DATE_SYNCED + " text);";

		db.execSQL(sqllogin);
		db.execSQL(sql);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

	public static synchronized int writeBeneficiaries(Context context, ArrayList<BeneficiaryModel> beneficiaryModels)
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);
		int imported = 0;
		try
		{

			wdb.beginTransaction();
			for (int i = 0; i < beneficiaryModels.size(); i++)
			{
				if (writeBeneficiary(wdb, beneficiaryModels.get(i)) != -1)
					imported++;

			}
			wdb.setTransactionSuccessful();
		} catch (Exception ex)
		{
			imported = -1;
		}
		finally
		{
			wdb.endTransaction();
		}

		wdb.close();
		return imported;

	}

	private static synchronized long writeBeneficiary(SQLiteDatabase wdb, BeneficiaryModel beneficiary)
	{
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ID, beneficiary.getId());
        cv.put(COLUMN_PAYMENT_ID, beneficiary.getPaymentId());
		cv.put(COLUMN_PAYMENT_CYCLE, beneficiary.getPaymentCycle());
		cv.put(COLUMN_DISTRICT, beneficiary.getDistrict());
		cv.put(COLUMN_TEHSIL, beneficiary.getTehsil());
		cv.put(COLUMN_UC, beneficiary.getUc());
		cv.put(COLUMN_VILLAGE, beneficiary.getVillage());
        cv.put(COLUMN_ADDRESS, beneficiary.getAddress());
        cv.put(COLUMN_SCHOOL, beneficiary.getSchool());
        cv.put(COLUMN_BENEFICIARY_CNIC, beneficiary.getSchool());
        cv.put(COLUMN_BENEFICIARY_NAME, beneficiary.getBeneficiaryName());
        cv.put(COLUMN_FATHER_NAME, beneficiary.getFatherName());
        cv.put(COLUMN_AMONUT, beneficiary.getAmount());
        cv.put(COLUMN_STATUS, beneficiary.getStatus());

		cv.put(COLUMN_DATE_DOWNLOADED, DateUtility.formatDateTime(new java.util.Date()));
		return wdb.insert(TABLE_BENEFICIARY, null, cv);
	}

	public static synchronized ArrayList<BeneficiaryModel> getAll(Context context)
	{
		SQLiteDatabase rdb = GetSqliteHelper(context);
		ArrayList<BeneficiaryModel> listmodel = new ArrayList<BeneficiaryModel>();
        String cols[] = new String[] { COLUMN_ID, COLUMN_PAYMENT_ID, COLUMN_PAYMENT_CYCLE, COLUMN_DISTRICT,COLUMN_TEHSIL,COLUMN_UC, COLUMN_VILLAGE, COLUMN_ADDRESS, COLUMN_SCHOOL,
                COLUMN_BENEFICIARY_CNIC,COLUMN_BENEFICIARY_NAME, COLUMN_FATHER_NAME, COLUMN_AMONUT, COLUMN_STATUS , COLUMN_DATE_DOWNLOADED, COLUMN_IS_PAID, COLUMN_DATE_PAID, COLUMN_IS_SYNCED, COLUMN_DATE_SYNCED };

        Cursor results = null;

		results = rdb.query(TABLE_BENEFICIARY, cols, null, null, null, null, COLUMN_PAYMENT_ID + " ASC");

		while (results.moveToNext())
		{
            BeneficiaryModel model = getFromCusor(results);
            listmodel.add(model);
		}

		results.close();
		rdb.close();
		return listmodel;
	}

	public static synchronized BeneficiaryModel getByPaymentId(Context context, String PaymentId)
	{
		SQLiteDatabase rdb = GetSqliteHelper(context);
        BeneficiaryModel model = null;
		String cols[] = new String[] { COLUMN_ID, COLUMN_PAYMENT_ID, COLUMN_PAYMENT_CYCLE, COLUMN_DISTRICT,COLUMN_TEHSIL,COLUMN_UC, COLUMN_VILLAGE, COLUMN_ADDRESS, COLUMN_SCHOOL,
				COLUMN_BENEFICIARY_CNIC,COLUMN_BENEFICIARY_NAME, COLUMN_FATHER_NAME, COLUMN_AMONUT, COLUMN_STATUS , COLUMN_DATE_DOWNLOADED, COLUMN_IS_PAID, COLUMN_DATE_PAID, COLUMN_IS_SYNCED, COLUMN_DATE_SYNCED };

		Cursor results = rdb.query(TABLE_BENEFICIARY, cols, COLUMN_PAYMENT_ID + " = ?", new String[] { PaymentId }, null, null, null);

		if (results.moveToFirst())
		{
            model  = getFromCusor(results);
		}

		results.close();
		rdb.close();
		return model;
	}

	public static synchronized ArrayList<BeneficiaryModel> getNotSynced(Context context)
	{
		SQLiteDatabase rdb = GetSqliteHelper(context);
		ArrayList<BeneficiaryModel> listmodel = new ArrayList<BeneficiaryModel>();
		String cols[] = new String[] { COLUMN_ID, COLUMN_PAYMENT_ID, COLUMN_PAYMENT_CYCLE, COLUMN_DISTRICT,COLUMN_TEHSIL,COLUMN_UC, COLUMN_VILLAGE, COLUMN_ADDRESS, COLUMN_SCHOOL,
				COLUMN_BENEFICIARY_CNIC,COLUMN_BENEFICIARY_NAME, COLUMN_FATHER_NAME, COLUMN_AMONUT, COLUMN_STATUS , COLUMN_DATE_DOWNLOADED, COLUMN_IS_PAID, COLUMN_DATE_PAID, COLUMN_IS_SYNCED, COLUMN_DATE_SYNCED };

		Cursor results = rdb.query(TABLE_BENEFICIARY, cols, COLUMN_IS_SYNCED + " = ? AND " + COLUMN_IS_PAID + " = ? ", new String[] { "0", "1" }, null, null, null);

		while (results.moveToNext())
		{
			BeneficiaryModel model = getFromCusor(results);
			listmodel.add(model);
		}

		results.close();
		rdb.close();
		return listmodel;
	}


	public static synchronized BeneficiaryModel getFromCusor(Cursor cursor) {
		BeneficiaryModel model = new BeneficiaryModel();
		model.setId(cursor.getString(0));
        model.setPaymentId(cursor.getString(1));
		model.setPaymentCycle(cursor.getString(2));
		model.setDistrict(cursor.getString(3));
		model.setTehsil(cursor.getString(4));
		model.setUc(cursor.getString(5));
		model.setVillage(cursor.getString(6));
		model.setAddress(cursor.getString(7));
		model.setSchool(cursor.getString(8));
		model.setBeneficiaryCNIC(cursor.getString(9));
		model.setBeneficiaryName(cursor.getString(10));
		model.setFatherName(cursor.getString(11));
		model.setAmount(cursor.getInt(12));
		model.setStatus(cursor.getInt(13));
		model.setDateDownloaded(DateUtility.toDateTime(cursor.getString(14)));
		model.setPaid(cursor.getInt(15) == 1);
		model.setDatePaid(DateUtility.toDateTime(cursor.getString(16)));
        model.setSynced(cursor.getInt(17) == 1);
        model.setDateSynced(DateUtility.toDateTime(cursor.getString(18)));
        return model;
	}

	public static synchronized void setPaymentSynced(Context context, String paymentId) throws DatabaseUpdateException
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_IS_SYNCED, 1);
		cv.put(COLUMN_DATE_SYNCED, DateUtility.formatDateTime(new java.util.Date()));
		if (wdb.update(TABLE_BENEFICIARY, cv, COLUMN_PAYMENT_ID + " = ?", new String[] {paymentId}) != 1)
			throw new DatabaseUpdateException();

		wdb.close();
	}

	public static synchronized int savePayment(Context context, String paymentId, int status)
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_STATUS, status);
		cv.put(COLUMN_IS_PAID, 1);
		cv.put(COLUMN_DATE_PAID, DateUtility.formatDateTime(new java.util.Date()));
		int result = wdb.update(TABLE_BENEFICIARY, cv, COLUMN_PAYMENT_ID + " = ?", new String[] {paymentId});

		wdb.close();

		return result;
	}

    public static LoginModel GetLoginData(Context context) throws EmptyAuthTokenException, AuthTokenExpiredException
    {
        SQLiteDatabase rdb = GetSqliteHelper(context);
        String cols[] = { COLUMN_LOGINDATA_AUTHTOKEN_EXPIRY, COLUMN_LOGINDATA_AUTHTOKEN, COLUMN_LOGINDATA_USERID, COLUMN_LOGINDATA_USERNAME, COLUMN_LOGINDATA_ACTIVE};
        Cursor results = rdb.query(TABLE_LOGINDATA, cols, COLUMN_LOGINDATA_ACTIVE + " = ?", new String[] { String.valueOf(1) }, null, null, "_id DESC");

        if (results.moveToFirst())
        {
            String authToken = results.getString(1);

            if (authToken.equals(""))
                throw new EmptyAuthTokenException();

            long expiryTime = Long.parseLong(results.getString(0));

            if (expiryTime > ProviderUtility.GetMilisTimeToLong())
            {
                LoginModel model = new LoginModel();
                model.setAuthToken(authToken);
                model.setUserId(results.getInt(2));
                model.setUserName(results.getString(3));

                results.close();
                rdb.close();
                return model;
            }
            else
            {
                results.close();
                rdb.close();
                throw new AuthTokenExpiredException();
            }
        }
        else
        {
            results.close();
            rdb.close();
            throw new NullPointerException();
        }

    }

	public static synchronized long WriteLoginData(Context context, LoginModel loginModel)
	{

		SQLiteDatabase wdb = GetSqliteHelper(context);

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOGINDATA_AUTHTOKEN, loginModel.getAuthToken());
		cv.put(COLUMN_LOGINDATA_AUTHTOKEN_EXPIRY, String.valueOf(loginModel.getTime()));
		cv.put(COLUMN_LOGINDATA_USERID, loginModel.getUserId());
		cv.put(COLUMN_LOGINDATA_USERNAME, loginModel.getUserName());
		cv.put(COLUMN_LOGINDATA_ACTIVE, 1);
		long no = wdb.insert(TABLE_LOGINDATA, null, cv);
		wdb.close();
		return no;
	}

	public static synchronized void ActivateLoginData(Context context) throws EmptyAuthTokenException
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);

		String query = "SELECT MAX(_id) FROM " + TABLE_LOGINDATA;
		Cursor results = wdb.rawQuery(query, null);

		int id = -1;
		if (results.moveToFirst())
		{
			id = results.getInt(0);
		}

		if (id == -1)
		{
			results.close();
			wdb.close();
			throw new EmptyAuthTokenException();
		}

		results.close();

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOGINDATA_ACTIVE, 1);
		wdb.update(TABLE_LOGINDATA, cv, "_id = ?", new String[] { String.valueOf(id) });

		wdb.close();
	}

	public static synchronized void LogOff(Context context)
	{

		SQLiteDatabase wdb = GetSqliteHelper(context);

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LOGINDATA_ACTIVE, 0);
		wdb.update(TABLE_LOGINDATA, cv, COLUMN_LOGINDATA_ACTIVE + " = ?", new String[] { String.valueOf(1) });
		wdb.close();
	}

}
