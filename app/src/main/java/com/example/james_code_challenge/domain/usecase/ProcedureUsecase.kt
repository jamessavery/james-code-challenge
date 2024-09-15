package com.example.james_code_challenge.domain.usecase

import com.example.james_code_challenge.data.model.Procedure
import com.example.james_code_challenge.data.repository.ProcedureRepository
import com.example.james_code_challenge.domain.util.Result
import com.example.james_code_challenge.data.model.ProcedureDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcedureUsecaseImpl @Inject constructor(
    private val procedureRepo: ProcedureRepository
) : ProcedureUsecase {

    override suspend fun getProcedureList(): Flow<Result<List<Procedure>>> {
        return procedureRepo.getProcedureList()
    }

    override suspend fun getProcedureDetail(procedureId: String): Flow<Result<ProcedureDetail>> {
        return procedureRepo.getProcedureDetail(procedureId)
    }

}

interface ProcedureUsecase {
    suspend fun getProcedureList(): Flow<Result<List<Procedure>>>
    suspend fun getProcedureDetail(procedureId: String): Flow<Result<ProcedureDetail>>
}