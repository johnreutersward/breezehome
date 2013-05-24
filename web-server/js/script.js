$(document).ready(function () {
    updateQueue();

    function get_parameters() {
        var url = $(location).attr('href');
        var n = url.indexOf("?isadmin=True");
        var a;
        if (n < 0) {
            a = "";
        }
        else {
            a = url.substring(n, url.lenght);
        }
        return a;
    }

    //Controlling play/pause button.
    var isPlaying = false;  
    $("#play-pause").click(function() {
        a = get_parameters();
        if (!isPlaying) {
            $.post('/play' + a, function(data) {
                $("#current-song").empty().append(data);
                $("#play-pause-icon i").removeClass("icon-play").addClass("icon-pause");
                isPlaying = true;
            });
        } else {
            $.post('/pause' + a, function(data) {
                $("#current-song").empty().append(data);
                $("#play-pause-icon i").removeClass("icon-pause").addClass("icon-play");
                isPlaying = false;
            });
        }
    });

	$("#next").click(function(){
        a = get_parameters();
        $.post('/next' + a, function(data) {
            updateQueue();
        });
    });

	$("#back").click(function(){
        a = get_parameters();
        $.post('/back', function(data) {
            updateQueue();
        });
    });

    $("#queue-link").click(function(){
        updateQueue();
    });

    $("#search").click(function(){
        a = get_parameters();
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

    $("#authorize").click(function(){
        authorize();
    });

    $("#deny").click(function(){
        deny();
    });

    function changeTrack(nr) {
        $.post('/playNumber/'+nr, function(data){
            updateQueue();
        });
    }


    //Updates the current Queue in the media player
    function updateQueue() {
        $.post('/queue', function(queue) {
            $("#queue-table").empty();
            jQuery.each(queue, function(index, song) {
                $("#queue-table").append("<tr><td><a class='songlist' value=" +index+ " href='#'>" + song + "</a></td></tr>");
            });
            $(".songlist").click(function(){
                var ind = $(this).attr("value");
                changeTrack(ind);
            });
        });
    }

    function getCurrentSong() {
        $.post('/getCurrentSong', function(song) {
            $("current-song").val(song);
        });
    }


    function authorize() {
        var a = get_parameters();
        $.post('/authorize' + a, function(data){
            alert(data);
        });
    }

    function deny() {
        var a = get_parameters();
        $.post('/deny' + a, function(data){
            alert(data);
        });
    }

}); //search-table