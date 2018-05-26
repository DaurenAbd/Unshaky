package com.example.sanzharaubakir.unshaky.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sanzharaubakir.unshaky.R;
import com.example.sanzharaubakir.unshaky.utils.Utils;


public class SDBooksFragment extends BookFragment implements View.OnClickListener {
    public static final String TAG = "SDBooksFragment";
    private Button browseSDcard;
    public SDBooksFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sd_browsing_view, container,
                false);
        browseSDcard = (Button) rootView.findViewById(R.id.browse_sd_card);
        browseSDcard.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.browse_sd_card:
                if (Utils.checkPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    browseSDcard();
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST);

                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
