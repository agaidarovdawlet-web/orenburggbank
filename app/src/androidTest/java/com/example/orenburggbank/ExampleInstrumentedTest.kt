/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class ExampleInstrumentedTest {
    /** Проверяет, что инструментальный тест запускается в пакете портала техработ. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.orenburggbank", appContext.packageName)
    }
}
