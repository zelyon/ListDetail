package bzh.zelyon.listdetail.utils

interface CallBack<T> {
    fun onResult(result: T)
    fun onFail(throwable: Throwable)
}