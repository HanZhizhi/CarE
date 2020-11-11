package com.space.care.mainFrag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.space.care.R;
import com.space.care.activity.LoginActivity;

/**
 * Created by SPACE on 2017/5/9.
 */

public class UserFrag extends Fragment {
    private Button btnLogin;
    private RelativeLayout rlUnloged;
    private ScrollView svLoged;
    private TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_user,container,false);

        rlUnloged= (RelativeLayout) view.findViewById(R.id.fuser_unloged_relative_layout);
        svLoged= (ScrollView) view.findViewById(R.id.fuser_loged_scroll_view);
        btnLogin= (Button) view.findViewById(R.id.fuser_login_button);
        tvUserName= (TextView) view.findViewById(R.id.fuser_user_name);

        String userName=getActivity().getApplicationContext().getSharedPreferences("CarEProfile", Context.MODE_PRIVATE).getString("userName",null);
        if (userName!=null)
        {
            tvUserName.setText(userName);
            svLoged.setVisibility(View.VISIBLE);
        }
        else
        {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
            rlUnloged.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
