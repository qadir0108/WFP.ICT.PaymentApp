package org.wfp.offlinepayment.db;

import java.util.ArrayList;

import org.wfp.offlinepayment.business.DateUtility;
import org.wfp.offlinepayment.business.ProviderUtility;
import org.wfp.offlinepayment.enums.BeneficiaryEnum;
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
                BeneficiaryEnum.Id.Value + " text primary key," +
                BeneficiaryEnum.PaymentId.Value + " text ," +
                BeneficiaryEnum.PaymentCycle.Value + " text ," +
                BeneficiaryEnum.District.Value + " text," +
                BeneficiaryEnum.Tehsil.Value + " text," +
                BeneficiaryEnum.SchoolId.Value + " text," +
                BeneficiaryEnum.SchoolName.Value + " text," +
                BeneficiaryEnum.StudentId.Value + " text," +
                BeneficiaryEnum.StudentName.Value + " text," +
                BeneficiaryEnum.DateOfBirth.Value + " text," +
                BeneficiaryEnum.StudentClass.Value + " text," +
                BeneficiaryEnum.BeneficiaryCNIC.Value + " text," +
                BeneficiaryEnum.BeneficiaryName.Value + " text," +
                BeneficiaryEnum.Amount.Value + " integer," +
                BeneficiaryEnum.Status.Value + " integer," +
                BeneficiaryEnum.DateDownloaded.Value + " text," +
                BeneficiaryEnum.IsPaid.Value + " integer," +
                BeneficiaryEnum.DatePaid.Value + " text," +
                BeneficiaryEnum.LatPaid.Value + " text," +
                BeneficiaryEnum.LngPaid.Value + " text," +
                BeneficiaryEnum.IsSynced.Value + " integer," +
                BeneficiaryEnum.DateSynced.Value + " text);";

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

		int imported = 0;
		try
		{

			for (int i = 0; i < beneficiaryModels.size(); i++)
			{
                boolean alreadyExist = true;
                BeneficiaryModel model = beneficiaryModels.get(i);
                BeneficiaryModel already = getByPaymentId(context, model.getPaymentId());

                if(already == null)
				if (writeBeneficiary(context, model) != -1)
					imported++;

			}
		} catch (Exception ex)
		{
			imported = -1;
		}
		finally
		{
		}
		return imported;

	}

	private static synchronized long writeBeneficiary(Context context, BeneficiaryModel beneficiary)
	{
        SQLiteDatabase wdb = GetSqliteHelper(context);
		ContentValues cv = new ContentValues();
		cv.put(BeneficiaryEnum.Id.Value, beneficiary.getId());
        cv.put(BeneficiaryEnum.PaymentId.Value, beneficiary.getPaymentId());
		cv.put(BeneficiaryEnum.PaymentCycle.Value, beneficiary.getPaymentCycle());
		cv.put(BeneficiaryEnum.District.Value, beneficiary.getDistrict());
		cv.put(BeneficiaryEnum.Tehsil.Value, beneficiary.getTehsil());
		cv.put(BeneficiaryEnum.SchoolId.Value, beneficiary.getSchoolId());
		cv.put(BeneficiaryEnum.SchoolName.Value, beneficiary.getSchoolName());
        cv.put(BeneficiaryEnum.StudentId.Value, beneficiary.getStudentId());
        cv.put(BeneficiaryEnum.StudentName.Value, beneficiary.getStudentName());
        cv.put(BeneficiaryEnum.DateOfBirth.Value, beneficiary.getDateOfBirth());
        cv.put(BeneficiaryEnum.StudentClass.Value, beneficiary.getStudentClass());
        cv.put(BeneficiaryEnum.BeneficiaryCNIC.Value, beneficiary.getBeneficiaryCNIC());
        cv.put(BeneficiaryEnum.BeneficiaryName.Value, beneficiary.getBeneficiaryName());
        cv.put(BeneficiaryEnum.Amount.Value, beneficiary.getAmount());
        cv.put(BeneficiaryEnum.Status.Value, beneficiary.getStatus());
		cv.put(BeneficiaryEnum.DateDownloaded.Value, DateUtility.formatDateTime(new java.util.Date()));
		long result = wdb.insert(TABLE_BENEFICIARY, null, cv);
        wdb.close();
        return result;
	}

	public static synchronized ArrayList<BeneficiaryModel> getAll(Context context)
	{
		SQLiteDatabase rdb = GetSqliteHelper(context);
		ArrayList<BeneficiaryModel> listmodel = new ArrayList<BeneficiaryModel>();
        String cols[] = new String[] { BeneficiaryEnum.Id.Value,
                BeneficiaryEnum.PaymentId.Value,BeneficiaryEnum.PaymentCycle.Value,BeneficiaryEnum.District.Value,
                BeneficiaryEnum.Tehsil.Value, BeneficiaryEnum.SchoolId.Value,BeneficiaryEnum.SchoolName.Value,
                BeneficiaryEnum.StudentId.Value,BeneficiaryEnum.StudentName.Value,BeneficiaryEnum.DateOfBirth.Value,
                BeneficiaryEnum.StudentClass.Value,BeneficiaryEnum.BeneficiaryCNIC.Value,BeneficiaryEnum.BeneficiaryName.Value,
                BeneficiaryEnum.Amount.Value,BeneficiaryEnum.Status.Value,BeneficiaryEnum.DateDownloaded.Value,
                BeneficiaryEnum.IsPaid.Value,BeneficiaryEnum.DatePaid.Value,BeneficiaryEnum.LatPaid.Value,
                BeneficiaryEnum.LngPaid.Value,BeneficiaryEnum.IsSynced.Value,BeneficiaryEnum.DateSynced.Value };

        Cursor results = null;

		results = rdb.query(TABLE_BENEFICIARY, cols, null, null, null, null, BeneficiaryEnum.PaymentId.Value + " ASC");

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
        String cols[] = new String[] { BeneficiaryEnum.Id.Value,
                BeneficiaryEnum.PaymentId.Value,BeneficiaryEnum.PaymentCycle.Value,BeneficiaryEnum.District.Value,
                BeneficiaryEnum.Tehsil.Value, BeneficiaryEnum.SchoolId.Value,BeneficiaryEnum.SchoolName.Value,
                BeneficiaryEnum.StudentId.Value,BeneficiaryEnum.StudentName.Value,BeneficiaryEnum.DateOfBirth.Value,
                BeneficiaryEnum.StudentClass.Value,BeneficiaryEnum.BeneficiaryCNIC.Value,BeneficiaryEnum.BeneficiaryName.Value,
                BeneficiaryEnum.Amount.Value,BeneficiaryEnum.Status.Value,BeneficiaryEnum.DateDownloaded.Value,
                BeneficiaryEnum.IsPaid.Value,BeneficiaryEnum.DatePaid.Value,BeneficiaryEnum.LatPaid.Value,
                BeneficiaryEnum.LngPaid.Value,BeneficiaryEnum.IsSynced.Value,BeneficiaryEnum.DateSynced.Value };

		Cursor results = rdb.query(TABLE_BENEFICIARY, cols, BeneficiaryEnum.PaymentId.Value + " = ?", new String[] { PaymentId }, null, null, null);

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
        String cols[] = new String[] { BeneficiaryEnum.Id.Value,
                BeneficiaryEnum.PaymentId.Value,BeneficiaryEnum.PaymentCycle.Value,BeneficiaryEnum.District.Value,
                BeneficiaryEnum.Tehsil.Value, BeneficiaryEnum.SchoolId.Value,BeneficiaryEnum.SchoolName.Value,
                BeneficiaryEnum.StudentId.Value,BeneficiaryEnum.StudentName.Value,BeneficiaryEnum.DateOfBirth.Value,
                BeneficiaryEnum.StudentClass.Value,BeneficiaryEnum.BeneficiaryCNIC.Value,BeneficiaryEnum.BeneficiaryName.Value,
                BeneficiaryEnum.Amount.Value,BeneficiaryEnum.Status.Value,BeneficiaryEnum.DateDownloaded.Value,
                BeneficiaryEnum.IsPaid.Value,BeneficiaryEnum.DatePaid.Value,BeneficiaryEnum.LatPaid.Value,
                BeneficiaryEnum.LngPaid.Value,BeneficiaryEnum.IsSynced.Value,BeneficiaryEnum.DateSynced.Value };

        Cursor results = rdb.query(TABLE_BENEFICIARY, cols, BeneficiaryEnum.IsSynced.Value + " = ? AND " + BeneficiaryEnum.IsPaid.Value + " = ? ", new String[] { "0", "1" }, null, null, null);

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
		model.setSchoolId(cursor.getString(5));
		model.setSchoolName(cursor.getString(6));
		model.setStudentId(cursor.getString(7));
		model.setStudentName(cursor.getString(8));
		model.setDateOfBirth(cursor.getString(9));
		model.setStudentClass(cursor.getString(10));
		model.setBeneficiaryCNIC(cursor.getString(11));
		model.setBeneficiaryName(cursor.getString(12));
		model.setAmount(cursor.getInt(13));
		model.setStatus(cursor.getInt(14));
		model.setDateDownloaded(DateUtility.toDateTime(cursor.getString(15)));
		model.setPaid(cursor.getInt(16) == 1);
		model.setDatePaid(DateUtility.toDateTime(cursor.getString(17)));
        model.setLatPaid(cursor.getString(18));
        model.setLngPaid(cursor.getString(19));
        model.setSynced(cursor.getInt(20) == 1);
        model.setDateSynced(DateUtility.toDateTime(cursor.getString(21)));
        return model;
	}

	public static synchronized void setPaymentSynced(Context context, String paymentId) throws DatabaseUpdateException
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);
		ContentValues cv = new ContentValues();
		cv.put(BeneficiaryEnum.IsSynced.Value, 1);
		cv.put(BeneficiaryEnum.DateSynced.Value, DateUtility.formatDateTime(new java.util.Date()));
		if (wdb.update(TABLE_BENEFICIARY, cv, BeneficiaryEnum.PaymentId.Value + " = ?", new String[] {paymentId}) != 1)
			throw new DatabaseUpdateException();

		wdb.close();
	}

	public static synchronized int savePayment(Context context, String paymentId, int status, String lat, String lng)
	{
		SQLiteDatabase wdb = GetSqliteHelper(context);
		ContentValues cv = new ContentValues();
		cv.put(BeneficiaryEnum.Status.Value, status);
		cv.put(BeneficiaryEnum.IsPaid.Value, 1);
		cv.put(BeneficiaryEnum.LatPaid.Value, lat);
		cv.put(BeneficiaryEnum.LngPaid.Value, lng);

		cv.put(BeneficiaryEnum.DatePaid.Value, DateUtility.formatDateTime(new java.util.Date()));
		int result = wdb.update(TABLE_BENEFICIARY, cv, BeneficiaryEnum.PaymentId.Value + " = ?", new String[] {paymentId});

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
