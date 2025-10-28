package com.scherzolambda.horarios.testutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    val testScope = TestScope(dispatcher)
    val testCoroutineScope: CoroutineScope get() = testScope

    override fun starting(description: Description?) {
        super.starting(description)
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        // ensure all coroutines completed
        runCatching { testScope.advanceUntilIdle() }
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}
