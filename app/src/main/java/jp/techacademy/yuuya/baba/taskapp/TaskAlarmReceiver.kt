package jp.techacademy.yuuya.baba.taskapp

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import io.realm.Realm

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //SDKバージョンが26以上の時チャネルを設定する必要がある
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "default",
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "channel descripsion"
            notificationManager.createNotificationChannel(channel)
        }

        //通知の設定をする
        val builder = NotificationCompat.Builder(context, "default")
        builder.setSmallIcon(R.drawable.small_icon)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.large_icon))
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)

        //EXTRA_TASKからTaskのidを取得して、idからTaskのインスタンスを取得する.
        val taskId = intent!!.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        val task = realm.where(Task::class.java).equalTo("id", taskId).findFirst()



        //Taskの情報を設定する。
        builder.setTicker(task!!.title)     //5.0以降は表示されない。
        builder.setContentTitle(task.title)
        builder.setContentText(task.contents)

        //通知をタップしたらアプリを起動するようにする。
        val startAppIntent = Intent(context, MainActivity::class.java)
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(context, 0, startAppIntent, 0)
        builder.setContentIntent(pendingIntent)

        //通知を表示する。
        notificationManager.notify(task!!.id, builder.build())
        realm.close()


    }
}