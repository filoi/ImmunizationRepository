
(function($) {

	$.fn.tableFreezer = function(param) {
		var defaults = {
			width: 0,
			height: 0,
			freeze:'head',
			scrollWidth:'100%',
			scrollHeightRemove: 0,
			show_scroll_x:true,
			show_scroll_y:true
		};
		var settings = $.extend({}, defaults, param);
		return this.each(function() {
			try{
				var tableObj = this;
				//$( tableObj ).wrapAll( "<div class='slim-scrollbar' style='width:100%;max-height:500px;position:relative;display:inline-block;overflow-y: hidden; overflow-x:hidden;border: 1px solid #d1d5de';padding-top:5px;padding-left:5px; />");
	
			//	$( tableObj ).wrapAll( "<div style='width:100%;max-height:600px;position:relative;display:inline-block;overflow-y: hidden; overflow-x:hidden;border: 1px solid #d1d5de;' />");
	
				$( tableObj ).wrapAll( "<div class='full-height tableScrollableDiv' style='max-width:100%;min-width:"+settings.scrollWidth+";max-height: calc(100vh - "+(170+settings.scrollHeightRemove)+"px) !important;position:relative;display:inline-block;overflow-y: hidden; overflow-x:hidden;border: 1px solid #c4c9d4;' />");
	
				var parentDiv = $(tableObj).parent();
				
				/*if(settings.height==0){
					$(parentDiv).css("max-height","100%");
				}*//*else{
					$(parentDiv).css("overflow-y",'hidden');
				}*/
				/* if(settings.width!=0){
					$(parentDiv).css("max-width",settings.width); 
				}*/
				/*else{
					$(parentDiv).css("overflow-x",'hidden');
				}*/
				if(settings.freeze=='head'){
					$(tableObj).parent().scroll(function(){
						$(tableObj).find("thead tr th").css("top", ($(this).scrollTop()!=0)?$(this).scrollTop()-1:0);
						//$(tableObj).find("thead tr th").css("margin-top","-10px");
					}); 
				}else if(settings.freeze=='column'){
					$(tableObj).parent().scroll(function(){
						$(tableObj).find(".freeze_left_column").css("left",($(this).scrollLeft()!=0)?$(this).scrollLeft()-1:0);
					});
				}else if(settings.freeze=='head_left_column'){
					//$(tableObj).find("tfoot.fixed-tfoot").css("top", $(".tableScrollableDiv").height() - 30);
					$(tableObj).parent().scroll(function(){
						var topPos = ($(this).scrollTop()!=0)?$(this).scrollTop()-1:0;
						$(tableObj).find("thead tr th").css("top", topPos);
						$(tableObj).find("tfoot.fixed-tfoot").css("top", topPos + $(".tableScrollableDiv").height() - 37);
						$(tableObj).find(".freeze_left_column").css("left",($(this).scrollLeft()!=0)?$(this).scrollLeft()-1:0);
					});
				}else if(settings.freeze=='head_right_column'){
					$(tableObj).parent().scroll(function(){
						$(tableObj).find("thead tr th").css("top", $(this).scrollTop()-1);
						$(tableObj).find(".freeze_right_column").css("right", $(this).scrollLeft()-1);
						
					});
				}
				
				/*setTimeout( function(){
				$("td.freeze_left_column").css("border-left","0");
				$("td.freeze_left_column").css("border-right","0");
				$("th.freeze_left_column").css("border-left","0");
				$("th.freeze_left_column").css("border-right","0"); } , 1000 );*/
				$(parentDiv).css("overflow","auto");
				/**
				$(parentDiv).mouseover(function(){
					showScrollBars(parentDiv);
					//$(this).css("overflow","scroll");
					
				});
				$(parentDiv).mouseout(function(){
					//$(this).css("overflow","hidden");
					hideScrollBars(parentDiv);
				});**/
				
				$.each($(tableObj).find("tr"), function(ri,ro){
					var lobj = $("th.freeze_left_column, td.freeze_left_column", ro).last();
					var lcss = lobj.attr("style") + ";border-right: 3px solid #ddd !important";
					lobj.attr("style", lcss);
				});
				
				/**
				lobj = $(tableObj).find("thead tr").last();
				lcss = lobj.attr("style") + ";border-bottom: 1px solid red !important";
				lobj.attr("style", lcss);
				
				$.each($(tableObj).find("tr"), function(ri,ro){
				     var lobj = $("th.freeze_left_column, td.freeze_left_column", ro).last();
				     var lcss = lobj.attr("style") + ";border-right: 3px solid #ddd !important";
				     lobj.attr("style", lcss);
				}); **/
			}
			catch( err ){
				console.log( "Exception in table freezer" );
			}
		});
		
		
		function showScrollBars(parentDiv){
			if(settings.show_scroll_x==true){
				$(parentDiv).css("overflow-x",'scroll');
			}
			if(settings.show_scroll_y==true){
				$(parentDiv).css("overflow-y",'scroll');
			}
		}
		function hideScrollBars(parentDiv){
			if(settings.show_scroll_x==true){
				$(parentDiv).css("overflow-x",'hidden');
			}
			if(settings.show_scroll_y==true){
				$(parentDiv).css("overflow-y",'hidden');
			}
		}

	};
	
	
	
	
})(jQuery);
