package io.keepcoding.eh_ho.topics

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.databinding.ViewTopicBinding
import io.keepcoding.eh_ho.extensions.inflater
import io.keepcoding.eh_ho.model.Topic

class TopicsAdapter(diffUtilItemCallback: DiffUtil.ItemCallback<Topic> = DIFF) :
    ListAdapter<Topic, TopicsAdapter.TopicViewHolder>(diffUtilItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder =
        TopicViewHolder(parent)

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem == newItem
        }
    }

    class TopicViewHolder(
        parent: ViewGroup,
        private val binding: ViewTopicBinding = ViewTopicBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.title.text = topic.title
            binding.title.setTextColor(
                if (topic.pinned) ContextCompat.getColor(
                    binding.root.context,
                    R.color.white
                ) else ContextCompat.getColor(binding.root.context, R.color.black)
            )

            binding.user.text = topic.user
            binding.user.isVisible = topic.pinned.not()

            binding.replyNum.text = topic.replyCount.toString()
            binding.replyNum.isVisible = topic.pinned.not()

            binding.likeNum.text = topic.likeCount.toString()
            binding.likeNum.isVisible = topic.pinned.not()

            binding.welcomePinned.isVisible = topic.pinned

            binding.card.setBackgroundColor(
                when {
                    topic.pinned -> {
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.black
                        )
                    }
                    topic.bumped -> {
                        ContextCompat.getColor(binding.root.context, R.color.grey)
                    }
                    else -> {
                        ContextCompat.getColor(binding.root.context, R.color.white)
                    }
                }
            )
        }
    }
}