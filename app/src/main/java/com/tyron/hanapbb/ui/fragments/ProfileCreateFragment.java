package com.tyron.hanapbb.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tyron.hanapbb.R;
import com.tyron.hanapbb.ui.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreateFragment extends Fragment {

    public static int GENDER_FEMALE = 0;
    public static int GENDER_MALE = 1;

    boolean male_button_state = false;
    boolean female_button_state = false;
    boolean male_interest_state = false;
    boolean female_interest_state = false;
    private int gender = -1;
    private int preferred_gender = -1;
    private int page;
    private long last_text_edit = 0;

    private final FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = _firebase.getReference("users");
    private final FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private final StorageReference up = _firebase_storage.getReference("users");


    private TextInputEditText edittext_username, edittext_name, edittext_age;
    private TextInputLayout textinput_username, textinput_name, textinput_age;

    private CircleImageView profile;

    private boolean isUsernameAvailable = false;
    private boolean imagePicked = false;
    private Uri profileUri;

    public ProfileCreateFragment() {

    }

    public static ProfileCreateFragment newInstance(int position) {
        ProfileCreateFragment fragment = new ProfileCreateFragment();
        Bundle args = new Bundle();
        args.putInt("page", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        switch (page) {
            case 1:
                view = inflater.inflate(R.layout.fragment_profile_create_2, container, false);
                initDetailsView(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_profile_create_3, container, false);
                initProfileSelectView(view);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_profile_create, container, false);
                initGenderSelect(view);
        }

        return view;
    }

    private void initProfileSelectView(View view) {
        profile = view.findViewById(R.id.circleImageView);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        profile.setOnClickListener((v) -> startActivityForResult(Intent.createChooser(intent, "Select profile picture"), 1022));
    }

    private void initDetailsView(View view) {

        long delay = 1500L;

        Pattern regex = Pattern.compile("[!@#$%^&*():;<>,.?/'']");

        edittext_username = view.findViewById(R.id.edittext_username);
        edittext_name = view.findViewById(R.id.edittext_name);
        edittext_age = view.findViewById(R.id.edittext_age);

        textinput_name = view.findViewById(R.id.textinput_name);
        textinput_username = view.findViewById(R.id.textinput_username);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable usernameCheck = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                ref.orderByChild("username").equalTo(edittext_username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isUsernameAvailable = false;
                            textinput_username.setError(getString(R.string.username_taken));
                        } else {
                            textinput_username.setErrorEnabled(false);
                            textinput_username.setHelperText(getString(R.string.username_available));
                            isUsernameAvailable = true;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };

        edittext_username.addTextChangedListener((new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(usernameCheck);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (regex.matcher(s).find()) {
                        textinput_username.setError(getString(R.string.invalid_username));
                    } else {
                        textinput_username.setErrorEnabled(false);
                        last_text_edit = System.currentTimeMillis();
                        handler.postDelayed(usernameCheck, delay);
                        textinput_username.setHelperText(getString(R.string.username_checking));
                    }
                }
            }
        }));
    }

    private void initGenderSelect(View view) {
        CardView male_button = view.findViewById(R.id.button_male);
        CardView female_button = view.findViewById(R.id.button_next);
        CardView male_button_interest = view.findViewById(R.id.button_interest_male);
        CardView female_button_interest = view.findViewById(R.id.button_interest_female);

        male_button.setOnClickListener((v) -> {
            try {
                ((onGenderSelectedListener) getActivity()).onGenderSelect(GENDER_MALE);
                gender = GENDER_MALE;
            } catch (ClassCastException ignored) {
            }
            animate(male_button, !male_button_state);
            if (female_button_state) {
                animate(female_button, false);
                female_button_state = false;
            }
            male_button_state = !male_button_state;
        });

        female_button.setOnClickListener((v1) -> {
            try {
                ((onGenderSelectedListener) getActivity()).onGenderSelect(GENDER_FEMALE);
                gender = GENDER_FEMALE;
            } catch (ClassCastException ignored) {
            }
            animate(female_button, !female_button_state);
            if (male_button_state) {
                animate(male_button, false);
                male_button_state = false;
            }
            female_button_state = !female_button_state;
        });

        male_button_interest.setOnClickListener((v) -> {
            try {
                ((onGenderSelectedListener) getActivity()).onGenderPreferredSelect(GENDER_MALE);
                preferred_gender = GENDER_MALE;
            } catch (ClassCastException ignored) {
            }
            animate(male_button_interest, !male_interest_state);
            if (female_interest_state) {
                animate(female_button_interest, false);
                female_interest_state = false;
            }
            male_interest_state = !male_interest_state;
        });

        female_button_interest.setOnClickListener((v1) -> {
            try {
                ((onGenderSelectedListener) getActivity()).onGenderPreferredSelect(GENDER_FEMALE);
                preferred_gender = GENDER_FEMALE;
            } catch (ClassCastException ignored) {
            }
            animate(female_button_interest, !female_interest_state);
            if (male_interest_state) {
                animate(male_button_interest, false);
                male_interest_state = false;
            }
            female_interest_state = !female_interest_state;
        });
    }

    private void animate(View v1, boolean down) {
        if (down) {
            v1.animate()
                    .scaleX(0.9f)
                    .setDuration(100L)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            v1.animate()
                    .scaleY(0.9f)
                    .setDuration(100L)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            v1.animate()
                    .scaleX(1.0f)
                    .setDuration(100L)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            v1.animate()
                    .scaleY(1.0f)
                    .setDuration(100L)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
        }
    }

    public boolean isDoneTyping() {
        return edittext_age.length() + edittext_name.length() > 0 && isUsernameAvailable;
    }

    public boolean pictureSelected() {
        return imagePicked;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1022) {
            imagePicked = true;
            profile.setImageURI(data.getData());
            profileUri = data.getData();
            ((onGenderSelectedListener)getActivity()).onPictureSelected(profileUri);
        }
    }

    public UserModel getModel(){
        UserModel model = new UserModel();
        model.setAge(Integer.parseInt(edittext_age.getText().toString()));
        model.setName(edittext_name.getText().toString());
        model.setUsername(edittext_username.getText().toString());
        return model;
    }

    public interface onGenderSelectedListener {
        void onGenderSelect(int gender);

        void onGenderPreferredSelect(int gender);

        void onPictureSelected(Uri uri);
    }
}
