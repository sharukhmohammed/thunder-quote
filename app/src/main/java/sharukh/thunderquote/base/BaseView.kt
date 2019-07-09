package sharukh.thunderquote.base

interface BaseView {
    fun showProgress()
    fun hideProgress()

    fun showEmptyView()
    fun showError(error: String)
}