package com.ngapp.portray.ui.profile.view_pager.user_photos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.ItemProfilePhotoBinding
import com.ngapp.portray.utils.DoubleClickListener
import com.ngapp.portray.utils.load


class UserPhotoListAdapter(
    private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
    private val onItemDoubleClick: (photo: Photo) -> Unit,
    private val onLikeImageViewClick: (photo: Photo) -> Unit
) :
    PagingDataAdapter<Photo, UserPhotoListAdapter.PhotosViewHolder>(
        UserPhotosAdapterDiffUtilCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotosViewHolder(
            ItemProfilePhotoBinding.inflate(inflater, parent, false),
            onItemSingleClick,
            onItemDoubleClick,
            onLikeImageViewClick
        )
    }

    override fun onBindViewHolder(
        holder: PhotosViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)
        currentItem?.let { holder.bind(it) }
    }

    class PhotosViewHolder(
        private val binding: ItemProfilePhotoBinding,
        private val onItemSingleClick: (photo: Photo, materialCardView: MaterialCardView) -> Unit,
        private val onItemDoubleClick: (photo: Photo) -> Unit,
        private val onLikeImageViewClick: (photo: Photo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.root.apply {
//                binding.photoImageView.load(photo.urls.regular ?: "", R.drawable.broken_image)
                Glide.with(itemView)
                    .load(photo.urls.regular)
                    .placeholder(R.drawable.broken_image)
                    .into(binding.photoImageView)

                binding.avatarImageView.load(
                    photo.user.profileImage?.small ?: "",
                    R.drawable.ic_user_placeholder
                )
                binding.authorTextView.text = photo.user.name
                binding.accountTextView.text = "@${photo.user.username}"
                binding.likesTextView.text = photo.likes

                if (photo.likedByUser) {
                    binding.likeImageView.setImageResource(R.drawable.ic_like_filled)
                } else {
                    binding.likeImageView.setImageResource(R.drawable.ic_like_outline)
                }

                binding.likeImageView.setOnClickListener {
                    onLikeImageViewClick(photo)
                }

                binding.root.setOnClickListener(object : DoubleClickListener() {
                    override fun onSingleClick() {
                        binding.card.transitionName = photo.blurHash
                        onItemSingleClick(photo, binding.card)
                    }

                    override fun onDoubleClick() {
                        onItemDoubleClick(photo)
                    }
                })
            }
        }
    }

    class UserPhotosAdapterDiffUtilCallBack : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}