import com.graf.vocab_wizard_app.data.dto.response.CreateDeckResponseDto

sealed class CreateDeckResult() {
    data class SUCCESS(
        val id: CreateDeckResponseDto
    ) : CreateDeckResult()
    data object LOADING: CreateDeckResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : CreateDeckResult()
}
