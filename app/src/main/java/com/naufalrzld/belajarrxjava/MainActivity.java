package com.naufalrzld.belajarrxjava;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.TIL_Email)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.TIL_Password)
    TextInputLayout textInputLayoutPassword;
    @BindView(R.id.TIL_ConfirmPassword)
    TextInputLayout textInputLayoutConfirmPassword;

    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.confirmPassword)
    TextInputEditText confirmPassword;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSubmit.setEnabled(false);

        Observable<Boolean> passwordStream = RxTextView.textChanges(password)
                .map(new Function<CharSequence,Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence) throws Exception {
                        return !TextUtils.isEmpty(charSequence) && charSequence.toString().trim().length() < 6;
                    }
                });

        Observable<Boolean> passwordConfirmStream = Observable.merge(
                RxTextView.textChanges(password)
                    .map(new Function<CharSequence, Boolean>() {
                        @Override
                        public Boolean apply(CharSequence charSequence) throws Exception {
                            return !charSequence.toString().trim().equals(confirmPassword.getText().toString());
                        }
                    }),
                RxTextView.textChanges(confirmPassword)
                    .map(new Function<CharSequence, Boolean>() {
                        @Override
                        public Boolean apply(CharSequence charSequence) throws Exception {
                            return !charSequence.toString().trim().equals(password.getText().toString());
                        }
                    })
        );

        Observable<Boolean> emptyFieldStream = Observable.combineLatest(
                RxTextView.textChanges(email)
                        .map(new Function<CharSequence, Boolean>() {
                            @Override
                            public Boolean apply(CharSequence charSequence) throws Exception {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                RxTextView.textChanges(password)
                        .map(new Function<CharSequence, Boolean>() {
                            @Override
                            public Boolean apply(CharSequence charSequence) throws Exception {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                RxTextView.textChanges(confirmPassword)
                        .map(new Function<CharSequence, Boolean>() {
                            @Override
                            public Boolean apply(CharSequence charSequence) throws Exception {
                                return TextUtils.isEmpty(charSequence);
                            }
                        }),
                new Function3<Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean emailEmpty, Boolean passwordEmpty, Boolean confirmPasswordEmpty) throws Exception {
                        return emailEmpty || passwordEmpty || confirmPasswordEmpty;
                    }
                }
        );

        Observable<Boolean> invalidFieldStream = Observable.combineLatest(
                passwordStream,
                passwordConfirmStream,
                emptyFieldStream,
                new Function3<Boolean, Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean emailValid, Boolean aBoolean2, Boolean aBoolean3) throws Exception {
                        return null;
                    }
                }
        );
    }
}
