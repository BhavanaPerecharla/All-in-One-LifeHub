In the MainActivity, the code initializes UI components for username and password input, buttons for login, sign-up, and forgot password. It establishes a connection with a DatabaseHelper for user authentication. The login function validates user credentials using the database and, if successful, saves the session data using SharedPreferences. If credentials are invalid, it displays a toast message. The SignUpPage button redirects users to the registration page, while the ForgotPassword button navigates to the password recovery page. The code facilitates login management, navigation to sign-up, and password recovery.

<img width="260" alt="Screenshot 2024-09-16 at 2 24 15 PM" src="https://github.com/user-attachments/assets/1e4c609f-24c6-4304-8ac1-0c2e94cb00df">

In the SignUpActivity, the code initializes UI components for user input, including fields for username, password, contact details, profile photo, and gender selection via a spinner. It implements an image picker for the profile photo and a date picker for the birthday, which also calculates the user’s age. Basic validations for input fields are performed, including checks for email format and phone number length. The registration process saves user details in the database, ensuring unique username, email, and phone number. The profile picture, if available, is converted to bytes before saving in the database.

![PHOTO-2024-09-16-14-34-50](https://github.com/user-attachments/assets/2eabfa52-0351-4e4a-ac81-59dc73a6522c)

![WhatsApp Image 2024-09-16 at 14 37 41](https://github.com/user-attachments/assets/4460b686-da3c-409a-ac0c-34d5959f61d5)

In the ForgotPasswordActivity, the code initializes UI components for inputting username/email and date of birth, with a date picker for selecting the birthdate. The resetPassword method validates inputs and checks the database for a matching user and date of birth. If a match is found, a dialog prompts the user to set a new password, updating the database upon confirmation. If inputs are empty or do not match, appropriate toast messages are displayed. The activity ensures user identity verification and secure password reset functionality.

![WhatsApp Image 2024-09-16 at 14 41 13](https://github.com/user-attachments/assets/bc7aca35-fd53-4a01-bac2-5f2743c812e1)

![WhatsApp Image 2024-09-16 at 14 41 48](https://github.com/user-attachments/assets/dd68dc06-0d8a-4551-8609-9c5c419cc800)


