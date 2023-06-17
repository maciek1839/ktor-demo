package com.showmeyourcode.ktor.demo.user

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun `should validate correct email addresses`() {
        assertTrue(isEmailValid("my-name.surname@gmail.com"))
    }

    @Test
    fun `should not validate incorrect email addresses`() {
        assertFalse(isEmailValid("my-name.surname-gmail.com"))
    }
}
