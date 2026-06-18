package com.axtarget.processnova.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.axtarget.processnova.core.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

/**
 * Gestiona la sesión del usuario usando DataStore.
 * Guarda sessionid, datos del usuario y estado de login.
 */
class SessionManager(private val context: Context) {

    companion object {
        private val KEY_SESSION_ID = stringPreferencesKey(Constants.KEY_SESSION_ID)
        private val KEY_USER_NAME = stringPreferencesKey(Constants.KEY_USER_NAME)
        private val KEY_USER_EMAIL = stringPreferencesKey(Constants.KEY_USER_EMAIL)
        private val KEY_ORG_NAME = stringPreferencesKey(Constants.KEY_ORG_NAME)
        private val KEY_BRANCH_NAME = stringPreferencesKey(Constants.KEY_BRANCH_NAME)
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: ""
    }

    val orgName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ORG_NAME] ?: ""
    }

    val branchName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_BRANCH_NAME] ?: "Principal"
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: ""
    }

    suspend fun saveSession(
        sessionId: String,
        userName: String,
        userEmail: String,
        orgName: String,
        branchName: String = "Principal"
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SESSION_ID] = sessionId
            prefs[KEY_USER_NAME] = userName
            prefs[KEY_USER_EMAIL] = userEmail
            prefs[KEY_ORG_NAME] = orgName
            prefs[KEY_BRANCH_NAME] = branchName
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun getSessionId(): String {
        return context.dataStore.data.first()[KEY_SESSION_ID] ?: ""
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    /**
     * Limpia la sesión de forma síncrona (usado en interceptores).
     */
    fun clearSessionSync() {
        kotlinx.coroutines.runBlocking {
            context.dataStore.edit { it.clear() }
        }
    }
}
