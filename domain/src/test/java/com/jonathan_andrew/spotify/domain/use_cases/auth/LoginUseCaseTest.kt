package com.jonathan_andrew.spotify.domain.use_cases.auth

import com.jonathan_andrew.spotify.domain.entities.Credentials
import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class LoginUseCaseTest {
    @Test
    fun apply_startsWithInProgress() {
        val mockLoginManager = mock(LoginManager::class.java)
        `when`(mockLoginManager.login()).thenReturn(Observable.just(Credentials.Authorized("token")))
        val subject = LoginUseCase(
                mockLoginManager
        )

        val testObserver = Observable.just<LoginAction>(LoginAction())
                .compose(subject)
                .test()
                .apply {
                    assertNoErrors()
                }

        assertTrue(testObserver.values().first() is LoginResult.InProgress)
    }

    @Test
    fun apply_emitsCorrectResultWhenAuthenticated() {
        val mockLoginManager = mock(LoginManager::class.java)
        `when`(mockLoginManager.login()).thenReturn(Observable.just(Credentials.Authorized("token")))
        val subject = LoginUseCase(
                mockLoginManager
        )

        val testObserver = Observable.just<LoginAction>(LoginAction())
                .compose(subject)
                .test()
                .apply {
                    assertNoErrors()
                }

        assertTrue(testObserver.values()[1] is LoginResult.Authenticated)
    }

    @Test
    fun apply_emitsCorrectResultWhenUnauthenticated() {
        val mockLoginManager = mock(LoginManager::class.java)
        `when`(mockLoginManager.login()).thenReturn(Observable.just(Credentials.Unauthorized()))
        val subject = LoginUseCase(
                mockLoginManager
        )

        val testObserver = Observable.just<LoginAction>(LoginAction())
                .compose(subject)
                .test()
                .apply {
                    assertNoErrors()
                }

        assertTrue(testObserver.values()[1] is LoginResult.Unauthenticated)
    }
}