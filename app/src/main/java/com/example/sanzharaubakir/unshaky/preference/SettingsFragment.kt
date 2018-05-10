package com.example.sanzharaubakir.unshaky.preference

import android.preference.PreferenceFragment
import android.os.Bundle
import com.example.sanzharaubakir.unshaky.R


class SettingsFragment: PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_main)
    }
}