package com.example.appointmentsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        Button doneButton = view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(v -> {
            // Close the fragment or handle the done action
            getActivity().getSupportFragmentManager().beginTransaction().remove(SuccessFragment.this).commit();
        });

        return view;
    }
}
