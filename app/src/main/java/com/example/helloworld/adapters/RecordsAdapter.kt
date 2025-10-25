package com.example.helloworld.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.R
import com.example.helloworld.data.ScoreRecord
import java.text.SimpleDateFormat
import java.util.*

class RecordsAdapter : ListAdapter<ScoreRecord, RecordsAdapter.RecordViewHolder>(DiffCallback) {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)

        fun bind(record: ScoreRecord, position: Int) {
            tvPlayerName.text = record.playerName
            tvScore.text = "Очки: ${record.score}"
            tvDifficulty.text = "Уровень: ${record.difficultyLevel}"

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            try {
                val date = inputFormat.parse(record.date)
                tvDate.text = outputFormat.format(date)
            } catch (e: Exception) {
                tvDate.text = record.date
            }

            tvPosition.text = "${position + 1}"

            when (position) {
                0 -> tvPosition.setBackgroundResource(R.drawable.gold_medal)
                1 -> tvPosition.setBackgroundResource(R.drawable.silver_medal)
                2 -> tvPosition.setBackgroundResource(R.drawable.bronze_medal)
                else -> tvPosition.setBackgroundResource(R.drawable.default_medal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record, position)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ScoreRecord>() {
        override fun areItemsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord): Boolean {
            return oldItem == newItem
        }
    }
}