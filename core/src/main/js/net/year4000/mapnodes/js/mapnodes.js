/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/** The service to handle pretty much everything with the JS side of MapNodes */
class MapNodes {

  constructor() {
    // Create the injector and inject our self with the injector
    new Injector({
      map_nodes: this,
      event_emitter: new EventEmitter(),
    });
  }

  /** Get the current game */
  get current_game() {
    return Conditions.not_null(this._current_game, '_current_game');
  }

  /** Set the current game and set the current game to the last game */
  set current_game(game) {
    Conditions.not_null(game, 'game');
    this._last_game = this._current_game;
    this._current_game = game;
    this.$injector.child_injector({
      game: this._current_game,
    });
  }
}

const map_nodes = new MapNodes();

// Event Listeners

/**
 The events used for the internal needs of MapNodes.
 They are able to interact with the bindings.
 Note that this file must be known after mapnodes.js.
 */

/** Called when the system has been loaded  */
map_nodes.$event_emitter.on('load', () => {
  println("Loading environment from the Javascript side");
  println('Lodash Version: ' + _.VERSION);
});
