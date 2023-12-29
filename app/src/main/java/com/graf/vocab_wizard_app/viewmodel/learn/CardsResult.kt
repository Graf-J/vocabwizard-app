import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto

sealed class CardsResult() {
    data class SUCCESS(
        val cards: List<CardResponseDto>
    ) : CardsResult()
    data object LOADING: CardsResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : CardsResult()
}
