package view;

/**
 * Created by ddopi on 7/9/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.example.ddopikmain.seedapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import presenter.adapter.SelectUserAdapter;
import presenter.pojoClass.SelectUser;

public class PhoneContact extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
        // ArrayList
        ArrayList<SelectUser> selectUsers;
        List<SelectUser> temp;
        // Contact List
        ListView listView;
        // Cursor to load contacts list
        Cursor phones,email;

        // Pop up
        ContentResolver resolver;
        SearchView search;
        SelectUserAdapter adapter;

@Override
protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_contact_activity);

        selectUsers=new ArrayList<SelectUser>();
        resolver=this.getContentResolver();
        listView=(ListView)findViewById(R.id.contacts_list);

    // Check the SDK version and whether the permission is already granted or not.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

    } else {
        // Android version is lesser than 6.0 or the permission is already granted.

        phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        LoadContact loadContact=new LoadContact();
        loadContact.execute();
    }



        search=(SearchView)findViewById(R.id.searchView);

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

@Override
public boolean onQueryTextSubmit(String query){
        // TODO Auto-generated method stub

        return false;
        }

@Override
public boolean onQueryTextChange(String newText){
        // TODO Auto-generated method stub
        adapter.filter(newText);
        return false;
        }
        });
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

                phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
                LoadContact loadContact=new LoadContact();
                loadContact.execute();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

// Load data on background
class LoadContact extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Get Contact list from Phone

        if (phones != null) {
            Log.e("count", "" + phones.getCount());
            if (phones.getCount() == 0) {
                Toast.makeText(PhoneContact.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            }

            while (phones.moveToNext()) {
                Bitmap bit_thumb = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                try {
                    if (image_thumb != null) {
                        bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                    } else {
                        Log.e("No Image Thumb", "--------------");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SelectUser selectUser = new SelectUser();
                selectUser.setThumb(bit_thumb);
                selectUser.setName(name);
                selectUser.setPhone(phoneNumber);
                selectUser.setEmail(id);
                selectUser.setCheckedBox(false);
                selectUsers.add(selectUser);
            }
        } else {
            Log.e("Cursor close 1", "----------------");
        }
        //phones.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        adapter = new SelectUserAdapter(selectUsers, PhoneContact.this);
        listView.setAdapter(adapter);

        // Select item on listclick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("search", "here---------------- listener");

                SelectUser data = selectUsers.get(i);
            }
        });

        listView.setFastScrollEnabled(true);
    }

}

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}