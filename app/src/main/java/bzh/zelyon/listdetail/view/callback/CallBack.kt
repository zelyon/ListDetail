package bzh.zelyon.listdetail.view.callback

interface CallBack<T> {
    fun onResult(result: T)
    fun onFail(throwable: Throwable)
}