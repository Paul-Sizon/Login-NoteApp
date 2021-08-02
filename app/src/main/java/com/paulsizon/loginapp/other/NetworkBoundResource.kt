package com.paulsizon.loginapp.other

import android.util.Log
import kotlinx.coroutines.flow.*

//Fancy, but a generic way of implementing caching mechanism

//comments here for educational purpose

//Inline improves runtime performance by making compiled function shorter. It is used when lambda is used as a parameter
//crossinline mark is necessary when a lambda parameter is used in local closure
inline fun <ResultType, RequestType> networkBoundResource(
    //how we get data from db
    crossinline query: () -> Flow<ResultType>,
    //get data from api
    crossinline fetch: suspend () -> RequestType,
    //insert data into db
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    //what we want to do if smth goes wrong
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    //we need to decide whether we want to fetch data from api or load it from db
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    //when we emit data the livedata is triggered
    emit(Resource.loading(null))
    //get data from db / 'first' assigns first emission of the flow (list of notes)
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        //firstly we show data from db but once the data is loaded from api we show the latter
        emit(Resource.loading(data))
        try {
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            query().map { Resource.success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            Log.i("DebugApp", t.toString())
            query().map { Resource.error("Could not reach server. It might be down", it) }
        }
    } else {
        //if fetch is not necessary, we return data from db
        query().map { Resource.success(it) }
    }
    //
    emitAll(flow)
}