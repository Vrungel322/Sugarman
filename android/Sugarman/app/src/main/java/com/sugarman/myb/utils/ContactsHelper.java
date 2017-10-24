package com.sugarman.myb.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.HashMap;

/**
 * Created by yegoryeriomin on 9/22/17.
 */

public class ContactsHelper {

  public static HashMap<String, String> getContactList(Context context) {
    ContentResolver cr = context.getContentResolver();
    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
        ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
    String lastnumber = "0";
    HashMap<String, String> contacts = new HashMap<>();

    if (cur.getCount() > 0) {
      while (cur.moveToNext()) {
        String number = null;
        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        if (Integer.parseInt(
            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
          Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id },
              null);
          while (pCur.moveToNext()) {
            number =
                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if (number.equals(lastnumber)) {

            } else {
              lastnumber = number;

              //Log.e("lastnumber ", lastnumber);
              int type =
                  pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
              switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                  //Log.e("Not Inserted", "Not inserted");
                  break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:

                  //Timber.e(name+ " " + lastnumber);
                  contacts.put(name, lastnumber);
                  break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                  //Log.e("Not Inserted", "Not inserted");
                  break;
              }
            }
          }
          pCur.close();
        }
      }
    }

    for (String key : contacts.keySet()) {
      //Timber.e(key + " " + contacts.get(key));
    }
    return contacts;
  }

  public static HashMap<String, String> getContactList(ContentResolver contentResolver) {
    ContentResolver cr = contentResolver;
    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
        ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
    String lastnumber = "0";
    HashMap<String, String> contacts = new HashMap<>();

    if (cur.getCount() > 0) {
      while (cur.moveToNext()) {
        String number = null;
        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        if (Integer.parseInt(
            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
          Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id },
              null);
          while (pCur.moveToNext()) {
            number =
                pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if (number.equals(lastnumber)) {

            } else {
              lastnumber = number;

              //Log.e("lastnumber ", lastnumber);
              int type =
                  pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
              switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                  //Log.e("Not Inserted", "Not inserted");
                  break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:

                  //Timber.e(name+ " " + lastnumber);
                  contacts.put(name, lastnumber);
                  break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                  //Log.e("Not Inserted", "Not inserted");
                  break;
              }
            }
          }
          pCur.close();
        }
      }
    }

    for (String key : contacts.keySet()) {
      //Timber.e(key + " " + contacts.get(key));
    }
    return contacts;
  }
}
