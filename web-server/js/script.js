$(document).ready(function () {
	$("#play").click(function(){
        $.post('/play_paus', function(data) {
            //alert(data);
        });
	});

	$("#next").click(function(){
        $.post('/next', function(data) {
            //alert(data);
        });
	});

	$("#back").click(function(){
        $.post('/back', function(data) {
            //alert(data);
        });
	});
});