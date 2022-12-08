@file:Suppress("DEPRECATION")

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.caregiverphase2.R
import com.google.android.material.snackbar.Snackbar

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun darkStatusBar (activity: Activity, color:Int)
{
    val window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = activity.getColor(color)
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun lightStatusBar(activity: Activity,color:Int)
{
    val window: Window = activity.window
    val winParams = window.attributes
    winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
    window.attributes = winParams
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = activity.getColor(color)
}

fun showToast(activity: Activity,message:String)
{
    Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun ViewGroup.inflate(layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this.context).inflate(layoutRes, this, attachToRoot)
}

fun Context.isConnectedToInternet(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnected ?: false
}

fun Activity.hideSoftKeyboard() {
    val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isActive) {
        if (this.currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
    }
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun AlertDialog.invisibleBg() {
    this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}

fun Activity.loadingDialog(cancelable: Boolean = false, lottieFile: Int? = null): AlertDialog {
    val nullParent: ViewGroup? = null
    val inflater = this.layoutInflater
    val alertLayout = inflater.inflate(R.layout.loadingdialog, nullParent)
    val loadingAnimation: LottieAnimationView = alertLayout.findViewById<LottieAnimationView>(R.id.loadingAnimation)

    val loading = AlertDialog.Builder(this)
        .setView(alertLayout)
        .setCancelable(cancelable)
        .create()
    loading.invisibleBg()
    if (lottieFile != null) {
        loadingAnimation.setAnimation(lottieFile)
    }
    return loading
}
