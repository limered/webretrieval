(function(WinJS){
	var resultList = {};
	function search2(evt){
		evt.preventDefault();
		search();
	}
	
	function search(evt){ 
		
		var searchString = encodeURI($('#search-input').val());
		if(searchString === "")
			return;
		var baseURL = "/html/search"; 
		var ajaxUrl = baseURL + "?q=" + searchString;
		
		var sortCh = $('#newest-first-checkbox')[0].checked;
		var redditCh = $('#only-reddit-checkbox')[0].checked;
		var twitterCh = $('#only-twitter-checkbox')[0].checked;
		var rssCh = $('#only-rss-checkbox')[0].checked;
		var youCh = $('#only-youtube-checkbox')[0].checked;
		
		var sortUrl = (sortCh) ? "&sort=true" : "";
		
		var filterUrl = (redditCh || twitterCh || rssCh || youCh) 
		? "&filter=" + 
				((redditCh) ? "reddit " : "") + 
				((twitterCh) ? "twitter " : "") +
				((youCh) ? "youtube " : "") +
				((rssCh) ? "rssfeed " : "") : "";
		
		ajaxUrl += sortUrl + filterUrl;
		
		$('#info-area').html("");
		WinJS.xhr({url: ajaxUrl, type:"GET"}).then(function(res){
			resultList.splice(0, resultList.length);
			if(res.status === 200){
				var response = JSON.parse(res.responseText);
				if(response.success){
					showResultsMessage(response.query);
					addToListView(response.items);
				}else 
					showNoResultsMessage(response.query); 
			}
		});
	}

	function showNoResultsMessage(query){
		var text = "Sorry, there are no results for: " + query;
		$('#info-area').html(text);
	}
	
	function showResultsMessage(query){
		var text = "Results for: " + query;
		$('#info-area').html(text);
	}

	function addToListView(itemList){
		itemList.forEach(function(item){
			resultList.push(item);
		});
	}
	
	WinJS.UI.processAll().then(function(){
		$('#search-button').click(search2);
		$('#newest-first-checkbox').click(search);
		$('#only-reddit-checkbox').click(search);
		$('#only-twitter-checkbox').click(search);
		$('#only-rss-checkbox').click(search);
		$('#only-youtube-checkbox').click(search);
		 
		function resultItemTemplate(itemPromise){
			return itemPromise.then(function(item){
				var result = item.data;
				
				var div = document.createElement("div");
				div.className = "result-item";
				if(result.reddit){
					//build reddit
					var link = document.createElement("a");
					link.href = result.reddit.link;
					link.innerText = result.reddit.linkText;
					link.className = "reddit-link line1 win-type-ellipsis";
					
					var date = document.createElement("div");
					date.innerText = new Date(parseInt(result.reddit.date)).toLocaleString();
					date.className = "reddit-date line2 win-type-ellipsis";
					
					var subReddit = document.createElement("a");
					subReddit.href = "http://www.reddit.com/r/" + result.reddit.subreddit;
					subReddit.innerText = "Source: /r/" + result.reddit.subreddit;
					subReddit.className = "reddit-sub line4 win-type-ellipsis";
					
					var comments = document.createElement("a");
					comments.href = result.reddit.clink;
					comments.innerText = "Comments / Permalink";
					comments.className = "reddit-comments line3 win-type-ellipsis";
					
					div.appendChild(link);
					div.appendChild(date);
					div.appendChild(subReddit);
					div.appendChild(comments);
					
					div.className = "reddit result-item";
					
				}else if(result.twitter){
					//build Twitter
					var link = document.createElement("a");
					link.href = result.twitter.link;
					link.innerText = result.twitter.linkText;
					link.className = "twitter-link line1 win-type-ellipsis";
					
					var date = document.createElement("div");
					date.innerText = new Date(parseInt(result.twitter.date)).toLocaleString();
					date.className = "twitter-date line2 win-type-ellipsis";
					
					var content = document.createElement("div");
					content.innerText = result.twitter.shortDecr;
					content.className = "twitter-content line3 win-type-ellipsis";
					
					div.appendChild(link);
					div.appendChild(date);
					div.appendChild(content);
					
					div.className = "twitter result-item";
				}else{
					//build RSS
					var link = document.createElement("a");
					link.href = result.rssfeed.link;
					link.innerText = result.rssfeed.title;
					link.className = "rssfeed-link line1 win-type-ellipsis";
					
					var date = document.createElement("div");
					date.innerText = new Date(parseInt(result.rssfeed.date)).toLocaleString();
					date.className = "rssfeed-date line2 win-type-ellipsis";
					
					var source = document.createElement("div");
					source.innerText = "Source: " + result.rssfeed.website;
					source.className = "rssfeed-sub line4 win-type-ellipsis";
					
					var content = document.createElement("div");
					content.innerText = result.rssfeed.shortDecr;
					content.className = "rssfeed-shortDecr line3 win-type-ellipsis";
					
					div.appendChild(link);
					div.appendChild(date);
					div.appendChild(content);
					div.appendChild(source);
					
					div.className = "rssfeed result-item";
				}
				return div; 
			});
		}
		
		resultList = new WinJS.Binding.List(); 
		var listView = document.querySelector("#result-area").winControl;
		
		listView.itemDataSource = resultList.dataSource;
		listView.itemTemplate = resultItemTemplate.bind(this);//document.querySelector('#result-template');
		listView.layout = new WinJS.UI.ListLayout(); 
		listView.selectionMode = 'none';
		listView.tapBehavior = 'none';
		listView.swipeBehavior = 'none';
		
	});
	
})(WinJS);