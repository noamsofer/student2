package com.example.studant;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "student_prefs";
    private static final String KEY_HAS_STUDENT = "has_student";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_TERMS = "terms";

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private RadioGroup gradeRadioGroup;
    private Spinner majorSpinner;
    private CheckBox notificationsCheckBox;
    private CheckBox termsCheckBox;
    private TextView resultTextView;

    private TextView firstNameError;
    private TextView lastNameError;
    private TextView emailError;
    private TextView gradeError;
    private TextView majorError;
    private TextView termsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View main = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    Math.max(systemBars.bottom, ime.bottom)
            );
            return insets;
        });

        bindViews();
        showKeyboardWhenFocused(firstNameEditText);
        showKeyboardWhenFocused(lastNameEditText);
        showKeyboardWhenFocused(emailEditText);
        setupMajorSpinner();

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> onLoginClicked());

        Student savedStudent = loadStudent();
        if (savedStudent != null) {
            fillForm(savedStudent);
            showStudent(savedStudent);
        }
    }

    private void showKeyboardWhenFocused(EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            editText.setAutoHandwritingEnabled(false);
        }
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            view.post(() -> WindowCompat.getInsetsController(getWindow(), view)
                    .show(WindowInsetsCompat.Type.ime()));
        });
    }

    private void bindViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        gradeRadioGroup = findViewById(R.id.gradeRadioGroup);
        majorSpinner = findViewById(R.id.majorSpinner);
        notificationsCheckBox = findViewById(R.id.notificationsCheckBox);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        resultTextView = findViewById(R.id.resultTextView);

        firstNameError = findViewById(R.id.firstNameError);
        lastNameError = findViewById(R.id.lastNameError);
        emailError = findViewById(R.id.emailError);
        gradeError = findViewById(R.id.gradeError);
        majorError = findViewById(R.id.majorError);
        termsError = findViewById(R.id.termsError);
    }

    private void setupMajorSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.majors,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(adapter);
    }

    private void onLoginClicked() {
        clearErrors();

        if (!validateInput()) {
            return;
        }

        Student student = createStudentFromForm();
        saveStudent(student);
        showStudent(student);
    }

    private boolean validateInput() {
        boolean isValid = true;

        String firstName = firstNameEditText.getText().toString().trim();
        if (firstName.isEmpty()) {
            firstNameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        String lastName = lastNameEditText.getText().toString().trim();
        if (lastName.isEmpty()) {
            lastNameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailError.setText(R.string.error_email_empty);
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setText(R.string.error_email_invalid);
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (gradeRadioGroup.getCheckedRadioButtonId() == -1) {
            gradeError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (majorSpinner.getSelectedItemPosition() == 0) {
            majorError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!termsCheckBox.isChecked()) {
            termsError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    private void clearErrors() {
        firstNameError.setVisibility(View.GONE);
        lastNameError.setVisibility(View.GONE);
        emailError.setVisibility(View.GONE);
        gradeError.setVisibility(View.GONE);
        majorError.setVisibility(View.GONE);
        termsError.setVisibility(View.GONE);
    }

    private Student createStudentFromForm() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        RadioButton selectedGrade = findViewById(gradeRadioGroup.getCheckedRadioButtonId());
        String grade = selectedGrade.getText().toString();
        String major = majorSpinner.getSelectedItem().toString();
        boolean notifications = notificationsCheckBox.isChecked();
        boolean termsAccepted = termsCheckBox.isChecked();

        return new Student(firstName, lastName, email, grade, major, notifications, termsAccepted);
    }

    private void fillForm(Student student) {
        firstNameEditText.setText(student.getFirstName());
        lastNameEditText.setText(student.getLastName());
        emailEditText.setText(student.getEmail());

        if (getString(R.string.grade_10).equals(student.getGrade())) {
            gradeRadioGroup.check(R.id.grade10RadioButton);
        } else if (getString(R.string.grade_11).equals(student.getGrade())) {
            gradeRadioGroup.check(R.id.grade11RadioButton);
        } else if (getString(R.string.grade_12).equals(student.getGrade())) {
            gradeRadioGroup.check(R.id.grade12RadioButton);
        }

        for (int i = 0; i < majorSpinner.getCount(); i++) {
            if (student.getMajor().equals(majorSpinner.getItemAtPosition(i).toString())) {
                majorSpinner.setSelection(i);
                break;
            }
        }

        notificationsCheckBox.setChecked(student.isNotifications());
        termsCheckBox.setChecked(student.isTermsAccepted());
    }

    private void showStudent(Student student) {
        String notificationsText = student.isNotifications()
                ? getString(R.string.value_yes)
                : getString(R.string.value_no);
        String termsText = student.isTermsAccepted()
                ? getString(R.string.value_yes)
                : getString(R.string.value_no);

        resultTextView.setText(getString(
                R.string.result_student,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getGrade(),
                student.getMajor(),
                notificationsText,
                termsText
        ));
        resultTextView.setVisibility(View.VISIBLE);
    }

    private void saveStudent(Student student) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_HAS_STUDENT, true);
        editor.putString(KEY_FIRST_NAME, student.getFirstName());
        editor.putString(KEY_LAST_NAME, student.getLastName());
        editor.putString(KEY_EMAIL, student.getEmail());
        editor.putString(KEY_GRADE, student.getGrade());
        editor.putString(KEY_MAJOR, student.getMajor());
        editor.putBoolean(KEY_NOTIFICATIONS, student.isNotifications());
        editor.putBoolean(KEY_TERMS, student.isTermsAccepted());
        editor.apply();
    }

    private Student loadStudent() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_HAS_STUDENT, false)) {
            return null;
        }

        return new Student(
                prefs.getString(KEY_FIRST_NAME, ""),
                prefs.getString(KEY_LAST_NAME, ""),
                prefs.getString(KEY_EMAIL, ""),
                prefs.getString(KEY_GRADE, ""),
                prefs.getString(KEY_MAJOR, ""),
                prefs.getBoolean(KEY_NOTIFICATIONS, false),
                prefs.getBoolean(KEY_TERMS, false)
        );
    }
}
