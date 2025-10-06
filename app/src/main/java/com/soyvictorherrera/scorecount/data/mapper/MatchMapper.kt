package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.MatchEntity
import com.soyvictorherrera.scorecount.domain.model.Match
import com.soyvictorherrera.scorecount.domain.model.Player
import javax.inject.Inject

class MatchMapper @Inject constructor() {

    fun mapFromEntity(entity: MatchEntity): Match {
        return Match(
            id = entity.id.toString(),
            players = Player(id = 0, name = entity.playerOneName) to Player(id = 0, name = entity.playerTwoName),
            score = entity.playerOneScore to entity.playerTwoScore,
            date = entity.date
        )
    }

    fun mapToEntity(domain: Match): MatchEntity {
        return MatchEntity(
            id = domain.id.toLongOrNull() ?: 0,
            playerOneName = domain.players.first.name,
            playerTwoName = domain.players.second.name,
            playerOneScore = domain.score.first,
            playerTwoScore = domain.score.second,
            date = domain.date
        )
    }

}
