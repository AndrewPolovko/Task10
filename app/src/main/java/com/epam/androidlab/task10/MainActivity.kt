package com.epam.androidlab.task10

import android.Manifest
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleCursorAdapter


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    val PERMISSION_CONTACTS = 621
    val LOADER_CONTACTS = 622
    val PROJECTION = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.TIMES_CONTACTED)
    val SELECTION = ContactsContract.Data.HAS_PHONE_NUMBER
    val from = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.TIMES_CONTACTED)
    val to = intArrayOf(R.id.name, R.id.times_contacted)
    lateinit var mContactsList: ListView
    lateinit var mProgressBar: ProgressBar
    lateinit var mAdapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContactsList = findViewById(R.id.contacts_list) as ListView
        mProgressBar = findViewById(R.id.activity_progress_bar) as ProgressBar

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_CONTACTS)
        } else {
            loaderManager.initLoader(LOADER_CONTACTS, null, this)
        }

        mAdapter = SimpleCursorAdapter(this, R.layout.contacts_list_item, null, from, to, 0)
        mContactsList.adapter = mAdapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CONTACTS &&
                permissions.isNotEmpty() &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            loaderManager.initLoader(LOADER_CONTACTS, null, this)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        if (id == LOADER_CONTACTS) {
            mProgressBar.visibility = View.VISIBLE
            return CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null)
        } else {
            return null
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        mProgressBar.visibility = View.INVISIBLE
        mAdapter.swapCursor(data)
        mAdapter.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mProgressBar.visibility = View.INVISIBLE
        mAdapter.swapCursor(null)
    }
}
