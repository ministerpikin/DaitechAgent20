package ique.daitechagent.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ique.daitechagent.common.Common;
import ique.daitechagent.model.Customer;


public class DataContext extends SQLiteOpenHelper {

    private final String CUSTOMER_TABLE = "Customers";

    public DataContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Common.DATABASE_NAME, factory, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tblCustomers = "CREATE TABLE IF NOT EXISTS "+CUSTOMER_TABLE+" " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_number TEXT NOT NULL, " +
                "customer_name TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "contact_person TEXT NOT NULL, " +
                "phone_number TEXT NOT NULL, " +
                "email TEXT DEFAULT '', " +
                "lng REAL, " +
                "lat REAL, " +
                "customer_rating INTEGER DEFAULT 0, " +
                "syncd INTEGER DEFAULT 0);";

        db.execSQL(tblCustomers);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String dropCustomers = "DROP TABLE IF EXISTS "+CUSTOMER_TABLE+";";
        db.execSQL(dropCustomers);

        onCreate(db);
    }

    public ArrayList<Customer> getCustomerList() {

        ArrayList<Customer> customerList = new ArrayList<>();
        String query = "SELECT * FROM "+CUSTOMER_TABLE+";";

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            try {
                Customer customer = new Customer();
                customer.setCustomer_auto_number(c.getInt(c.getColumnIndex("id")));
                customer.setCustomername(c.getString(c.getColumnIndex("customer_name")));
                customer.setCustomernumber(c.getString(c.getColumnIndex("customer_number")));
                customer.setAddress(c.getString(c.getColumnIndex("address")));
                customer.setContactperson(c.getString(c.getColumnIndex("contact_person")));
                customer.setPhoneno(c.getString(c.getColumnIndex("phone_number")));
                customer.setEmail(c.getString(c.getColumnIndex("email")));
                customer.setCustomerrating(c.getInt(c.getColumnIndex("customer_rating")));
                customer.setLat(c.getDouble(c.getColumnIndex("lat")));
                customer.setLng(c.getDouble(c.getColumnIndex("lng")));

                customerList.add(customer);
                c.moveToNext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();

        Collections.sort(customerList, new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getCustomername().compareTo(o2.getCustomername());
            }
        });

        return customerList;

    }

    public void refreshCustomerListX(ArrayList<Customer> customerList) {
        // Escape the string guy...na wa for you oo
        // DatabaseUtils.appendEscapedSQLString(escaper, value);
        for (Customer item : customerList) {
            // check if user already exists
            if (checkCustomerAlreadyExists(item.getCustomernumber()) == 0) {
                // insert
                SQLiteDatabase db = getWritableDatabase();
                String query = "INSERT INTO "+CUSTOMER_TABLE+" (customer_name, customer_number, address,contact_person, phone_number, email, lat, lng) " +
                        "values ('" + item.getCustomername() + "', '" + item.getCustomernumber() + "', '" + item.getAddress() + "', " +
                        "'" + item.getContactperson() + "', '" + item.getPhoneno() + "', " +
                        "'" + item.getEmail() + "', '" + item.getLat() + "', " +
                        "'" + item.getLng() + "');";
                db.execSQL(query);
                // db.close();
            } else {
                // We gonna update the existing data
                SQLiteDatabase db = getWritableDatabase();
                String query = "UPDATE "+CUSTOMER_TABLE+
                        " SET customer_name = '"+item.getCustomername() + "', " +
                        " customer_number = '"+item.getCustomernumber() + "', " +
                        " address = '"+item.getAddress() + "', " +
                        " contact_person = '"+item.getContactperson() + "', " +
                        " phone_number = '"+item.getPhoneno() + "', " +
                        " lat = '"+item.getLat() + "', " +
                        " lng = '"+item.getLng() + "', " +
                        " email = '"+item.getEmail() + "' " +
                        "WHERE customer_number = '" + item.getCustomernumber() + "';";
                db.execSQL(query);
            }
        }
    }

    public void refreshCustomerList(ArrayList<Customer> customerList) {
        // Escape the string guy...na wa for you oo
        // DatabaseUtils.appendEscapedSQLString(escaper, value);
        SQLiteDatabase db = null;

        try{

            String queryInsert = "INSERT INTO "+CUSTOMER_TABLE+" (customer_name, customer_number, address,contact_person, phone_number, email, lat, lng) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?);";

            String queryUpdate = "UPDATE "+CUSTOMER_TABLE+
                    " SET customer_name = ?, " +
                    " customer_number = ?, " +
                    " address = ?, " +
                    " contact_person = ?, " +
                    " phone_number = ?, " +
                    " lat = ?, " +
                    " lng = ?, " +
                    " email = ? " +
                    "WHERE customer_number = ?;";

            db = getWritableDatabase();
            db.beginTransaction();
            SQLiteStatement statementInserts = db.compileStatement(queryInsert);
            SQLiteStatement statementUpdates = db.compileStatement(queryUpdate);

            for (Customer item : customerList) {
                // check if user already exists
                if (checkCustomerAlreadyExists(item.getCustomernumber()) == 0) {

                    bindValuesInsert(statementInserts, item);
                    statementInserts.executeInsert();

                } else {

                    bindValuesUpdate(statementUpdates, item);
                    statementUpdates.executeUpdateDelete();

                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.w("Exception:", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public int checkCustomerAlreadyExists(String customer_number) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM "+CUSTOMER_TABLE+" where customer_number = ?";
            c = db.rawQuery(query, new String[]{ customer_number });
            if (c.moveToFirst()) {
                int count = c.getInt(0);
                return count; //c.getInt(0);
            }
            return 0;

        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                //db.close();
            }
        }
    }

    protected void bindValuesInsert(@NonNull SQLiteStatement statement, @NonNull Customer item) {


        statement.clearBindings();

        String name = item.getCustomername();
        if (name != null) {
            statement.bindString(1, item.getCustomername());
        }

        String number = item.getCustomernumber();
        if (number != null) {
            statement.bindString(2, item.getCustomernumber());
        }

        String address = item.getAddress();
        if (address != null) {
            statement.bindString(3, item.getAddress());
        }

        String person = item.getContactperson();
        if (person != null) {
            statement.bindString(4, item.getContactperson());
        }

        String fone = item.getPhoneno();
        if (fone != null) {
            statement.bindString(5, item.getPhoneno());
        }

        String email = item.getEmail();
        if (email != null) {
            statement.bindString(6, item.getEmail());
        }

        Double lat = item.getLat();
        if (lat != null) {
            statement.bindDouble(7, item.getLat());
        }

        Double lng = item.getLng();
        if (lng != null) {
            statement.bindDouble(8, item.getLng());
        }

    }

    protected void bindValuesUpdate(@NonNull SQLiteStatement statement, @NonNull Customer item) {

        statement.clearBindings();

        String name = item.getCustomername();
        if (name != null) {
            statement.bindString(1, item.getCustomername());
        }

        String number = item.getCustomernumber();
        if (number != null) {
            statement.bindString(2, item.getCustomernumber());
        }

        String address = item.getAddress();
        if (address != null) {
            statement.bindString(3, item.getAddress());
        }

        String person = item.getContactperson();
        if (person != null) {
            statement.bindString(4, item.getContactperson());
        }

        String fone = item.getPhoneno();
        if (fone != null) {
            statement.bindString(5, item.getPhoneno());
        }

        Double lat = item.getLat();
        if (lat != null) {
            statement.bindDouble(6, item.getLat());
        }

        Double lng = item.getLng();
        if (lng != null) {
            statement.bindDouble(7, item.getLng());
        }

        String email = item.getEmail();
        if (email != null) {
            statement.bindString(8, item.getEmail());
        }

        //String number = item.getCustomernumber();
        if (number != null) {
            statement.bindString(9, item.getCustomernumber());
        }

    }

    public void deleteAllCustomers() {
        String query = "DELETE FROM " + CUSTOMER_TABLE;

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(query);
        }catch (Exception ex){
            Log.e("Exception", ex.getMessage(), ex);
        }

    }

    public void deleteCustomerByEmail(String email) {
        //String query = "DELETE FROM "+CUSTOMER_TABLE+" where email = '" + email + "';";

        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = "DELETE FROM " + CUSTOMER_TABLE + " where email = ?;";
            SQLiteStatement stmt = db.compileStatement(query);

            stmt.clearBindings();
            stmt.bindString(1, email);
            stmt.executeUpdateDelete();
            //getWritableDatabase().execSQL(query);
        }catch (Exception ex){
            Log.e("Exception", ex.getMessage(), ex);
        }
    }

    public void deleteCustomerByNumber(String customer_number) {
        //String query = "DELETE FROM "+CUSTOMER_TABLE+" where customer_number = '" + customer_number + "';";

        try {
            SQLiteDatabase db = getWritableDatabase();
            String query = "DELETE FROM " + CUSTOMER_TABLE + " where customer_number = ?;";
            SQLiteStatement stmt = db.compileStatement(query);

            stmt.clearBindings();
            stmt.bindString(1, customer_number);
            stmt.executeUpdateDelete();
        }catch (Exception ex){
            Log.e("Exception", ex.getMessage(), ex);
        }

        //getWritableDatabase().execSQL(query);

    }

}
