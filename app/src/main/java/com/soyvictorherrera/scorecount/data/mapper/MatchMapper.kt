package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.domain.model.Match
import javax.inject.Inject

class MatchMapper
    @Inject
    constructor() {
        fun mapFromEntity(entity: MatchEntity): Match =
            Match(
                id = entity.id.toString(),
                playerOneName = entity.playerOneName,
                playerTwoName = entity.playerTwoName,
                playerOneScore = entity.playerOneScore,
                playerTwoScore = entity.playerTwoScore,
                date = entity.date
            )

        fun mapToEntity(domain: Match): MatchEntity =
            MatchEntity(
                id = domain.id.toLongOrNull() ?: 0,
                playerOneName = domain.playerOneName,
                playerTwoName = domain.playerTwoName,
                playerOneScore = domain.playerOneScore,
                playerTwoScore = domain.playerTwoScore,
                date = domain.date
            )
    }
