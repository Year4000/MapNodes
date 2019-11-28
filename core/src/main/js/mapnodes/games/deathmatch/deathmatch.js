/*
 * Copyright 2019 Year4000. All Rights Reserved.
 */
import AbstractGameMode from '../abstract_game_mode.js'
import { game_mode } from '../game_registry.js'

/**
 * Deathmatch is your classic team death match, team with the most kills wins or first to react the kill limit.
 */
@game_mode('deathmatch')
export default class Deathmatch extends AbstractGameMode {
}
