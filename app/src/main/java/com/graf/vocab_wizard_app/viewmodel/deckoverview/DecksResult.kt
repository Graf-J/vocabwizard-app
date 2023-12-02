import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto

sealed class DecksResult() {
    data class SUCCESS(
        val decks: List<DeckResponseDto>
    ) : DecksResult()
    data object LOADING: DecksResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : DecksResult()
}
