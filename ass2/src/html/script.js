$(document).ready(function(){
	$('#search-button').click(search);
});

function search(evt){
	var searchString = encodeURI($('#search-input').val());
	
	var baseURL = "/html/q";
	var ajaxUrl = baseURL + "?" + searchString;
	$('#result-area').html('');
	$.ajax(ajaxUrl).done(function(res){
		$('#result-area').append(res);
	});
}