$(document).ready(function () {
    updateQueue();

    //Controlling play/pause button.
    var isPlaying = false;  
    $("#play-pause").click(function() {
        var url = $(location).attr('href');
        var n = url.indexOf("?isadmin=True");
        var a;
        if (n < 0) {
            a = "";
        }
        else {
            a = url.substring(n, url.lenght);
        }
        if (!isPlaying) {
            $.post('/play' + a, function(data) {
                $("#current-song").empty().append(data);
                $("#play-pause-icon").removeClass("icon-play").addClass("icon-pause");
                isPlaying = true;
            });
        } else {
            $.post('/pause' + a, function(data) {
                $("#current-song").empty().append(data);
                $("#play-pause-icon").removeClass("icon-play").addClass("icon-pause");
                isPlaying = false;
            });
        }
    });

	$("#next").click(function(){
        var url = $(location).attr('href');
        var n = url.indexOf("?isadmin=True");
        var a;
        if (n < 0) {
            a = "";
        }
        else {
            a = url.substring(n, url.length);
        }
        $.post('/next' + a, function(data) {
            updateQueue();
        });
    });

	$("#back").click(function(){
        var url = $(location).attr('href');
        var n = url.indexOf("?isadmin=True");
        var a;
        if (n < 0) {
            a = "";
        }
        else {
            a = url.substring(n, url.length);
        }
        $.post('/back', function(data) {
            updateQueue();
        });
    });

    $("#queue-link").click(function(){
        updateQueue();
    });

    $("#search").click(function(){
        var url = $(location).attr('href');
        var n = url.indexOf("?isadmin=True");
        var a;
        if (n < 0) {
            a = "";
        }
        else {
            a = url.substring(n, url.length);
        }
        $.post('/search', {song: $("#search-value").val()}, function(searchList) {
            $("#search-table").empty();
            jQuery.each(searchList, function(index, song) {
                $("#search-table").append("<tr><td>" + song + "<a class='btn pull-right add-song' data-uri='"+song+"' href='#'><i class='icon-plus icon-large'></i></a></td></tr>");
            });
            $(".add-song").click(function(){
                $.post('/add'+a, {uri: $(this).data("uri")}, function(data) {
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