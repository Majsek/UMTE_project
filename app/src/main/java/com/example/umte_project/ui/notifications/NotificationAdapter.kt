package com.example.umte_project.ui.notifications

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.umte_project.R
import com.example.umte_project.data.NotificationEntity
import java.util.Date
import java.util.Locale

class NotificationAdapter(private val notifications: List<NotificationEntity>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textNotificationMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.textNotificationTimestamp)

        fun bind(notification: NotificationEntity) {
            messageText.text = notification.message

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            timestampText.text = sdf.format(Date(notification.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}
