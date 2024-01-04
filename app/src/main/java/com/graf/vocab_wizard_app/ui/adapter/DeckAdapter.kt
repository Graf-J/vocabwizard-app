package com.graf.vocab_wizard_app.ui.adapter

import com.graf.vocab_wizard_app.viewmodel.deckoverview.DecksViewModel
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.databinding.ItemDeckBinding


class DeckAdapter(
    private var decks: List<DeckResponseDto>,
    private val decksViewModel: DecksViewModel,
    private val view: View?,
) : RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {
    inner class DeckViewHolder(val binding: ItemDeckBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding: ItemDeckBinding = ItemDeckBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val deck = decks[position]

        displayValues(holder, deck)
        addClickListener(holder, deck)
        addLongPressListener(holder, deck)
    }

    private fun displayValues(holder: DeckViewHolder, deck: DeckResponseDto) {
        holder.binding.deckName.text = deck.name
        holder.binding.newCardsCount.text = deck.newCardCount.toString()
        holder.binding.oldCardsCount.text = deck.oldCardCount.toString()
    }

    private fun addClickListener(holder: DeckViewHolder, deck: DeckResponseDto) {
        holder.binding.deckCard.setOnClickListener {
            // Check if there are Cards to learn
            if ((deck.newCardCount + deck.oldCardCount) >= 1) {
                if (isNetworkAvailable()) {
                    val bundle = bundleOf("id" to deck.id)
                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_learnFragment, bundle)
                    }
                } else {
                    Toast.makeText(view!!.context, view.context.getString(R.string.noInternetConnection), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(view!!.context, view.context.getString(R.string.noCards), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addLongPressListener(holder: DeckViewHolder, deck: DeckResponseDto) {
        holder.binding.deckCard.setOnLongClickListener {
            val popupMenu = createPopupMenu(holder, deck)
            popupMenu.show()

            true
        }
    }

    private fun createPopupMenu(holder: DeckViewHolder, deck: DeckResponseDto): PopupMenu {
        val popupMenu = PopupMenu(holder.itemView.context, holder.binding.deckCard)
        popupMenu.menuInflater.inflate(R.menu.deck_popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.addCard) {
                val bundle = Bundle().apply {
                    putString("id", deck.id)
                    putString("fromLang", deck.fromLang)
                }
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_addCardFragment, bundle)
                }
            }  else if (menuItem.itemId == R.id.copyId) {
                copyToClipboard(holder, deck.id)
                Toast.makeText(view!!.context, view.context.getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show()
            } else if (menuItem.itemId == R.id.statistics) {
                val bundle = Bundle().apply {
                    putString("id", deck.id)
                }
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_statisticsFragment, bundle)
                }
            } else if (menuItem.itemId == R.id.updateDeck) {
                val bundle = Bundle().apply {
                    putString("id", deck.id)
                    putString("name", deck.name)
                    putString("fromLang", deck.fromLang)
                    putString("toLang", deck.toLang)
                    putInt("learningRate", deck.learningRate)
                }
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_updateDeckFragment, bundle)
                }
            } else if (menuItem.itemId == R.id.reverseDeck) {
                decksViewModel.reverseDeck(deck.id)
            } else if (menuItem.itemId == R.id.deleteDeck) {
                openDeleteDeckModal(holder, deck.id, deck.name)
            }

            true
        }

        return popupMenu
    }

    private fun openDeleteDeckModal(holder: DeckViewHolder, deckId: String, deckName: String) {
        // Show Delete Dialog
        val dialog = AlertDialog.Builder(holder.itemView.context)
            .setTitle(holder.itemView.context.getString(R.string.delete_deck))
            .setMessage(holder.itemView.context.getString(R.string.sure_about_delete) + " $deckName")
            .setPositiveButton(holder.itemView.context.getString(R.string.delete)) { _, _ ->
                decksViewModel.deleteDeck(deckId)
                // Remove deck from RecyclerView
                decks = decks.filter { it.id != deckId }
                notifyDataSetChanged()
            }
            .setNegativeButton(holder.itemView.context.getString(R.string.cancel)) { _, _ -> }
            .create()

        dialog.show()
    }

    private fun copyToClipboard(holder: DeckViewHolder, deckId: String) {
        val clip = ClipData.newPlainText("deckId", deckId)
        getSystemService(
            holder.itemView.context,
            ClipboardManager::class.java
        )?.setPrimaryClip(clip)
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = view!!.context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}