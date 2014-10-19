$(document).ready(function(){
	$.cookie.raw = true;

		$.getJSON('//'+location.host+'/rest/availability/getdates', function(data){
			console.log(data);
			// initialize 2 empty arrays
			var homeDates = [];
			var awayDates = [];
			
			// traverse through data.event
			for (var i=0; i<data.event.length; i++){
				console.log(data.event[i]);
				if (data.event[i].location=='away'){
					awayDates.push(data.event[i].start);
				}
				else {
					homeDates.push(data.event[i].start);
				}
				
			}
			
			
			$('#availableDatesHome').DatePickerSetDate(homeDates);
			$('#availableDatesAway').DatePickerSetDate(awayDates);
		});		
		
	
		$('#updateDatesButton').click(setDates);
		$('body').keyup(function(e){
			if (e.keyCode == 13) {setDates();}
		});
	
	

	$('#availableDatesHome').DatePicker({
		  mode: 'multiple',
		  dateFormat: "yyyy-mm-dd",
		  calendars: 6,
		  inline: true,
		  current: '1/1/2015'
		});
	
	$('#availableDatesAway').DatePicker({
		  mode: 'multiple',
		  dateFormat: "yyyy-mm-dd",
		  calendars: 6,
		  inline: true,
		  current: '1/1/2015'
		});
	
	
	
});


function setDates(){
	var homeDates = $('#availableDatesHome').DatePickerGetDate()[0];
	var awayDates = $('#availableDatesAway').DatePickerGetDate()[0];
	
	for (var i=0; i<homeDates.length; i++){
		homeDates[i] = homeDates[i].getFullYear() + '/' + new Number(homeDates[i].getMonth()+1) + '/' +  homeDates[i].getDate();
	}
	for (var i=0; i<awayDates.length; i++){
		awayDates[i] = awayDates[i].getFullYear() + '/' + new Number(awayDates[i].getMonth()+1) + '/' +  awayDates[i].getDate();
	}
	
		$.ajax({
	        type: 'POST',
	        url: '//'+location.host+'/rest/availability/setdates',
	        contentType: 'application/json',
	        data: {
	            'datesAvailableHome': JSON.stringify(homeDates),
	            'datesAvailableAway': JSON.stringify(awayDates)
	        },
	        success: function (data, status, jqXHR) {
	        	$.notify('Dates saved', 'success');
	        	},
	        error: function (jqXHR, status) {$.notify('Unable to add dates', 'error');}
	   });
	
}