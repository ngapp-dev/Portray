package com.ngapp.portray.utils

import android.app.Activity
import android.content.Context
import android.os.*
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ngapp.portray.R
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

fun <T : ViewBinding> ViewGroup.inflate(
    inflateBinding: (
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean
    ) -> T, attachToRoot: Boolean = false
): T {
    val inflater = LayoutInflater.from(context)
    return inflateBinding(inflater, this, attachToRoot)
}

fun Activity.hideKeyboardAndClearFocus() {
    val view = currentFocus ?: View(this)
    hideKeyboardFrom(view)
    view.clearFocus()
}

fun Context.hideKeyboardFrom(view: View) {
    getSystemService(Activity.INPUT_METHOD_SERVICE)
        .let { it as InputMethodManager }
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T : Fragment> T.toast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T : Fragment> T.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun <T : Fragment> T.withArguments(action: Bundle.() -> Unit): T {
    return apply {
        val args = Bundle().apply(action)
        arguments = args
    }
}

fun haveM(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

fun haveN(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}

fun haveQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun haveO(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}

fun haveR(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun setText(context: Context, textView: TextView, stringResource: Int, text: String?) {
    if (text.isNullOrEmpty().not()) {
        textView.isGone = false
        textView.text = context.resources.getString(stringResource, text)
    } else {
        textView.isGone = true
    }
}

fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(u.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
    }
    text = spannable
}

suspend fun <T> getResponse(
    request: suspend () -> Response<T>,
    defaultErrorMessage: String,
    retrofit: Retrofit,
    context: Context
): FetchResult<T> {
    return try {
        println("I'm working in thread ${Thread.currentThread().name}")
        val result = request.invoke()
        when {
            result.isSuccessful -> {
                return FetchResult.success(result.body())
            }
            result.code() == 401 -> {
                FetchResult.error(context.getString(R.string.auth_error), null)
            }
            result.code() == 403 -> {
                FetchResult.error(context.getString(R.string.rate_limit_exceeded), null)
            }
            result.code() == 404 -> {
                FetchResult.error(context.getString(R.string.resource_not_found), null)
            }
            else -> {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                FetchResult.error(
                    errorResponse?.status_message ?: defaultErrorMessage,
                    errorResponse
                )
            }
        }

    } catch (e: IOException) {
        FetchResult.error("Internet connection error", null)
    } catch (e: Throwable) {
        FetchResult.error("Unknown Error", null)
    }
}

abstract class DoubleClickListener : View.OnClickListener {
    private val DEFAULT_QUALIFICATION_SPAN = 200L
    private var isSingleEvent = false
    private val doubleClickQualificationSpanInMillis =
        DEFAULT_QUALIFICATION_SPAN
    private var timestampLastClick = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: () -> Unit = {
        if (isSingleEvent) {
            onSingleClick()
        }
    }

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - timestampLastClick < doubleClickQualificationSpanInMillis) {
            isSingleEvent = false
            handler.removeCallbacks(runnable)
            onDoubleClick()
            return
        }
        isSingleEvent = true
        handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN)
        timestampLastClick = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick()
    abstract fun onSingleClick()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
