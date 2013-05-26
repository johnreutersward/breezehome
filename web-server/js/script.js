$(document).ready(function () {
    
    refresh();

    function refresh(){
        get_status();
        updateQueue();
        get_current();
    }

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

    function get_current() {
        $.post('/getCurrentSong', function(data){
            $("#current-song").empty().append(data);
        });
    }

    function get_status() {
        $.post('/get_status', function(data){
           if(data == "[playing]"){
                $("#play-pause-icon i").removeClass("icon-play").addClass("icon-pause");
            }else{
                $("#play-pause-icon i").removeClass("icon-pause").addClass("icon-play");
            }
        });
    }

    //Controlling play/pause button.
    $("#play-pause").click(function() {
        var a = get_parameters();
        $.post('/toggle' + a, function(data){
            if(data == "denied"){
                $("#status").removeClass().addClass("text-error").text("You're not allowed to do that");
            }else{
                $("#status").removeClass().text("");
            }
            });
        get_status();
    });

	$("#next").click(function(){
        var a = get_parameters();
        $.post('/next' + a, function(data) {
            if(data == "denied"){
                $("#status").removeClass().addClass("text-error").text("You're not allowed to do that");
            }else{
                $("#status").removeClass().text("");
            }
            refresh();
        });
    });

	$("#back").click(function(){
        var a = get_parameters();
        $.post('/back', function(data) {
            refresh();
        });
    });

    $("#queue-link").click(function(){
        refresh();
    });

    $("#search").click(function(){
        var a = get_parameters();
        $.post('/search', {song: $("#search-value").val()}, function(searchList) {
            $("#search-table").empty();
            jQuery.each(searchList, function(index, song) {
                $.get("http://ws.spotify.com/lookup/1/.json?uri=" + song, function(data){
                    console.log(data);
                    var type = data.info.type;
                    var name;
                    if(type == "artist"){
                        name = data.artist.name;
                    }else if(type == "track"){
                        name = data.track.name;
                    }else if(type == "album"){
                        name = data.album.name;
                    }

                    $("#search-table").append("<tr><td>" + type + ": " + name + "<a id=song" + index + " class='btn pull-right add-song' data-uri='"+song+"' href='#'><i class='icon-plus icon-large'></i></a></td></tr>");
                
                    $("#song"+index).click(function() {
                        $.post('/add'+a, {uri: $(this).data("uri")}, function(data) {
                            if(data == "denied"){
                                $("#status").removeClass().addClass("text-error").text("You're not allowed to do that");
                            }else{
                                $("#status").removeClass().addClass("text-success").text(name +" added");
                            }
                            
                        });
                    });
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
            if(data == "denied"){
                $("#status").removeClass().addClass("text-warning").text("You're not allowed to do that");
            }else{
                $("#status").removeClass().text("");
            }
            refresh();
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