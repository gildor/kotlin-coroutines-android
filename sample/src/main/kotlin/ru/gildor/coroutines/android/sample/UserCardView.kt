package ru.gildor.coroutines.android.sample

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.user_card_view.view.*
import ru.gildor.coroutines.android.lifecycle.CoroutineLifecycle
import ru.gildor.coroutines.android.lifecycle.Event
import ru.gildor.coroutines.android.lifecycle.asyncMain
import ru.gildor.coroutines.android.lifecycle.createLifecycle
import ru.gildor.coroutines.retrofit.await
import ru.gildor.sample.R

/**
 * Example of usage CoroutineLifecycle for custom [View]
 */
class UserCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) :
        LinearLayout(context, attrs, defStyleRes),
        /**
         * [View] lifecycle supports only [Event.Destroy] (actually [onDetachedFromWindow])
         */
        CoroutineLifecycle by createLifecycle(listOf(Event.Destroy)) {

    init {
        View.inflate(context, R.layout.user_card_view, this)
    }

    fun loadUser(username: String, api: GitHub) {
        asyncMain {
            val user = api.user(username).await()
            userName.text = "${user.name} @${user.login}"
//            userImage.setImageDrawable(user)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sendEvent(Event.Destroy)
    }
}