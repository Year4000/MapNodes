/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */

/**
    The events used for the internal needs of MapNodes.
    They are able to interact with the bindings.
*/

/** Called when the system has been loaded  */
map_nodes.$event_emitter.on('load', event => {
    println("Loading environment from the Javascript side");
    println('Lodash Version: ' + _.VERSION);
    println('Immutable Type: ' + typeof Immutable);
});
