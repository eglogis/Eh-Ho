package io.keepcoding.eh_ho.topics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.databinding.ActivityTopicsBinding
import io.keepcoding.eh_ho.di.DIProvider


class TopicsActivity : AppCompatActivity() {

    private val binding: ActivityTopicsBinding by lazy {
        ActivityTopicsBinding.inflate(
            layoutInflater
        )
    }
    private val topicsAdapter = TopicsAdapter()
    private val vm: TopicsViewModel by viewModels { DIProvider.topicsViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.topics.apply {
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(this@TopicsActivity, LinearLayout.VERTICAL))
        }
        vm.state.observe(this) {
            when (it) {
                is TopicsViewModel.State.LoadingTopics -> {
                    renderLoading(it)
                    binding.swipe.isRefreshing = false
                }
                is TopicsViewModel.State.TopicsReceived -> {
                    binding.emptyText.isVisible = it.topics.isEmpty()
                    topicsAdapter.submitList(it.topics)
                }
                is TopicsViewModel.State.NoTopics -> {
                    binding.emptyText.isVisible = true
                }
            }
        }

        vm.isError.observe(this) {
            if (it) {
                showToastError()
            }
        }

        vm.loadingEvent.observe(this) {
            when (it) {
                is TopicsViewModel.LoadingEvent.StartLoadingEvent -> binding.progress.isVisible =
                    true
                is TopicsViewModel.LoadingEvent.StopLoadingEvent -> binding.progress.isVisible =
                    false
            }
        }

        binding.swipe.setOnRefreshListener(OnRefreshListener { // Esto se ejecuta cada vez que se realiza el gesto
            vm.loadTopics()
        })
    }

    override fun onResume() {
        super.onResume()
        vm.loadTopics()
    }

    private fun renderEmptyState() {
        // Render empty state
    }

    private fun renderLoading(loadingState: TopicsViewModel.State.LoadingTopics) {
        (loadingState as? TopicsViewModel.State.LoadingTopics.LoadingWithTopics)?.let {
            topicsAdapter.submitList(
                it.topics
            )
        }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, TopicsActivity::class.java)
    }

    private fun showToastError() {
        Toast.makeText(this, getString(R.string.request_error_text), Toast.LENGTH_LONG)
            .show()
    }
}