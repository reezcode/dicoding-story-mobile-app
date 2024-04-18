package com.ran.dicodingstory.utils

object ValidationHelper {
        fun checkEmailValid(email: String): String? {
            return if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                null
            } else {
                "Email is not valid"
            }
        }
        fun checkPasswordValid(password: String): String? {
            return if(password.length < 8){
                 "Password must be at least 8 characters"
            } else {
                 null
            }
        }
}
