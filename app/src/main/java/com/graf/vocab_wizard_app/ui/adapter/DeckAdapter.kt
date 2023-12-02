package com.graf.vocab_wizard_app.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.databinding.ItemDeckBinding

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
            val bundle = bundleOf("id" to deck.id)
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_learnFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    fun update(decks: List<DeckResponseDto>) {
        this.decks = decks
    }
}