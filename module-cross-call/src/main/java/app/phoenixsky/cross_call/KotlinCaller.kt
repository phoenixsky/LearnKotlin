package app.phoenixsky.cross_call

import io.reactivex.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.future
import kotlinx.coroutines.rx2.asObservable
import java.util.concurrent.CompletableFuture

/**
 * @author: Rocky
 * @date: 2021/11/25
 * @Git phoenixsky
 */
object KotlinCaller {

    private suspend fun testSuspend() {
        flow { emit(1) }
    }


    fun testSuspendAsync(): CompletableFuture<Unit> =
        GlobalScope.future { testSuspend() }


    fun <T : Any> toObserve(f: Flow<T>): Observable<T> {
        return f.asObservable()
    }





}
