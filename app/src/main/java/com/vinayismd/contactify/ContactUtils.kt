package com.vinayismd.contactify

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.RawContacts


object ContactUtils {

    fun saveContacts(contentResolver: ContentResolver, contacts: ArrayList<Contact>): Boolean {
        val operations = ArrayList<ContentProviderOperation>()
        for (contact in contacts) {
            val rawContactInsertIndex = operations.size

            // Add a new contact
            operations.add(
                ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Add name to the new contact
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex
                    )
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        contact.firstName + contact.lastName
                    )
                    .build()
            )

            // Add phone number to the new contact
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex
                    )
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, contact.phnNumber)
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build()
            )
        }
        try {
            // Execute the batch of operations
            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
        } catch (e: Exception) {
            // Handle exception here
            e.printStackTrace()
            return false
        }
        return true
    }
}
