package com.example.gymapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class TermsAndConditionsDialog extends Dialog {
    private Button agreeButton;
    private CheckBox termsCheckBox;
    private TextView termsContent;

    public TermsAndConditionsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes default dialog title
        setContentView(R.layout.terms_conditions_dialog);

        agreeButton = findViewById(R.id.tc_agree_button);
        termsCheckBox = findViewById(R.id.tc_checkbox);
        termsContent = findViewById(R.id.tc_content);

        // Set the Terms and Conditions content
        termsContent.setText(getTermsAndConditionsText());

        // Disable the "Continue" button initially
        agreeButton.setEnabled(false);

        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Enable the button only if the checkbox is checked
            agreeButton.setEnabled(isChecked);
        });

        agreeButton.setOnClickListener(v -> {
            dismiss(); // Close the dialog
        });
    }

    private String getTermsAndConditionsText() {
        return "Terms and Conditions for Calisthenics App\n" +
                "Effective Date: September 9, 2024\n\n" +
                "Welcome to Calestechsync, a fitness app designed to help you achieve your calisthenics goals. " +
                "By downloading and using the App, you agree to comply with and be bound by the following Terms and Conditions.\n\n" +
                "1. Acceptance of Terms\n" +
                "By accessing or using Calestechsync, you agree to these Terms and Conditions (the 'Terms'), " +
                "including our Privacy Policy, which governs the collection and use of your personal data. " +
                "If you do not agree with any part of these Terms, you must discontinue use of the App immediately.\n\n" +

                "2. Personal Data Collected\n" +
                "In order to provide personalized fitness recommendations and services, we collect the following personal information:\n" +
                "• Name: To identify and personalize your experience in the app.\n" +
                "• Email: Used for account creation, login, and communication regarding the app.\n" +
                "• Gender: Helps tailor exercises and recommendations based on your profile.\n" +
                "• Weight: Used to provide accurate fitness tracking and progress monitoring.\n" +
                "• Height: Collected to calculate metrics like BMI and caloric needs.\n" +
                "• Exercise Focus Goal: Allows us to customize workout plans based on your specific fitness goals (e.g., strength, endurance).\n\n" +

                "3. How We Use Your Data\n" +
                "We use your personal information for the following purposes:\n" +
                "• To Provide and Improve Services: We use your data to personalize your workout experience, track your progress, " +
                "and offer exercise recommendations tailored to your needs.\n" +
                "• Analytics and Performance Monitoring: Your data may be used to analyze app performance and user engagement, " +
                "helping us improve the app’s functionality and features.\n" +
                "• Communications: We may use your email to send important notifications, such as updates to these Terms, " +
                "new features, or account-related information.\n" +
                "• Security and Compliance: Your data is used to maintain the security of the app and ensure compliance with legal requirements.\n\n" +

                "4. Data Security\n" +
                "We are committed to protecting your personal data. We implement reasonable security measures to safeguard " +
                "your information from unauthorized access, disclosure, or misuse. However, no method of transmission over " +
                "the Internet or method of electronic storage is 100% secure, and we cannot guarantee its absolute security.\n\n" +

                "5. Third-Party Access\n" +
                "We do not sell, trade, or otherwise transfer your personal data to outside parties without your consent, " +
                "except for trusted third parties who assist in operating the App (e.g., cloud storage providers, analytics services). " +
                "These parties are required to keep your information confidential.\n\n" +

                "6. User Responsibilities\n" +
                "You are responsible for ensuring the accuracy of the information you provide in the App. " +
                "You must not use the App for any unlawful activities or in a manner that could harm the App, other users, or third parties.\n\n" +

                "7. Age Restriction\n" +
                "The App is intended for users who are 18 years or older. If you are under 18, you must have parental consent to use the App.\n\n" +

                "8. Changes to Terms and Conditions\n" +
                "We reserve the right to modify or update these Terms at any time. Changes will be communicated via email or through the App. " +
                "Your continued use of the App after such modifications constitutes your acceptance of the updated Terms.\n\n" +

                "9. Termination\n" +
                "We reserve the right to terminate or suspend your account at any time, without notice, if we believe you have violated " +
                "these Terms or engaged in unlawful behavior.\n\n" +

                "10. Governing Law\n" +
                "These Terms are governed by and construed in accordance with the laws of [Your Country/State], without regard to its conflict of law principles.\n\n" +

                "11. Contact Us\n" +
                "If you have any questions regarding these Terms and Conditions or the use of your personal data, please contact us at:\n" +
                "Email: calestechsync@gmail.com.\n\n" +

                "By using Calestechsync, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.";
    }

}
