$(document).ready(function(){
	$('#search-button').click(search);
});

function search(evt){
	var searchString = encodeURI($('#search-input').val());
	
	var baseURL = "/html/q";
	var ajaxUrl = baseURL + "?" + searchString;
	$('#result-area').html('');
	$.ajax(ajaxUrl).done(function(res){
		resultObject = JSON.parse(res);
		if(resultObject.success){
			// buildResults
			$('#result-area').append(resultObject);
		}else{
			showNoResultsMessage(resultObject.query);
		}
	});
}

function showNoResultsMessage(query){
	var text = "Sorry, there are no results for: " + query;
	$('#info-area').html(text);
}