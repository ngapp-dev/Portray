package com.ngapp.portray.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ngapp.portray.R
import com.ngapp.portray.databinding.FooterLoadStateBinding

class NewLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<NewLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LoadStateViewHolder(
            FooterLoadStateBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams.isFullSpan = true
        }
        holder.bind(loadState)

        holder.itemView.findViewById<Button>(R.id.loadStateRetryButton).setOnClickListener {
            retry.invoke()
        }
    }

    inner class LoadStateViewHolder(
        private val binding: FooterLoadStateBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {

            binding.root.apply {
                binding.loadStateRetryButton.isVisible = loadState !is LoadState.Loading
                binding.loadStateErrorMessageTextView.isVisible = loadState !is LoadState.Loading
            }
//                if (loadState is LoadState.Error) {
//                    binding.loadStateErrorMessageTextView.text = loadState.error.localizedMessage
//                }
        }
    }
}
