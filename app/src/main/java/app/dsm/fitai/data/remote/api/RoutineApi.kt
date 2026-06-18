package app.dsm.fitai.data.remote.api

import app.dsm.fitai.data.remote.dto.GenerateRoutineRequestDto
import app.dsm.fitai.data.remote.dto.GenerateRoutineResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RoutineApi {

    @POST("functions/v1/hello")
    suspend fun generateRoutine(
        @Body request: GenerateRoutineRequestDto
    ): GenerateRoutineResponseDto
}