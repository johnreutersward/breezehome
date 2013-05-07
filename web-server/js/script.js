$(document).ready(function () {
    updateQueue();

	$("#play").click(function(){
        $.post('/play_paus', function(data) {
            $("#current").empty().append(data);
        });
    });

	$("#next").click(function(){
        $.post('/next', function(data) {
            updateQueue();
        });
    });

	$("#back").click(function(){
        $.post('/back', function(data) {
            updateQueue();
        });
    });

    $("#queue-link").click(function(){
        updateQueue();
    });

    $("#search").click(function(){
        $.post('/search', {song: $("#search-value").val()}, function(searchList) {
            $("#search-table").empty();
            jQuery.each(searchList, function(index, song) {
                $("#search-table").append("<tr><td>" + song + "<a class='btn pull-right add-song' data-uri='"+song+"' href='#'><i class='icon-plus icon-large'></i></a></td></tr>");
            });
            $(".add-song").click(function(){
                $.post('/add', {uri: $(this).data("uri")}, function(data) {
                    alert(data);
                });
            });
        });
    });

    //Updates the current Queue in the media player
    function updateQueue() {
        $.post('/queue', function(queue) {
            $("#queue-table").empty();
            jQuery.each(queue, function(index, song) {
                $("#queue-table").append("<tr><td>" + song + "</td></tr>");
            });
        });
    }

    function getCurrentSong() {
        $.post('/getCurrentSong', function(song) {
            $("current-song").val(song);
        });
    }
}); //search-table