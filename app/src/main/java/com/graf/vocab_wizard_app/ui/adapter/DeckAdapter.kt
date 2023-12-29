package com.graf.vocab_wizard_app.ui.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.databinding.ItemDeckBinding
import com.graf.vocab_wizard_app.ui.MainActivity

class DeckAdapter(private var decks: List<DeckResponseDto>, private val view: View?) : RecyclerView.Adapter<DeckAdapter.DeckViewHolder>() {
    inner class DeckViewHolder(val binding: ItemDeckBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding: ItemDeckBinding = ItemDeckBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        // Set the values for the item_todo.xml elements inside the RecyclerView
        val deck: DeckResponseDto = decks[position]
        holder.binding.deckName.text = deck.name
        holder.binding.newCardsCount.text = deck.newCardCount.toString()
        holder.binding.oldCardsCount.text = deck.oldCardCount.toString()

        holder.binding.deckCard.setOnClickListener {
            // Check if there are Cards to learn
            if ((deck.newCardCount + deck.oldCardCount) >= 1) {
                val bundle = bundleOf("id" to deck.id)
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_learnFragment, bundle)
                }
            } else {
                Toast.makeText(view!!.context, view!!.context.getString(R.string.noCards), Toast.LENGTH_SHORT).show()
            }
        }

        holder.binding.deckCard.setOnLongClickListener {
            Log.d("Graf", "Long Press")
            val popupMenu = PopupMenu(holder.itemView.context, holder.binding.deckCard)
            popupMenu.menuInflater.inflate(R.menu.deck_popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.toString() == holder.itemView.context.getString(R.string.update_deck)) {
                    val bundle = bundleOf("id" to deck.id)
                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_updateDeckFragment, bundle)
                    }
                } else if (menuItem.toString() == holder.itemView.context.getString(R.string.add_card)) {
                    // TODO: Navigate to Add Card Fragment
                    Log.d("Graf", "Add another Card")
                }

                true
            }

            popupMenu.show()
            // Return true to consume the long click event
            true
        }
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    fun update(decks: List<DeckResponseDto>) {
        this.decks = decks
    }
}