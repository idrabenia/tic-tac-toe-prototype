(function($) {
    var cometd = $.cometd;

    function initCometd($, cometdBatch) {
        function _connectionEstablished() {
            $('#body').append('<div>CometD Connection Established</div>');
        }

        function _connectionBroken() {
            $('#body').append('<div>CometD Connection Broken</div>');
        }

        function _connectionClosed() {
            $('#body').append('<div>CometD Connection Closed</div>');
        }

        // Function that manages the connection status with the Bayeux server
        var _connected = false;

        function _metaConnect(message) {
            if (cometd.isDisconnected()) {
                _connected = false;
                _connectionClosed();
                return;
            }

            var wasConnected = _connected;
            _connected = message.successful === true;
            if (!wasConnected && _connected) {
                _connectionEstablished();
            }
            else if (wasConnected && !_connected) {
                _connectionBroken();
            }
        }

        // Function invoked when first contacting the server and
        // when the server has lost the state of this client
        function _metaHandshake(handshake) {
            if (handshake.successful === true) {
                cometd.batch(function() {
                    cometdBatch(cometd);
                });
            }
        }

        // Disconnect when the page unloads
        $(window).unload(function() {
            cometd.disconnect(true);
        });

        var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";
        cometd.configure({
                    url: cometURL,
                    logLevel: 'debug'
                });

        cometd.addListener('/meta/handshake', _metaHandshake);
        cometd.addListener('/meta/connect', _metaConnect);

        cometd.handshake();
    }

    function cleanGameBoard() {
        $('#gameTable tr td').each(function() {
            $(this).html('&nbsp;');
        });

        $('#gameInfoBlock span.state').html('&nbsp;');
        $('#gameInfoBlock span.player1').html('&nbsp;');
        $('#gameInfoBlock span.player2').html('&nbsp;');
        $('#gameInfoBlock span.nextStepPlayer').html('&nbsp;');
        $('#gameInfoBlock span.winner').html('&nbsp;');
    }

    $(document).ready(function() {
        initCometd($, function(cometd) {
            cometd.subscribe('/hello', function(message) {
                $('#body').append('<div>Server Says: ' + message.data.greeting + '</div>');
            });

            cometd.subscribe('/game/onUpcoming', onGameUpcoming);
            cometd.subscribe('/game/onStarted', onGameStarted);
            cometd.subscribe('/game/onFinished', onGameFinished);
            cometd.subscribe('/game/onCellMarked', onCellMarked);
            cometd.subscribe('/game/onNextStepPlayerChanged', onNextStepPlayerChanged);
        });

        function onGameUpcoming(message) {
            $('#gameInfoBlock span.state').html(message.data.gameState);
        }

        function onGameStarted(message) {
            $('#gameInfoBlock span.state').html(message.data.gameState);
            $('#gameInfoBlock span.player1').html(message.data.player1);
            $('#gameInfoBlock span.player2').html(message.data.player2);
            $('#gameInfoBlock span.nextStepPlayer').html(message.data.nextStepPlayer);
        }

        function onGameFinished(message) {
            $('#gameInfoBlock span.state').html(message.data.gameState);
            $('#gameInfoBlock span.winner').html(message.data.winner);
            $('#gameTable tr td[data-number=' + message.data.markedCell + ']').html(message.data.markType);
        }

        function onCellMarked(message) {
            $('#gameTable tr td[data-number=' + message.data.markedCell + ']').html(message.data.markType);
        }

        function onNextStepPlayerChanged(message) {
            $('#gameInfoBlock span.nextStepPlayer').html(message.data.nextStepPlayer);
        }

//        $('#newsTextBox input[type=button]').click(function() {
//            // Publish on a service channel since the message is for the server only
//            cometd.publish('/service/hello', { name: $('#newsTextBox textarea').val() });
//        });

        $('#joinGameBlock button').click(function() {
            cleanGameBoard();
            cometd.publish('/service/game/join', {
                command: 'join',
                player: $('#joinGameBlock input').val()
            });
        });

        $('#gameTable tr td').click(function() {
            cometd.publish('/service/game/markCell', {
                command: 'mark',
                player: $('#joinGameBlock input').val(),
                cellNumber: $(this).attr('data-number')
            });
        });
    });
})(jQuery);
