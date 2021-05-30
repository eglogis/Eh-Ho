package io.keepcoding.eh_ho.topics

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.databinding.ViewTopicBinding
import io.keepcoding.eh_ho.databinding.ViewTopicBumpedBinding
import io.keepcoding.eh_ho.databinding.ViewTopicPinedBinding
import io.keepcoding.eh_ho.extensions.inflater
import io.keepcoding.eh_ho.model.Topic

class TopicsAdapter(diffUtilItemCallback: DiffUtil.ItemCallback<Topic> = DIFF) :
    ListAdapter<Topic, RecyclerView.ViewHolder>(diffUtilItemCallback) {

    companion object {
        private const val TOPIC_NORMAL_CELL = 0
        private const val BUMPED_TOPIC_CELL = 1
        private const val PINED_TOPIC_CELL = 2

        val DIFF = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        val topic = currentList[position]
        return when {
            topic.pinned -> PINED_TOPIC_CELL
            topic.bumped -> BUMPED_TOPIC_CELL
            else -> TOPIC_NORMAL_CELL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PINED_TOPIC_CELL -> PinedTopicViewHolder(parent)
            BUMPED_TOPIC_CELL -> BumpedTopicViewHolder(parent)
            else -> TopicViewHolder(parent)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = run {
        when (holder) {
            is TopicViewHolder -> holder.bind(getItem(position))
            is BumpedTopicViewHolder -> holder.bind(getItem(position))
            is PinedTopicViewHolder -> holder.bind(getItem(position))
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
            binding.user.text = topic.user
            binding.replyNum.text = topic.replyCount.toString()
            binding.likeNum.text = topic.likeCount.toString()
        }
    }

    class BumpedTopicViewHolder(
        parent: ViewGroup,
        private val binding: ViewTopicBumpedBinding = ViewTopicBumpedBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.title.text = topic.title
            binding.user.text = topic.user
            binding.replyNum.text = topic.replyCount.toString()
            binding.likeNum.text = topic.likeCount.toString()
        }
    }

    class PinedTopicViewHolder(
        parent: ViewGroup,
        private val binding: ViewTopicPinedBinding = ViewTopicPinedBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.title.text = topic.title
        }
    }
}